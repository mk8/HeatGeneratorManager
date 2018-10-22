package com.torosoft.heater.heatermanager

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.AttributeSet
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import kotlinx.android.synthetic.main.temperature_dialog_fragment.*
import android.app.Activity




class SetTemperatureDialogFragment : DialogFragment() {

    var inflatedView: View? = null
    var callback: (Int) -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val inflater = activity.layoutInflater
        inflatedView = inflater.inflate(R.layout.temperature_dialog_fragment, null)
        var builder = AlertDialog.Builder(activity)
        .setTitle(R.string.dialog_setpoint_temperature_title)
        .setNegativeButton(R.string.dialog_setpoint_temperature_cancel) {
            _ ,
            i -> Toast.makeText(this.context, "Desired temperature clicked", Toast.LENGTH_LONG).show()
        }
        .setPositiveButton(R.string.dialog_setpoint_temperature_confirm) {
            _,
            i -> run {
                var td = inflatedView!!.findViewById<NumberPicker>(R.id.temperature_dialog_fragment_desired_temperature)
                //var heaterState = HeaterState.Instance()
                //heaterState.currentSetpointTemperature = td.value
                callback(td.value)
            }
        }
        .setView(inflatedView)
        return builder.create()
    }

    override fun onResume() {
        super.onResume()

        var td = inflatedView!!.findViewById<NumberPicker>(R.id.temperature_dialog_fragment_desired_temperature)
        val value = arguments.getInt((ARG_START_VALUE))

        td.minValue = 16
        td.maxValue = 26
        td.value = value

        td.wrapSelectorWheel = false
    }

    companion object {
        private val ARG_START_VALUE = "start_value"
        fun newInstance(startValue: Int, callback: (Int) -> Unit): SetTemperatureDialogFragment {
            val fragment = SetTemperatureDialogFragment()
            val args = Bundle()
            args.putInt(ARG_START_VALUE, startValue)
            fragment.arguments = args
            fragment.callback = callback
            return fragment
        }
    }
}