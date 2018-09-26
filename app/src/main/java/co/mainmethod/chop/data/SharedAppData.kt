package co.mainmethod.chop.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Persists app data to a shared preferences file
 * Created by evan on 12/4/17.
 */
class SharedAppData(context: Context) : AppData {

    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
    }

    override fun getDeviceSdkLevel(): Int = sharedPreferences.getInt(KEY_DEVICE_SDK_LEVEL, -1)

    override fun setDeviceSdkLevel(sdkLevel: Int) {
        sharedPreferences.edit()
                .putInt(KEY_DEVICE_SDK_LEVEL, sdkLevel)
                .apply()
    }

    override fun getVersionCode(): Int = sharedPreferences.getInt(KEY_VERSION_CODE, -1)

    override fun setVersionCode(versionCode: Int) {
        sharedPreferences.edit()
                .putInt(KEY_VERSION_CODE, versionCode)
                .apply()
    }

    companion object {
        val FILENAME = "chop_shared_app_data"
        val KEY_DEVICE_SDK_LEVEL = "device_sdk_level"
        val KEY_VERSION_CODE = "version_code"
    }
}