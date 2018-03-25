package com.torosoft.heater.heatermanager

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import io.moquette.BrokerConstants
import io.moquette.server.Server
import io.moquette.server.config.MemoryConfig
import java.io.File
import java.io.IOException
import java.util.*
import android.content.ContentValues.TAG




class MQTTDataService : Service() {

    var handler: Handler? = null
    var runnable: Runnable? = null

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        //throw UnsupportedOperationException("Not yet implemented")
        return null;
    }

    override fun onCreate() {

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
        var broker = Server();
        try {
            var storageDir = Environment.getExternalStorageDirectory().absolutePath + File.separator + "mqtt"
            var memoryConfig: MemoryConfig = MemoryConfig(Properties())
            var f: File = File(storageDir)
            if (f.exists()) {
                Log.d(MainActivity.TAG, "Error: Storage already exist - Now remove files on it")
                var files = f.listFiles()
                if (files != null && files.count() > 0) {
                    for (file in files) {
                        file.delete()
                    }
                }
            } else {
                if (f.mkdir()) {
                    Log.d(MainActivity.TAG, "Storage created")
                } else {
                    Log.d(MainActivity.TAG, "Unable to create Storage ")
                    return
                }

            }
            storageDir += File.separator + BrokerConstants.DEFAULT_MOQUETTE_STORE_MAP_DB_FILENAME
            memoryConfig.setProperty(
                    BrokerConstants.PERSISTENT_STORE_PROPERTY_NAME,
                    storageDir
            )

            broker.startServer(memoryConfig)
            return
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        }
    }
}
