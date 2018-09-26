package co.mainmethod.chop.task

import co.mainmethod.mediautils.MediaKit
import co.mainmethod.mediautils.Parameters
import timber.log.Timber

/**
 * Created by evan on 11/28/17.
 */
object TaskUtils {

    val RESULT_OK = 0
    val RESULT_FAILED = 1

    fun processTaskRequest(taskRequest: TaskRequest): Int {
        Timber.d("Processing task request $taskRequest")
        val mediaKit = MediaKit()
        val parameters = Parameters(
                inputImage = taskRequest.imagePath.toString(),
                inputAudio = taskRequest.audioPath.toString(),
                outputVideo = taskRequest.videoPath.toString(),
                outputWidth = taskRequest.videoWidth,
                outputHeight = taskRequest.videoHeight,
                startSecs = taskRequest.startSecs,
                durationSecs = taskRequest.durationSecs)

        return try {
            val result = mediaKit.execute(parameters)
            Timber.d("MediaKit result $result")

            when (result) {
                0 -> RESULT_OK
                else -> {
                    Timber.w("MediaKit failed with $result")
                    RESULT_FAILED
                }
            }
        } catch (e: Exception) {
            Timber.w(e, "Error processing task request")
            RESULT_FAILED
        }
    }

}