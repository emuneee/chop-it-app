package co.mainmethod.chop.app

import android.content.Context
import android.os.Build
import co.mainmethod.chop.BuildConfig
import co.mainmethod.chop.data.AppData
import co.mainmethod.chop.util.NotificationUtil
import co.mainmethod.chop.util.VersionUtil
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by evan on 12/4/17.
 */
class AppUpdateHelper {

    @Inject
    lateinit var appData: AppData

    @Inject
    lateinit var context: Context

    init {
       ChopApp.component.inject(this)
    }

    fun checkForUpdate() {
        val oldSdkLevel = appData.getDeviceSdkLevel()
        val newSdkLevel = Build.VERSION.SDK_INT
        val oldVersionCode = appData.getVersionCode()
        val newVersionCode = BuildConfig.VERSION_CODE

        if (newSdkLevel > oldSdkLevel) {
            onSdkUpdate(oldSdkLevel, newSdkLevel)
        }

        if (newVersionCode > oldVersionCode) {

            if (oldVersionCode == -1) {
                onFirstInstall(newVersionCode)
            }
            onUpdate(oldVersionCode, newVersionCode)
        }

        if (newSdkLevel != oldSdkLevel) {
            appData.setDeviceSdkLevel(newSdkLevel)
        }

        if (newVersionCode != oldVersionCode) {
            appData.setVersionCode(newVersionCode)
        }
    }

    private fun onSdkUpdate(oldSdkLevel: Int, newSdkLevel: Int) {
        Timber.d("onSdkUpdate - old sdk level $oldSdkLevel, new sdk level $newSdkLevel")

        if (oldSdkLevel < Build.VERSION_CODES.O && VersionUtil.isO()) {
            NotificationUtil.createNotificationChannel(context)
        }
    }

    private fun onFirstInstall(versionCode: Int) {
        Timber.d("onFirstInstall - version code $versionCode")
    }

    private fun onUpdate(oldVersionCode: Int, newVersionCode: Int) {
        Timber.d("onUpdate - old version code $oldVersionCode, new version code $newVersionCode")
    }

}