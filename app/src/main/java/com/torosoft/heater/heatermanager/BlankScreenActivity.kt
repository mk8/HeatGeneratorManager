package com.torosoft.heater.heatermanager

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent

class BlankScreenActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blank_screen)

        val layoutParams = window.attributes
        layoutParams.screenBrightness = 0.0f
        window.attributes=layoutParams
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        super.dispatchTouchEvent(ev)
        finish()
        return true
    }
}
