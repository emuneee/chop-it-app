package co.mainmethod.chop.task

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by evan on 11/28/17.
 */
@Parcelize
class TaskRequest(val imagePath: Uri,
                  val audioPath: Uri,
                  val videoPath: Uri,
                  val videoWidth: Int,
                  val videoHeight: Int,
                  val startSecs: Int = 0,
                  val durationSecs: Int) : Parcelable {

    override fun toString(): String =
            "TaskRequest(imagePath='$imagePath', audioPath='$audioPath', videoPath='$videoPath', startMs=$startSecs, durationSecs=$durationSecs)"


    companion object {
        public val OUTPUT_SIZE_AUTO_SCALE = -1
    }
}