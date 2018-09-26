package co.mainmethod.chop.create

import android.net.Uri
import co.mainmethod.chop.common.BaseView

/**
 * Created by evan on 12/1/17.
 */
interface CreateView : BaseView {

    fun startExport()

    fun showAudioMissingError()

    fun showImageMissingError()

    fun startAudioScan(uri: Uri)
    fun onAudioScanning()
    fun onAudioScanFinished()
    fun updateAudioScannerProgress(progress: Double)
    fun showAudioScanError()
    fun showSelectedAudio(frequencies: List<Double>, startMs: Long, endMs: Long, durationMs: Long)

    fun openImageChooser()

    fun openAudioChooser()

    fun showSelectedImage(uri: Uri)

    fun removeSelectedImage()

    fun removeSelectedAudio()

    fun updateStart(startMs: Long)

    fun updateEnd(endMs: Long)
    fun showImageCropper(uri: Uri)

    fun play(position: Long)
    fun seek(position: Long)

    fun pause()
    fun setupPlayer(uri: Uri)
    fun teardownPlayer()
    fun updateProgress(progressMs: Long)

    fun zoomIn(uri: Uri, startMs: Long, endMs: Long)
    fun zoomError()
}