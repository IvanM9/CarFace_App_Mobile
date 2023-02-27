package com.example.carface_movil

import Persona
import WebSocketManager
import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MyService : Service() {

    private lateinit var webSocketManager: WebSocketManager
    private lateinit var thread: Thread
    private lateinit var sharedPreferences: SharedPreferences

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Aquí puedes realizar las tareas que deseas que se ejecuten en segundo plano.
        // Por ejemplo, puedes iniciar una tarea en un hilo separado:
        Thread {
            // Código de la tarea aquí
        }.start()
        // Recuperar la información de SharedPreferences
        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences?.getString("data", null)
        if (json != null) {
            val data: ArrayList<Persona> = gson.fromJson(json, object : TypeToken<ArrayList<Persona>>() {}.type)
            WebSocketData.data = data
        }
        // Retorna START_STICKY para que el servicio se reinicie si se detiene inesperadamente.
        return START_STICKY
    }

    override fun onCreate() {
        // Aquí inicializa el WebSocketManager y el hilo para ejecutarlo en segundo plano
        /*webSocketManager = WebSocketManager()
        thread = Thread(webSocketManager)
        thread.start()*/

        val chan = NotificationChannel(
            "MyChannelId",
            "My Foreground Service",
            NotificationManager.IMPORTANCE_LOW
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_SECRET

        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)!!
        manager!!.createNotificationChannel(chan)

        val notificationBuilder = NotificationCompat.Builder(
            this, "MyChannelId"
        )
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(com.example.carface_movil.R.drawable.baseline_directions_car_24)
            .setContentTitle("CarFace esta ejecutandose en segundo plano")
            .setPriority(NotificationManager.IMPORTANCE_NONE)
            .setColor(Color.GREEN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setChannelId("MyChannelId")
            .build()

        startForeground(1, notification)

    }

    override fun onDestroy() {
        super.onDestroy()
        // Aquí puedes realizar cualquier limpieza necesaria cuando el servicio se detiene.
    }
}
