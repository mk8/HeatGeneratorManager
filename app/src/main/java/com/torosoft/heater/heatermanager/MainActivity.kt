package com.torosoft.heater.heatermanager

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.text.SimpleDateFormat
import java.util.*
import android.arch.persistence.room.Room
import android.content.Intent
import android.os.AsyncTask
import android.provider.Settings
import com.torosoft.heater.heatermanager.Entities.Measure
import android.support.v4.content.LocalBroadcastManager
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.widget.Toast
import android.R.attr.data
import android.view.Display
import android.hardware.display.DisplayManager
import android.view.WindowManager
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.R.menu
import android.view.Menu


/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {

    var timer: Timer? = null
    var timerTask: TimerTask? = null

    val handler = Handler()
    val heaterState: HeaterState = HeaterState.Instance()
    val TIMEOUT_BLANK: Int = 20
    var tick: Int = TIMEOUT_BLANK

    companion object {
        val TAG = MainActivity.javaClass.canonicalName
        var database: MeasureDatabase? = null
    }

    //val TAG: String = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        updateImages()

        // Create DB instance
        MainActivity.database = Room.databaseBuilder(this, MeasureDatabase::class.java, "measures").build()


        // Start server MQTT
        var intent = Intent(this, MQTTDataService::class.java)
        startService(intent)

        // Start client MQTT service
        intent = Intent(this, MQTTClientService::class.java)
        startService(intent)

        //getMqttClient(this, "tcp://localhost:1883", "Viewer")

        onoff_auto.setOnClickListener {
            heaterState.isAuto = true
            heaterState.isSun = false
            heaterState.isMoon = false
            updateImages()
        }
        onoff_sun.setOnClickListener {
            heaterState.isAuto = false
            heaterState.isSun = true
            heaterState.isMoon = false
            updateImages()
        }
        onoff_moon.setOnClickListener {
            heaterState.isAuto = false
            heaterState.isSun = false
            heaterState.isMoon = true
            updateImages()
        }

        desiredTemperature.setOnClickListener {
            tick = TIMEOUT_BLANK
            var dialogFragment = SetTemperatureDialogFragment.newInstance(Integer.valueOf(desiredTemperature.text.toString())) {
                heaterState.currentSetpointTemperature = it
                updateImages()
            }
            dialogFragment.show(fragmentManager, "Desired Temperature")
        }

        preferences.setOnClickListener {
            //val intent = Intent(this, ConfigTemperatureActivity::class.java)
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    fun startTimer() {
        //set a new Timer
        timer = Timer()

        //initialize the TimerTask's job
        initializeTimerTask()

        timer!!.schedule(timerTask, 1000, 1000)
    }

    fun stoptimertask() {

        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    fun initializeTimerTask() {
        timerTask = object: TimerTask() {

            override fun run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(object: Runnable {

                    override fun run() {

                        //get the current timeStamp
                        var calendar: Calendar = Calendar.getInstance()
                        var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.ITALY)
                        val strDate: String  = simpleDateFormat.format(calendar.getTime())
                        date.setText(strDate)

                        if (tick < 0) {

                            val intent = Intent(this@MainActivity, BlankScreenActivity::class.java)
                            startActivity(intent)
                            tick = TIMEOUT_BLANK

                        } else {
                            tick--
                        }
                    }
                })
            }
        }
    }

    fun updateImages() {
        tick = TIMEOUT_BLANK
        if (heaterState.isAuto) {
            onoff_auto.setImageResource(R.drawable.auto_on)
        } else {
            onoff_auto.setImageResource(R.drawable.auto_off)
        }
        if (heaterState.isSun) {
            onoff_sun.setImageResource(R.drawable.sun_on)
            onoff_status.setImageResource(R.drawable.on)
        } else {
            onoff_sun.setImageResource(R.drawable.sun_off)
            onoff_status.setImageResource(R.drawable.off)
        }
        if (heaterState.isMoon) {
            onoff_moon.setImageResource(R.drawable.moon_on)
        } else {
            onoff_moon.setImageResource(R.drawable.moon_off)
        }

        desiredTemperature.text = heaterState.currentSetpointTemperature.toString()
    }

    public override fun onResume() {
        super.onResume()
        // This registers mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver,
                        IntentFilter("MQTT-Value"))
        startTimer()

        val layoutParams = window.attributes
        layoutParams.screenBrightness = 1.0f
        window.attributes=layoutParams

    }

    // Handling the received Intents for the "my-integer" event
    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("internal_temperature")) {
                internal_temperature.text = intent.getStringExtra("internal_temperature")
            }
            if (intent.hasExtra("external_temperature")) {
                external_temperature.text = intent.getStringExtra("external_temperature")
            }
        }
    }

    override fun onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mMessageReceiver)
        stoptimertask()
        super.onPause()
    }
}
