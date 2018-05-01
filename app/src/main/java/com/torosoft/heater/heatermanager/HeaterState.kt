package com.torosoft.heater.heatermanager

class HeaterState {
    var isAuto: Boolean = false
    var isSun:  Boolean = true
    var isMoon: Boolean = false
    var currentSetpointTemperature = 20

    private fun HeaterState () {}

    companion object {
        val TAG = HeaterState.javaClass.canonicalName
        private var heaterState: HeaterState? = null

        fun Instance(): HeaterState {
            if (heaterState == null) {
                heaterState = HeaterState()
            }
            val hs = heaterState
            return hs!!
        }
    }

}