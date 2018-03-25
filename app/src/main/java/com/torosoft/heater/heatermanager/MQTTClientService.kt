package com.torosoft.heater.heatermanager

import android.app.Service
import android.arch.persistence.room.Room
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.torosoft.heater.heatermanager.Entities.Measure
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.util.*
import android.support.v4.content.LocalBroadcastManager



class MQTTClientService : Service() {

    var handler: Handler? = null
    var runnable: Runnable? = null

    companion object {
        val TAG = MainActivity.javaClass.canonicalName
        var database: MeasureDatabase? = null
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        return null;
    }

    override fun onCreate() {

        // Create DB instance
        MQTTClientService.database = Room.databaseBuilder(this, MeasureDatabase::class.java, "measures").build()

        handler = Handler()
        runnable = Runnable {
            handler!!.postDelayed(runnable, 10000)
        }

        handler!!.postDelayed(runnable, 15000)
    }

    override fun onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
    }

    override fun onStart(intent: Intent, startid: Int) {
        getMqttClient(this, "tcp://localhost:1883", "Viewer")
    }

    fun getMqttClient(context: Context, brokerUrl: String, clientId: String): MqttAndroidClient {
        var mqttClient = MqttAndroidClient(context, brokerUrl, clientId)
        try {
            var token = mqttClient.connect()
            token.actionCallback = object: IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(MQTTClientService.TAG, "Success");

                    // Subscribe all messages for internal sensors
                    var tokenSub = mqttClient.subscribe("/sensors/internal/temperature/#", 0)
                    tokenSub.actionCallback = object: IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            Log.d(MQTTClientService.TAG, "Subscription Success");
                        }

                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                            Log.d(MQTTClientService.TAG, "Subscription Success");
                        }
                    }

                    // Subscribe all messages for external sensors
                    tokenSub = mqttClient.subscribe("/sensors/external/temperature/#", 0)
                    tokenSub.actionCallback = object: IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            Log.d(MQTTClientService.TAG, "Subscription Success");
                        }

                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                            Log.d(MQTTClientService.TAG, "Subscription Success");
                        }
                    }
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(MQTTClientService.TAG, "Error");
                    exception?.printStackTrace()
                }
            }

            mqttClient.setCallback(object: MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    Log.d(MQTTClientService.TAG, "Topic: " + topic?.toString() + " Message: " + message?.toString())
                    if (topic?.contains("temperature") == true) {
                        if (topic?.contains("internal") == true) {
                            sendMessage("internal_temperature",message?.toString() + "°")
                        }
                        if (topic?.contains("external") == true) {
                            sendMessage("external_temperature",message?.toString() + "°")
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
                        Log.d(MQTTClientService.TAG, "Exception: " + ex.message)
                    }
                }

                override fun connectionLost(cause: Throwable?) {
                    mqttClient.disconnect();
                    getMqttClient(context, brokerUrl, clientId)
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    TODO("not implemented")
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
                    MQTTClientService.database?.measureDao()?.insert(measure!!)
                }
            } catch (ex: Exception) {
                Log.d(MQTTClientService.TAG, "Exception: " + ex.message)
                return false
            }
            return true
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }
    }

    // Supposing that your value is an integer declared somewhere as: int myInteger;
    private fun sendMessage(dataSent: String, value: String) {
        val intent = Intent("MQTT-Value")

        intent.putExtra(dataSent, value)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}