package co.mainmethod.chop.analytics

import android.os.Bundle

/**
 * Created by evan on 2/3/18.
 */
interface AnalyticsTracker {

    fun logEvent(event: Event, param: Pair<Param, String>? = null)
    fun logEvent(event: Event, params: Bundle)

    enum class Event(val label: String) {
        EXPORT_START("export_start"),
        EXPORT_FINISHED("export_finished"),
        EXPORT_FAILED("export_failed"),
        AUDIO_SELECTED("audio_selected"),
        AUDIO_ZOOM("audio_zoom"),
        IMAGE_SELECTED("image_selected"),
        CROP_IMAGE("crop_image")
    }

    enum class Param(val value: String) {
        TIME_ELAPSED("time_elapsed"),
        DURATION("duration")
    }
}