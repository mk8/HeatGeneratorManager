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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val inflater = activity.layoutInflater
        inflatedView = inflater.inflate(R.layout.temperature_dialog_fragment, null)
        var builder = AlertDialog.Builder(activity)
        .setTitle(R.string.dialog_setpoint_temperature_title)
        .setNegativeButton(R.string.dialog_setpoint_temperature_cancel,  {
            _ ,
            i -> Toast.makeText(this.context, "Desired temperature clicked", Toast.LENGTH_LONG).show()
        })
        .setPositiveButton(R.string.dialog_setpoint_temperature_confirm,  {
            _,
            i -> run {
                var heaterState = HeaterState.Instance()
                var td = inflatedView!!.findViewById<NumberPicker>(R.id.temperature_dialog_fragment_desired_temperature)
                heaterState.currentSetpointTemperature = td.value
            }
        })
        .setView(inflatedView)


        return builder.create()
    }

    override fun onResume() {
        super.onResume()

        var td = inflatedView!!.findViewById<NumberPicker>(R.id.temperature_dialog_fragment_desired_temperature)
        var heaterState = HeaterState.Instance()
        td.minValue = 16
        td.maxValue = 26
        td.value = heaterState.currentSetpointTemperature
        td.wrapSelectorWheel = false
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        val activity = this.activity
        if (activity is MainActivity)
            (activity as MainActivity).updateImages()
    }
}