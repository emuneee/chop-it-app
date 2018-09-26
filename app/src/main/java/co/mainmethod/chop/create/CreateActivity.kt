package co.mainmethod.chop.create

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.Menu
import co.mainmethod.chop.R
import co.mainmethod.chop.analytics.AnalyticsTracker
import co.mainmethod.chop.app.ChopApp
import co.mainmethod.chop.app.Constants
import co.mainmethod.chop.common.BaseActivity
import co.mainmethod.chop.common.BasePresenter
import co.mainmethod.chop.task.TaskProcessorService
import co.mainmethod.chop.util.*
import co.mainmethod.chop.view.WaveformView
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_create.*
import org.jetbrains.anko.intentFor
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by evan on 9/29/17.
 */
class CreateActivity : BaseActivity<CreatePresenter, CreateView, CreateViewModel>(), CreateView {

    private var audioPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private val pauseDrawable: Drawable by lazy {
        getDrawable(R.drawable.ic_pause)
    }
    private val playDrawable: Drawable by lazy {
        getDrawable(R.drawable.ic_play)
    }

    private val audioScanner: AudioScanner by lazy {
        AudioScanner(this, object: AudioScanner.ScanUpdateListener {
            override fun onUpdate(progress: Double) {
                getPresenter().onAudioScanUpdate(progress)
            }
        })
    }

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    init {
        ChopApp.component.inject(this)
    }

    private val updateThread = object : Runnable {

        override fun run() {
            val player = audioPlayer

            if (player != null) {
                val progress = player.currentPosition.toLong()
                getPresenter().onProgressUpdated(progress)

                if (player.isPlaying) {
                    handler.postDelayed(this, 75)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))

        // click listeners
        image.setOnClickListener { getPresenter().onOpenImageChooserClick() }
        audioOverlay.setOnClickListener { getPresenter().onOpenAudioChooserClick() }
        export.setOnClickListener { getPresenter().onExportClick() }
        removeAudio.setOnClickListener { getPresenter().onRemoveAudioClick() }
        removeImage.setOnClickListener { getPresenter().onRemoveImageClick() }
        play.setOnClickListener { getPresenter().onPlayClick() }
        fastForward.setOnClickListener { getPresenter().onFastForwardClick() }
        fastRewind.setOnClickListener { getPresenter().onFastRewindClick() }
        crop.setOnClickListener { getPresenter().onCropImageClick() }
        zoomIn.setOnClickListener { getPresenter().onZoomInClick() }
        zoomOut.setOnClickListener { getPresenter().onZoomOutClick() }

        audio.waveformListener = object : WaveformView.WaveformListener {
            override fun onStartUpdated(value: Long) {
                getPresenter().onStartPositionUpdated(value)
            }

            override fun onEndUpdated(value: Long) {
                getPresenter().onEndPositionUpdated(value)
            }
        }
        audioOverlay.visible()
        val colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary)
        DrawableCompat.setTint(pauseDrawable, colorPrimary)
        DrawableCompat.setTint(playDrawable, colorPrimary)
    }

