package co.mainmethod.chop.util

import android.os.Build

/**
 * Created by evan on 12/4/17.
 */
object VersionUtil {

    fun isO() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

}