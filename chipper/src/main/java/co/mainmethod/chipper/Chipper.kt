package co.mainmethod.chipper

import io.reactivex.Observable
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Reads the apps logcat logs
 * Created by evan on 2/13/18.
 */
class Chipper {

    fun readLogs(): Observable<String> {

        return Observable.create<String> ({ emitter ->
            var reader: BufferedReader? = null

            try {
                val process = Runtime.getRuntime().exec("logcat")
                reader = BufferedReader(InputStreamReader(process.inputStream))
                var line: String?

                do {
                    line = reader.readLine()

                    if (line != null) {
                        emitter.onNext(line)
                    }
                } while (line != null)

            } catch (e: Exception) {
                emitter.onError(e)
            } finally {
                reader?.close()
                emitter.onComplete()
            }
        })
    }

    fun readLogs(listener: ChipperListener) {
        var reader: BufferedReader? = null

        try {
            val process = Runtime.getRuntime().exec("logcat")
            reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?

            do {
                line = reader.readLine()

                if (line != null) {
                    listener.onNewLine(line)
                }
            } while (line != null)

        } finally {
            reader?.close()
        }
    }

    interface ChipperListener {
        fun onNewLine(newLine: String)
    }
}