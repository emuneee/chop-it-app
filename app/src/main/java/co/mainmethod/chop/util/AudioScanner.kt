package co.mainmethod.chop.util

import android.content.Context
import android.net.Uri
import android.media.MediaExtractor
import timber.log.Timber
import java.nio.ByteBuffer
import android.media.MediaFormat
import io.reactivex.Single
import java.nio.ByteOrder


/**
 * Contains functions for processing audio files
 * Created by evan on 10/9/17.
 */
class AudioScanner(private val context: Context,
                   private val listener: ScanUpdateListener) {

    companion object {
        private const val MAX_SAMPLES = 750
        private const val BUFFER_SIZE = 16_384
        private const val MICROSECS_PER_SEC = 1_000_000
        private const val MICROSECS_PER_MS = 1_000
    }

    private fun scanAudioFileImpl(uri: Uri,
                                  maxSamples: Int = MAX_SAMPLES,
                                  startMs: Long = -1L,
                                  endMs: Long = -1L): AudioScanResult {
        val result: AudioScanResult
        val extractor = MediaExtractor()

        try {
            extractor.setDataSource(context, uri, null)
            val inputBuffer = ByteBuffer.allocate(BUFFER_SIZE)
            val format = extractor.getTrackFormat(0)
            extractor.selectTrack(0)
            val mime = format.getString(MediaFormat.KEY_MIME)
            val durationUs =
                    if (endMs == -1L) {
                        format.getLong(MediaFormat.KEY_DURATION)
                    } else {
                        (endMs - startMs) * MICROSECS_PER_MS
                    }
            val durationMs = durationUs / MICROSECS_PER_MS
            val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            val numSamples = (durationUs / MICROSECS_PER_SEC) * sampleRate
            val durationInterval = durationUs / MAX_SAMPLES
            val scanner = FrequencyScanner()
            val frequencies = ArrayList<Double>(maxSamples)

            Timber.d("Duration interval $durationInterval")
            Timber.d("Number of samples $numSamples")
            Timber.d("Max samples $maxSamples")

            var sampleSize: Int
            var byteArray: ByteArray?
            var timestampUs = 0L
            val maxMaxSamples = Math.min(maxSamples, numSamples.toInt())

            if (startMs > -1L) {
                timestampUs = startMs * MICROSECS_PER_MS
                Timber.d("Seeking to $timestampUs")
                extractor.seekTo(timestampUs, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            }

            val endUs =
                    if (endMs > -1) {
                        endMs * MICROSECS_PER_MS
                    } else {
                        -1
                    }

            do {
                listener.onUpdate(frequencies.size.toDouble() / maxMaxSamples)
                // read the data into the buffer then calculate a frequency
                sampleSize = extractor.readSampleData(inputBuffer, 0)

                if (sampleSize == -1) {
                    break
                }

                byteArray = ByteArray(sampleSize)
                inputBuffer.get(byteArray, 0, sampleSize)
                val samples = ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
                val samplesArray = ShortArray(byteArray.size / 2)
                samples.get(samplesArray)
                frequencies.add(scanner.extractFrequency(samplesArray, sampleRate))

                // skip to the next timestamp
                timestampUs += durationInterval

                if (endUs != -1L && timestampUs > endUs) {
                    Timber.d("Exceeded endUs, ending scanning")
                    break
                }
                extractor.seekTo(timestampUs, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            } while (extractor.advance())
            result = AudioScanResult(sampleRate, mime, durationMs, frequencies)
        } finally {
            extractor.release()
        }
        Timber.d("AudioScanResult $result")
        return result
    }

    fun scanAudioFile(uri: Uri, startMs: Long = -1, endMs: Long = -1): Single<AudioScanResult> {

        return Single.create({ single ->
            try {
                val result = scanAudioFileImpl(uri, startMs = startMs, endMs = endMs)
                single.onSuccess(result)
            } catch (e: Exception) {
                Timber.w("Error scanning audio file")
                single.onError(e)
            }
        })
    }

    class AudioScanResult(val sampleRate: Int,
                          val audioFormatMime: String,
                          val durationMs: Long,
                          val frequencies: List<Double>) {

        override fun toString(): String {
            return "AudioScanResult(sampleRate=$sampleRate, audioFormatMime='$audioFormatMime', " +
                    "durationMs=$durationMs, frequencies=$frequencies)"
        }
    }

    interface ScanUpdateListener {
        fun onUpdate(progress: Double)
    }
}