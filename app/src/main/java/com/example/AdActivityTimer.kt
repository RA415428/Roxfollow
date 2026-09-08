package com.example

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.os.SystemClock

object AdActivityTimer {
    private const val INTERVAL_MS = 4 * 60 * 1000L

    private var running = false
    private var startedAt = 0L
    private var elapsedBeforePause = 0L

    private val handler = Handler(Looper.getMainLooper())

    private var activity: Activity? = null
    private var onTrigger: (() -> Unit)? = null

    private val tick = object : Runnable {
        override fun run() {
            if (!running) return

            val elapsed = elapsedBeforePause +
                (SystemClock.elapsedRealtime() - startedAt)

            if (elapsed >= INTERVAL_MS) {
                elapsedBeforePause = 0L
                startedAt = SystemClock.elapsedRealtime()
                onTrigger?.invoke()
            }

            handler.postDelayed(this, 1000L)
        }
    }

    fun start(activity: Activity, onTrigger: () -> Unit) {
        this.activity = activity
        this.onTrigger = onTrigger
        running = true
        elapsedBeforePause = 0L
        startedAt = SystemClock.elapsedRealtime()
        handler.removeCallbacks(tick)
        handler.postDelayed(tick, 1000L)
    }

    fun pause() {
        if (!running) return

        elapsedBeforePause += SystemClock.elapsedRealtime() - startedAt
        running = false
        handler.removeCallbacks(tick)
    }

    fun reset() {
        elapsedBeforePause = 0L
        startedAt = SystemClock.elapsedRealtime()
    }

    fun stop() {
        running = false
        handler.removeCallbacks(tick)
        activity = null
        onTrigger = null
        elapsedBeforePause = 0L
    }
}
