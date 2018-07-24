package com.torosoft.heater.heatermanager

import android.os.Bundle
import android.app.Activity
import kotlinx.android.synthetic.main.activity_config_temperature.*
import android.widget.ArrayAdapter
import android.R.menu
import android.view.Menu
import android.view.MenuInflater



class ConfigTemperatureActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_temperature)

        val sampleStringList = arrayOf("Busy day", "Busy day with morning", "Weekend", "Vacation");

        val spinnerArrayAdapter = ArrayAdapter<String>(
                this, R.layout.spinner_item, sampleStringList
        )
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item)
        sunday_spinner.setAdapter(spinnerArrayAdapter)
        monday_spinner.setAdapter(spinnerArrayAdapter)
        tuesday_spinner.setAdapter(spinnerArrayAdapter)
        wednesday_spinner.setAdapter(spinnerArrayAdapter)
        thursday_spinner.setAdapter(spinnerArrayAdapter)
        friday_spinner.setAdapter(spinnerArrayAdapter)
        saturday_spinner.setAdapter(spinnerArrayAdapter)
    }
}
