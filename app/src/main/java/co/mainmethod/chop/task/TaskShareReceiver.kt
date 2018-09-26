package co.mainmethod.chop.task

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

/**
 * Created by evan on 1/18/18.
 */
class TaskShareReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent == null || intent.action.contentEquals(ACTION_SHARE_FILE)) {
            return
        }
        val data = intent.data
        Timber.d("Sharing file @ $data")
        val shareIntent = Intent(Intent.ACTION_SEND)
                .setDataAndType(data, "video/*")
        val chooserIntent = Intent.createChooser(shareIntent, "Share Chop")
        chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context?.startActivity(chooserIntent)
    }

    companion object {
        val ACTION_SHARE_FILE = "co.mainmethod.chop.intent.SHARE_FILE"
    }
}