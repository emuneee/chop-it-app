package co.mainmethod.chop.data

/**
 * AppData persists application key-value pair data
 * Created by evan on 12/4/17.
 */
interface AppData {

    fun getDeviceSdkLevel(): Int
    fun setDeviceSdkLevel(sdkLevel: Int)

    fun getVersionCode(): Int
    fun setVersionCode(versionCode: Int)

}