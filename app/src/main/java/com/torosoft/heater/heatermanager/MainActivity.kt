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

    var isAuto: Boolean = false
    var isSun: Boolean = false
    var isMoon: Boolean = true

    var timer: Timer? = null
    var timerTask: TimerTask? = null

    val handler = Handler()

    companion object {
        val TAG = MainActivity.javaClass.canonicalName
        var database: MeasureDatabase? = null
    }

    //val TAG: String = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        updateImages()

        // Disable ALWAYS ON screen
/*
        Settings.Global.putInt(getContentResolver(),
                Settings.Global.STAY_ON_WHILE_PLUGGED_IN, 0);
*/
        // Create DB instance
        MainActivity.database = Room.databaseBuilder(this, MeasureDatabase::class.java, "measures").build()


        // Start server MQTT
        val intent = Intent(this, MQTTDataService::class.java)
        startService(intent)

        getMqttClient(this, "tcp://localhost:1883", "Viewer")

        onoff_auto.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                isAuto = ! isAuto
                updateImages()
            }
        })
        onoff_auto.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                isAuto = true
                isSun = false
                isMoon = false
                updateImages()
            }
        })
        onoff_sun.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                isAuto = false
                isSun = true
                isMoon = false
                updateImages()
            }
        })
        onoff_moon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                isAuto = false
                isSun = false
                isMoon = true
                updateImages()
            }
        })

        startTimer()

    }

    fun startTimer() {
        //set a new Timer
        timer = Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        timer!!.schedule(timerTask, 1000, 1000);
    }

    fun stoptimertask(v: View) {

        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel();
            timer = null;
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
                        var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
                        val strDate: String  = simpleDateFormat.format(calendar.getTime());
                        date.setText(strDate)
                    }
                })
            }
        }
    }

    fun updateImages() {
        if (isAuto) {
            onoff_auto.setImageResource(R.drawable.auto_on)
        } else {
            onoff_auto.setImageResource(R.drawable.auto_off)
        }
        if (isSun) {
            onoff_sun.setImageResource(R.drawable.sun_on)
            onoff_status.setImageResource(R.drawable.on)
        } else {
            onoff_sun.setImageResource(R.drawable.sun_off)
            onoff_status.setImageResource(R.drawable.off)
        }
        if (isMoon) {
            onoff_moon.setImageResource(R.drawable.moon_on)
        } else {
            onoff_moon.setImageResource(R.drawable.moon_off)
        }
    }

    fun getMqttClient(context: Context, brokerUrl: String, clientId: String): MqttAndroidClient {
        var mqttClient = MqttAndroidClient(context, brokerUrl, clientId)
        try {
            var token = mqttClient.connect()
            token.actionCallback = object: IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Success");

                    // Subscribe all messages for internal sensors
                    var tokenSub = mqttClient.subscribe("/sensors/internal/temperature/#", 0)
                    tokenSub.actionCallback = object: IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            Log.d(TAG, "Subscription Success");
                        }

                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                            Log.d(TAG, "Subscription Success");
                        }
                    }

                    // Subscribe all messages for external sensors
                    tokenSub = mqttClient.subscribe("/sensors/external/temperature/#", 0)
                    tokenSub.actionCallback = object: IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            Log.d(TAG, "Subscription Success");
                        }

                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                            Log.d(TAG, "Subscription Success");
                        }
                    }
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Error");
                    exception?.printStackTrace()
                }
            }

            mqttClient.setCallback(object: MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    Log.d(TAG, "Topic: " + topic?.toString() + " Message: " + message?.toString())
                    if (topic?.contains("temperature") == true) {
                        if (topic?.contains("internal") == true) {
                            internal_temperature.text = message?.toString() + "°"
                        }
                        if (topic?.contains("external") == true) {
                            external_temperature.text = message?.toString() + "°"
                        }
                    }

                    var measure: Measure = Measure(0,
                            Date(),
                            "temperature",
                            message?.toString()!!.toDouble(),
                            message?.toString(),
                            topic
                    )

                    try {
                        AsyncTaskExample().execute(measure)

                    } catch (ex: Exception) {
                        Log.d(TAG, "Exception: " + ex.message)
                    }
                }

                override fun connectionLost(cause: Throwable?) {
                    mqttClient.disconnect();
                    getMqttClient(context, brokerUrl, clientId)

                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mqttClient
    }

    inner class AsyncTaskExample: AsyncTask<Measure, String, Boolean>() {
        override fun doInBackground(vararg measures: Measure?): Boolean {
            try {
                for (measure: Measure? in measures) {
                    MainActivity.database?.measureDao()?.insert(measure!!)
                }
            } catch (ex: Exception) {
                Log.d(TAG, "Exception: " + ex.message)
                return false
            }
            return true
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }
    }
}
