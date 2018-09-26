package co.mainmethod.chop.create

import android.net.Uri
import co.mainmethod.chop.common.BasePresenter
import co.mainmethod.chop.task.TaskRequest
import co.mainmethod.chop.util.AudioScanner
import java.util.concurrent.TimeUnit

/**
 * Created by evan on 12/1/17.
 */
class CreatePresenter(viewModel: CreateViewModel) : BasePresenter<CreateView, CreateViewModel>(viewModel) {

    fun getTaskRequest(videoFileUri: Uri): TaskRequest =
            TaskRequest(imagePath = viewModel.imageFile!!,
                    audioPath = viewModel.audioFile!!,
                    videoPath = videoFileUri,
                    videoWidth = 480,
                    videoHeight = 320,
                    startSecs = TimeUnit.MILLISECONDS.toSeconds(viewModel.startMs).toInt(),
                    durationSecs = TimeUnit.MILLISECONDS.toSeconds(viewModel.endMs - viewModel.startMs).toInt())

    fun onOpenImageChooserClick() {
        getView()!!.openImageChooser()
    }

    fun onOpenAudioChooserClick() {
        getView()!!.openAudioChooser()
    }

    fun onRemoveAudioClick() {
        viewModel.audioFile = null
        viewModel.audioScanResult = null
        getView()!!.removeSelectedAudio()
    }

    fun onRemoveImageClick() {
        viewModel.imageFile = null
        getView()!!.removeSelectedImage()
    }

    fun onPlayClick() {

        if (viewModel.isPlaying) {
            getView()!!.pause()
        } else {

            if (viewModel.progressMs > -1 &&
                    viewModel.progressMs >= viewModel.startMs &&
                    viewModel.progressMs < viewModel.endMs) {
                getView()!!.play(viewModel.progressMs)
            } else {
                getView()!!.play(viewModel.startMs)
            }
        }
        viewModel.isPlaying = !viewModel.isPlaying
    }

    fun onFastRewindClick() {

        if (viewModel.isPlaying) {
            getView()!!.seek(Math.max(0, viewModel.progressMs + (-1 * 5_000)))
        }
    }

    fun onFastForwardClick() {

        if (viewModel.isPlaying) {
            getView()!!.seek(Math.min(viewModel.durationMs, viewModel.progressMs + 5_000))
        }
    }

    fun onExportClick() {
        val imageFile = viewModel.imageFile
        val audioFile = viewModel.audioFile
        val view = getView()!!

        when {
            imageFile == null -> view.showImageMissingError()
            audioFile == null -> view.showAudioMissingError()
            else -> view.startExport()
        }
    }

    fun onCropImageClick() {
        val uri = viewModel.imageFile

        if (uri != null) {
            getView()!!.showImageCropper(uri)
        }
    }

    fun onAudioScanResult(result: AudioScanner.AudioScanResult,
                          startMs: Long = -1L,
                          endMs: Long = -1L) {

        if (viewModel.initialAudioScanResult == null) {
            viewModel.initialAudioScanResult = result
        }

        viewModel.audioScanResult = result
        viewModel.startMs =
                if (startMs == -1L) {
                    0
                } else {
                    startMs
                }
        viewModel.endMs =
                if (endMs == -1L) {
                    result.durationMs
                } else {
                    endMs
                }
        viewModel.durationMs =
                if (startMs > -1L && endMs > -1L) {
                    endMs - startMs
                } else {
                    result.durationMs
                }
        getView()!!.showSelectedAudio(result.frequencies,
                viewModel.startMs,
                viewModel.endMs,
                viewModel.durationMs)
        getView()!!.setupPlayer(viewModel.audioFile!!)
        getView()!!.onAudioScanFinished()
    }

    fun onAudioFileLoaded(uri: Uri) {
        viewModel.audioFile = uri
        getView()!!.onAudioScanning()
        getView()!!.startAudioScan(uri)
    }

    fun onAudioScanUpdate(progress: Double) {
        getView()!!.updateAudioScannerProgress(progress)
    }

    fun onAudioScanError() {
        getView()!!.showAudioScanError()
    }

    fun onImageFileLoaded(uri: Uri) {
        viewModel.imageFile = uri
        getView()!!.showSelectedImage(uri)
    }

    fun onStartPositionUpdated(start: Long) {
        viewModel.startMs = start + viewModel.timeOffset
        getView()!!.updateStart(viewModel.startMs)

        if (viewModel.isPlaying && viewModel.startMs > viewModel.progressMs) {
            getView()!!.seek(viewModel.startMs)
        }
    }

    fun onEndPositionUpdated(end: Long) {
        viewModel.endMs = end + viewModel.timeOffset
        getView()!!.updateEnd(viewModel.endMs)

        if (viewModel.isPlaying && viewModel.endMs < viewModel.progressMs) {
            getView()!!.pause()
        }
    }

    fun onProgressUpdated(progressMs: Long) {
        viewModel.progressMs = progressMs

        if (progressMs >= viewModel.endMs) {
            onPlayClick()
        }
        getView()!!.updateProgress(progressMs - viewModel.timeOffset)
    }

    fun onZoomInClick() {

        if (viewModel.startMs > 0 || viewModel.endMs < viewModel.audioDurationMs) {
            viewModel.isZoomedIn = true
            viewModel.timeOffset = viewModel.startMs
            getView()!!.onAudioScanning()
            getView()!!.zoomIn(viewModel.audioFile!!, viewModel.startMs, viewModel.endMs)
        } else {
            getView()!!.zoomError()
        }
    }

    fun onZoomOutClick() {

        if (viewModel.isZoomedIn) {
            viewModel.isZoomedIn = false
            viewModel.timeOffset = 0
            onAudioScanResult(viewModel.initialAudioScanResult!!)
        }
    }
}