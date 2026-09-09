package com.example

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class CrashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val text = TextView(this)
        text.text = "ROXYEFOLLOW"
        text.textSize = 28f
        text.setPadding(40, 100, 40, 40)
        setContentView(text)
    }
}
