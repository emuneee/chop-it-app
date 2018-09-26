package co.mainmethod.chop.create

import android.net.Uri
import co.mainmethod.chop.common.BaseViewModel
import co.mainmethod.chop.util.AudioScanner

/**
 * Created by evan on 9/29/17.
 */
class CreateViewModel : BaseViewModel() {

    var imageFile: Uri? = null
    var audioFile: Uri? = null
    var startMs: Long = -1
    var endMs: Long = -1
    var durationMs: Long = -1
    // the duration of the loaded audio file regardless of zoom
    val audioDurationMs: Long
        get() = audioScanResult!!.durationMs

    var progressMs: Long = -1
    var initialAudioScanResult: AudioScanner.AudioScanResult? = null
    var audioScanResult: AudioScanner.AudioScanResult? = null

    var isPlaying = false
    var isZoomedIn = false

    // used when zoomed in, added to the start and end ms so the times are accurate
    var timeOffset: Long = 0


}