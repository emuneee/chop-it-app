package co.mainmethod.mediautils

/**
 * Represents ffmpeg parameters, this generates a string array of arguments to send to ffmpeg
 * Created by evan on 9/11/17.
 */
class Parameters(private val inputImage: String,
                 private val inputAudio: String,
                 private val outputVideo: String,
                 private val outputHeight: Int,
                 private val outputWidth: Int,
                 private var startSecs: Int = 0,
                 private var durationSecs: Int = 0,
                 private var doLoop: Boolean = false) {

    fun toArray(): Array<String> {
        val params = ArrayList<String>()
        //params.add("-loop 1")

        // input image
        params.add("-i")
        params.add(inputImage)

        // input audio and audio parameters
        if (startSecs > 0) {
            params.add("-ss")
            params.add(startSecs.toDurationString())
        }

        if (durationSecs > 0) {
            params.add("-t")
            params.add(durationSecs.toString())
        }

        params.add("-i")
        params.add(inputAudio)

        // codecs and other default parameters
        params.add("-c:v")
        params.add(VIDEO_CODEC)
        params.add("-c:a")
        params.add(AUDIO_CODEC)
        params.add("-b:a")
        params.add(AUDIO_BITRATE)
        params.add("-threads")
        params.add(MAX_THREADS)

        // output video size
        params.add("-vf")
        params.add("scale=$outputWidth:$outputHeight")

        // output video
        params.add(outputVideo)

        // convert to an array
        val paramsArr = arrayOfNulls<String>(params.size)
        return params.toArray(paramsArr)
    }

    override fun toString(): String {
        return "Parameters(inputImage='$inputImage', inputAudio='$inputAudio', outputVideo='$outputVideo', outputHeight=$outputHeight, outputWidth=$outputWidth, startSecs=$startSecs, durationSecs=$durationSecs, doLoop=$doLoop)"
    }

    companion object {
        private val AUDIO_BITRATE = "192k"
        private val AUDIO_CODEC = "aac"
        private val VIDEO_CODEC = "mpeg4"
        private val MAX_THREADS = "4"
    }
}

fun Int.toDurationString(): String {
    val hours = this / 3600
    val minutes = (this - (hours * 3600)) / 60
    val seconds = this - hours * 3600 - minutes * 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}