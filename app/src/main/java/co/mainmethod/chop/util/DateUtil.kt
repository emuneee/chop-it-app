package co.mainmethod.chop.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by evan on 11/13/17.
 */
object DateUtils {

    private var dateFormat: SimpleDateFormat? = null

    fun convertSecondsToHMmSs(milliseconds: Long): String {
        val seconds = milliseconds / 1_000
        val s = seconds % 60
        val m = seconds / 60 % 60
        val h = seconds / (60 * 60) % 24

        return if (h == 0L) {
            String.format("%02d:%02d", m, s)
        } else {
            String.format("%d:%02d:%02d", h, m, s)
        }
    }

    fun convertHhMmSsToSeconds(hhmmss: String): Int {

        if (dateFormat == null) {
            dateFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
            dateFormat!!.timeZone = TimeZone.getTimeZone("UTC")
        }
        return (dateFormat!!.parse(hhmmss).time / 1000L).toInt()
    }
}

fun Long.toTime(): String = DateUtils.convertSecondsToHMmSs(this)
fun String.toSeconds(): Int = DateUtils.convertHhMmSsToSeconds(this)