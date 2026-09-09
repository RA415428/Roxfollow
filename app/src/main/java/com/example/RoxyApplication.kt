package com.example

import android.app.Application
import android.os.Environment
import java.io.File

class RoxyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val handler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                )
                dir.mkdirs()

                File(dir, "roxy_crash.txt").writeText(
                    "THREAD: ${thread.name}\n\n" +
                    "EXCEPTION:\n${throwable.stackTraceToString()}"
                )
            } catch (_: Exception) {
            }

            handler?.uncaughtException(thread, throwable)
        }
    }
}
