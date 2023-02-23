package com.example.carface_movil

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelID = "notification-channel"
const val channelName = "com.geek.kotlin_api_revistas"
class MyFirebaseMessagingService: FirebaseMessagingService() {

    fun generateNotification(title: String,message: String)
    {
        val intent= Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        var bulider: NotificationCompat.Builder=NotificationCompat.Builder(applicationContext, channelID).setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
        bulider=bulider.setContent(getRemoteView(title,message))

        val notificacionManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificacionManager.createNotificationChannel(channel)
            }
        notificacionManager.notify(0,bulider.build())


        }

    fun getRemoteView(title:String,message:String):RemoteViews
    {
        val remoteViews = RemoteViews("com.geek.kotlin_api_revistas",R.layout.notificacion)
        remoteViews.setTextViewText(R.id.title,title)
        remoteViews.setTextViewText(R.id.description,message)
        return remoteViews
    }


    override fun onMessageReceived(remotemessage: RemoteMessage) {
        if(remotemessage.getNotification()!=null){
            generateNotification(remotemessage.notification!!.title!!,remotemessage.notification!!.body!!)
        }
    }
}