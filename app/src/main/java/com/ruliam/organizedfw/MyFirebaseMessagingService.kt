package com.ruliam.organizedfw

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Received notification")
        if (tiramisuPermissionsCheck()) {
            generateNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
        }
    }


    private fun generateNotification(title: String, message: String){
        Log.d(TAG, "Generate notification called")
        val intent = Intent(this, MainActivity::class.java)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        createNotificationChannel(notificationManager)
        // Clears other activities until our MainActivity opens up
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // So when we click the notification we are going to open the main activity
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        // Create the actual notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, R.drawable.ic_organized))
            .setSmallIcon(R.drawable.ic_organized)
            .setColor(Color.BLACK)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Showing the notification
        notificationManager.notify(notificationID, notification)
    }

    private fun tiramisuPermissionsCheck(): Boolean {
        // If we are above level 33, check permissions
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "Organized"
        val channel = NotificationChannel(
            CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Our notification channel"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }


    companion object {
        const val TAG = "MyFirebaseService"
        const val CHANNEL_ID = "com.ruliam.organizedfw"
    }
}