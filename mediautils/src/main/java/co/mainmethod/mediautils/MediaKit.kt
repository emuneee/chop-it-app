package co.mainmethod.mediautils

import android.util.Log

/**
 * Created by evan on 9/11/17.
 * Provides an interface in the FFMPEG native implementation
 */
class MediaKit {

    init {

        try {
            System.loadLibrary("avutil")
            System.loadLibrary("swresample")
            System.loadLibrary("avcodec")
            System.loadLibrary("avformat")
            System.loadLibrary("swscale")
            System.loadLibrary("avfilter")
            System.loadLibrary("avdevice")
            System.loadLibrary("mediakit")
        } catch (e: Exception) {
            Log.w("MediaKit", "Error loading libraries", e)
        }
    }

    fun execute(parameters: Parameters): Int {
        return process(parameters.toArray())
    }

    /**
     * Call FFmpeg with specified arguments
     * @param args FFmpeg arguments
     * @return ret_code equal to 0 if success, for handled codes see file ffmpeg_ret_codes in docs
     */
    private fun process(args: Array<String>): Int {
        val params = arrayOfNulls<String>(args.size + 1)

        args.forEachIndexed { index, arg ->
            params[index + 1] = "$arg"
        }
        params[0] = FFMPEG_BINARY
        return run(2, params.requireNoNulls())
    }

    // If loglevel greater then 0 there will VERY BIG AND ANNOYING LOG of ffmpeg working process
    // that however could be very useful in case return code didn't help a lot
    private external fun run(loglevel: Int, args: Array<String>): Int

    companion object {
        private val FFMPEG_BINARY = "ffmpeg"
    }
}