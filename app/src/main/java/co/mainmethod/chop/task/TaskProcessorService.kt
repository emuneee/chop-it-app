package co.mainmethod.chop.task

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.support.v4.content.FileProvider
import co.mainmethod.chipper.Chipper
import co.mainmethod.chop.R
import co.mainmethod.chop.analytics.AnalyticsTracker
import co.mainmethod.chop.app.ChopApp
import co.mainmethod.chop.util.FileUtil
import co.mainmethod.chop.util.NotificationUtil
import co.mainmethod.chop.util.toSeconds
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import javax.inject.Inject


/**
 * Processes requests to FFPMEG for a encoding task
 * Created by evan on 11/28/17.
 */
class TaskProcessorService : IntentService("TaskProcessorService") {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    private var notificationId: Int = -1

    init {
        ChopApp.component.inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {

        if (intent == null || !intent.extras.containsKey(PARAM_TASK_REQUEST)) {
            Timber.w("No intent or intent doesn't contain required data")
            return
        }

        notificationId = NotificationUtil.NOTIF_ID_TASK_SERVICE + intent.hashCode()
        val request = intent.getParcelableExtra<TaskRequest>(PARAM_TASK_REQUEST)
        Timber.d("Task request received $request")
        analyticsTracker.logEvent(AnalyticsTracker.Event.EXPORT_START)

        // move files to cache for prepare for processing
        try {
            showTaskCopyingFilesNotification()
            val cachedAudioPath = FileUtil.copyFileToCache(this, request.audioPath)
            val cachedImagePath = FileUtil.copyFileToCache(this, request.imagePath)

            // build a new task request object, using the cached paths instead
            showTaskProcessingNotification()
            // start the log reader
            val disposable = Chipper().readLogs()
                    .filter { line -> line.contains("frame") && line.contains("fps") }
                    .subscribeOn(Schedulers.io())
                    .subscribe({ line ->
                        val durationStr = line.substringAfter("time=")
                                .substringBefore("bitrate")
                                .substringBefore(".")
                                .trim()
                        val duration = durationStr.toSeconds()
                        showTaskProcessingNotification(duration, request.durationSecs)
                    }, { error -> Timber.w(error, "Error updating notification") })
            val newRequest = TaskRequest(imagePath = cachedImagePath,
                    audioPath = cachedAudioPath,
                    videoPath = request.videoPath,
                    videoWidth = request.videoWidth,
                    videoHeight = request.videoHeight,
                    startSecs = request.startSecs,
                    durationSecs = request.durationSecs)
            val start = System.currentTimeMillis()
            val result = TaskUtils.processTaskRequest(newRequest)
            disposable.dispose()
            val duration = System.currentTimeMillis() - start
            Timber.d("Task processing took $duration milliseconds")

            when (result) {
                TaskUtils.RESULT_OK -> {
                    showTaskFinishedNotification(request.videoPath)
                    analyticsTracker.logEvent(AnalyticsTracker.Event.EXPORT_FINISHED,
                            Pair(AnalyticsTracker.Param.TIME_ELAPSED, duration.toString()))
                }
                TaskUtils.RESULT_FAILED -> {
                    showTaskFailedNotification()
                    analyticsTracker.logEvent(AnalyticsTracker.Event.EXPORT_FAILED)
                }
            }
        } catch (e: Exception) {
            Timber.w(e, "Error while copying files to path")
        }
    }

    private fun showTaskCopyingFilesNotification() {
        val notification = NotificationUtil.getBuilder(this, R.string.notification_channel_id_task_service)
                .setContentTitle(getString(R.string.notification_title_task_in_progress))
                .setContentText(getString(R.string.notification_text_task_copying))
                .setProgress(0, 0, true)
                .setSmallIcon(R.drawable.ic_stat_chop)
                .build()
        startForeground(notificationId, notification)
    }

    private fun showTaskProcessingNotification(progress: Int = -1, max: Int = -1) {
        val builder = NotificationUtil.getBuilder(this, R.string.notification_channel_id_task_service)
                .setContentTitle(getString(R.string.notification_title_task_in_progress))
                .setContentText(getString(R.string.notification_text_task_processing))
                .setSmallIcon(R.drawable.ic_stat_chop)

        if (progress != -1 && max != -1) {
            builder.setProgress(max, progress, false)
        } else {
            builder.setProgress(0, 0, true)
        }
        startForeground(notificationId, builder.build())
    }

    private fun showTaskFinishedNotification(uri: Uri) {
        stopForeground(true)
        val authority = "co.mainmethod.chop.fileprovider"
        val fileUri = FileProvider.getUriForFile(this, authority, File(uri.toString()))
        Timber.d("Task finished $fileUri")
        val intent = Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_STREAM, fileUri)
                .setType("video/*")
        val pendingIntent = PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationUtil.getBuilder(this, R.string.notification_channel_id_task_service)
                .setContentTitle(getString(R.string.notification_title_task_finished))
                .setContentText(getString(R.string.notification_text_task_finished))
                .setSmallIcon(R.drawable.ic_stat_chop)
                .setContentIntent(pendingIntent)
                .addAction(-1, getString(R.string.notification_action_text_share), pendingIntent)
                .setAutoCancel(true)
                .build()
        NotificationUtil.notify(this, notificationId, notification)
    }

    private fun showTaskFailedNotification() {
        stopForeground(true)
        val notification = NotificationUtil.getBuilder(this, R.string.notification_channel_id_task_service)
                .setContentTitle(getString(R.string.notification_title_task_failed))
                .setContentText(getString(R.string.notification_text_task_failed))
                .setSmallIcon(R.drawable.ic_stat_chop)
                .build()
        NotificationUtil.notify(this, notificationId, notification)
    }

    companion object {
        val PARAM_TASK_REQUEST = "TaskRequest"
    }

}