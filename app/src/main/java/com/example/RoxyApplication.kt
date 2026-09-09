package com.example

import android.app.Application
import java.io.File

class RoxyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val oldHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val file = File(getExternalFilesDir(null), "roxy_crash.txt")
                file.writeText(
                    "THREAD: ${thread.name}\n\n" +
                    "EXCEPTION:\n${throwable.stackTraceToString()}"
                )
            } catch (_: Exception) {
            }

            oldHandler?.uncaughtException(thread, throwable)
        }
    }
}
