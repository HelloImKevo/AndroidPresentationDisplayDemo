package com.kevo.displaydemo.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import com.kevo.displaydemo.R

class AccessoryDisplayService : Service() {

    private var isForeground = false
    private var isBound = false

    /**
     * [Binder] for the [AccessoryDisplayService].
     */
    inner class ServiceBinder : Binder() {

        var service: AccessoryDisplayService = this@AccessoryDisplayService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate -> Service is being created ...")

        isForeground = true
        startForeground(REGISTER_ACCESSORY_DISPLAY_SERVICE, getNotification())
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand -> Command configuration: $intent / $flags / $startId")
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind -> Service bound using $intent")
        return ServiceBinder()
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind -> Service un-bound with $intent")
        isBound = false
        return true
    }

    override fun onRebind(intent: Intent) {
        super.onRebind(intent)
        Log.d(TAG, "onRebind -> Service is being re-binded using $intent")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy -> Service is being destroyed ...")
    }

    fun handleIntent(intent: Intent) {
        Log.w(TAG, "Handling Intents is not supported yet ...")
    }

    private fun getNotification(): Notification {
        /*
        Since Android 8 (Oreo) a Notification Channel must be configured, otherwise you will
        get this runtime crash:

        FATAL EXCEPTION: main
        Process: com.kevo.displaydemo, PID: 11754
        kotlin.NotImplementedError: An operation is not implemented: Not yet implemented
          at com.kevo.displaydemo.service.AccessoryDisplayService.onBind(AccessoryDisplayService.kt:53)
          at android.app.ActivityThread.handleBindService(ActivityThread.java:3980)
          at android.app.ActivityThread.access$1600(ActivityThread.java:219)
          at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1880)
          at android.os.Handler.dispatchMessage(Handler.java:107)
          at android.os.Looper.loop(Looper.java:214)
          at android.app.ActivityThread.main(ActivityThread.java:7356)
          at java.lang.reflect.Method.invoke(Native Method)
          at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:492)
          at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:930)

         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: String = getString(R.string.notification_accessorydisplayservice_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

        // TODO: Support dynamic changing of the Status Icon
        @DrawableRes val smallIcon = R.drawable.ic_fa_layers_half
        // if (!isConnected) {
        //     smallIcon = android.R.drawable.stat_notify_error
        // }

        // TODO: Build Status Bar Notifications for "Stopping" service (and other states)

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(smallIcon)
                .setContentTitle(
                    getString(R.string.notification_accessorydisplayservice_content_title))
                .setContentText(
                    getString(R.string.notification_accessorydisplayservice_content_text))
                .setTicker(getString(R.string.notification_accessorydisplayservice_ticker))
                .setAutoCancel(true)
                .setShowWhen(false)
                .setContentIntent(null)

        return notificationBuilder.build()
    }

    companion object {

        private const val TAG = "AccessoryDisplayService"

        private const val NOTIFICATION_CHANNEL_ID = "AccessoryDisplayService"

        // This is a unique ID that should be defined in an apps Constants file.
        private const val REGISTER_ACCESSORY_DISPLAY_SERVICE = 2
    }
}
