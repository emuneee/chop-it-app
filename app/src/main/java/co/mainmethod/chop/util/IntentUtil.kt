package co.mainmethod.chop.util

import android.app.Activity
import android.content.Intent
import co.mainmethod.chop.app.Constants

/**
 * Created by evan on 10/7/17.
 */
object IntentUtil {

    fun openImageChooser(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type= "image/*"
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_ADD_IMAGE)
    }

    fun openAudioChooser(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type= "audio/*"
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_ADD_AUDIO)
    }

}