    override fun onDestroy() {
        super.onDestroy()
        teardownPlayer()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (data != null) {

            when (requestCode) {
                Constants.REQUEST_CODE_ADD_AUDIO -> {
                    Timber.i("Audio File ${data.data}")
                    analyticsTracker.logEvent(AnalyticsTracker.Event.AUDIO_SELECTED)
                    getPresenter().onAudioFileLoaded(data.data)
                }
                Constants.REQUEST_CODE_ADD_IMAGE -> {
                    Timber.i("Image File ${data.data}")
                    analyticsTracker.logEvent(AnalyticsTracker.Event.IMAGE_SELECTED)
                    getPresenter().onImageFileLoaded(data.data)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)

                    if (resultCode == Activity.RESULT_OK) {
                        val uri = result.uri
                        analyticsTracker.logEvent(AnalyticsTracker.Event.CROP_IMAGE)
                        getPresenter().onImageFileLoaded(uri)
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun startExport() {
        val request = getPresenter()
                .getTaskRequest(FileUtil.generateVideoExportFileName(this))
        startService(intentFor<TaskProcessorService>(TaskProcessorService.PARAM_TASK_REQUEST to request))
    }

    override fun startAudioScan(uri: Uri) {

        subscribe(audioScanner.scanAudioFile(uri)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    getPresenter().onAudioScanResult(result)
                }, { error -> Timber.w(error) }))
    }

    override fun openImageChooser() {
        IntentUtil.openImageChooser(this)
    }

    override fun openAudioChooser() {
        IntentUtil.openAudioChooser(this)
    }

    override fun onAudioScanning() {
        audioOverlay.setImageDrawable(null)
        audioLoading.visible()
        audio.invisible()
    }

    override fun onAudioScanFinished() {
        audioOverlay.invisible()
        audioLoading.invisible()
        audioLoading.progress = 0
        audio.visible()
    }

    override fun showAudioScanError() {
        audioOverlay.visible()
        audioLoading.invisible()
        audioLoading.progress = 0
        audio.invisible()
    }

    override fun showSelectedImage(uri: Uri) {
        imageOverlay.invisible()
        image.setImageURI(uri)
    }

    override fun showSelectedAudio(frequencies: List<Double>, startMs: Long, endMs: Long, durationMs: Long) {
        audio.frequencySamples = frequencies.toTypedArray()
        audio.durationMs = durationMs
        startTime.text = startMs.toTime()
        endTime.text = endMs.toTime()
        audio.progressMs = -1L
    }

    override fun showImageCropper(uri: Uri) {
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this)
    }

    override fun removeSelectedImage() {
        imageOverlay.visible()
        image.setImageDrawable(null)
    }

    override fun removeSelectedAudio() {
        audio.clearFrequencySamples()
        audioOverlay.setImageResource(R.drawable.ic_audio_placeholder)
        audioOverlay.visible()
        audioLoading.invisible()
        audio.invisible()
        play.text = ""
        startTime.text = ""
        endTime.text = ""
    }

    override fun setupPlayer(uri: Uri) {
        audioPlayer = MediaPlayer()
        audioPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        audioPlayer!!.setDataSource(applicationContext, uri)
        audioPlayer!!.prepare()
    }

    override fun teardownPlayer() {

        if (audioPlayer != null) {
            audioPlayer!!.stop()
            audioPlayer!!.release()
            audioPlayer = null
        }
    }

    override fun play(position: Long) {
        audioPlayer?.seekTo(position.toInt())
        audioPlayer?.start()
        play.setCompoundDrawablesRelativeWithIntrinsicBounds(null, pauseDrawable, null, null)
        updateThread.run()
    }

    override fun pause() {
        audioPlayer?.pause()
        play.setCompoundDrawablesRelativeWithIntrinsicBounds(null, playDrawable, null, null)
        handler.removeCallbacks(updateThread)
    }

    override fun seek(position: Long) {
        audioPlayer?.seekTo(position.toInt())
    }

    override fun showAudioMissingError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showImageMissingError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun init(): BasePresenter<CreateView, CreateViewModel> {
        val viewModel = ViewModelProviders.of(this).get(CreateViewModel::class.java)
        val presenter = CreatePresenter(viewModel)
        presenter.attach(this)
        return presenter
    }

    override fun getLayoutResource(): Int = R.layout.activity_create

    override fun updateStart(startMs: Long) {
        startTime.text = startMs.toTime()
    }

    override fun updateEnd(endMs: Long) {
        endTime.text = endMs.toTime()
    }

    override fun updateProgress(progressMs: Long) {
        Timber.d("Progress $progressMs")
        audio.progressMs = progressMs
        play.text = progressMs.toTime()
    }

    override fun zoomIn(uri: Uri, startMs: Long, endMs: Long) {
        subscribe(audioScanner.scanAudioFile(uri, startMs, endMs)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    getPresenter().onAudioScanResult(result, startMs, endMs)
                }, { error ->
                    getPresenter().onAudioScanError()
                    Timber.w(error)
                }))
    }

    override fun zoomError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateAudioScannerProgress(progress: Double) {
        runOnUiThread { audioLoading.progress = (progress * 100).toInt() }
    }
}