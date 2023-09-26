package com.kevo.displaydemo.service

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.kevo.displaydemo.app.Intent.Action
import com.kevo.displaydemo.app.IntentExtras

/**
 * Singleton that retains a reference to service bindings that can be used to relay
 * instructions to the service.
 */
class ServiceManager private constructor() {

    // TODO: I'm not real comfortable with this approach of holding a forever reference to
    //  a Service instance in the scope of a Singleton - smells like it could be a bad design.
    //  However, the onServiceDisconnected() should nullify it.
    private var accessoryDisplayService: AccessoryDisplayService? = null

    fun startAccessoryDisplayService(app: Context) {
        val intent = Intent(app, AccessoryDisplayService::class.java).apply {
            putExtra(IntentExtras.ACCESSORY_DISPLAY_START, true)
        }

        Log.i(TAG, "Sending instruction to start the Accessory Display service ...")
        sendToService(app, intent)
    }

    // TODO: This is a work in progress ...
    fun stopAccessoryDisplayService(app: Context): PendingIntent {
        val intent = Intent(app, AccessoryDisplayService::class.java).apply {
            action = Action.STOP_SERVICE
        }

        return PendingIntent.getService(app, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun sendToService(app: Context, intent: Intent) {
        if (accessoryDisplayService != null) {
            accessoryDisplayService?.handleIntent(intent)
        } else {
            Log.i(TAG, "Creating new service connection ...")
            app.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            Log.i(TAG, "Service connected. Name: $name / Binder: $binder")
            val serviceBinder = binder as AccessoryDisplayService.ServiceBinder
            accessoryDisplayService = serviceBinder.service
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i(TAG, "Service disconnected. Name: $name")
            accessoryDisplayService = null
        }
    }

    companion object {

        private const val TAG = "ServiceManager"

        @Volatile
        private lateinit var instance: ServiceManager

        @JvmStatic
        fun getInstance(): ServiceManager {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = ServiceManager()
                }
                return instance
            }
        }
    }
}
