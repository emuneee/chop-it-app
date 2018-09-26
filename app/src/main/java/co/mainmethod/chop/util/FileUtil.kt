package co.mainmethod.chop.util

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by evan on 11/28/17.
 */
object FileUtil {

    /**
     * Generates a file name to be used for the video export
     */
    fun generateVideoExportFileName(context: Context): Uri {
        val filename = "export-${System.currentTimeMillis()}.mp4"
        val file = File(context.getExternalFilesDir("exports"), filename).absolutePath
        return Uri.parse(file)
    }

    /**
     * Copies a file from a URI to the cache directory
     */
    fun copyFileToCache(context: Context, uri: Uri): Uri {
        val pathToFile: String
        val extension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(context.contentResolver.getType(uri))
        val filename = "temp-${System.currentTimeMillis()}.$extension"
        val file = File(context.externalCacheDir, filename)
        Timber.d("Copying $uri to ${file.absolutePath}")

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            inputStream = context.contentResolver.openInputStream(uri)
            outputStream = file.outputStream()

            val buffer = ByteArray(1024)
            var length: Int

            do {
                length = inputStream.read(buffer)

                if (length > 0) {
                    outputStream.write(buffer)
                }
            } while(length > 0)
            pathToFile = file.absolutePath
        } catch (e: Exception) {
            Timber.w(e, "Error copying file to cache")
            throw e
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
        return Uri.parse(pathToFile)
    }

}