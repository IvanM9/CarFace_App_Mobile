package com.example.carface_movil

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.os.PowerManager
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import dev.icerock.moko.socket.SocketEvent
import dev.icerock.moko.socket.SocketOptions
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI


class Solicitud : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitud)
        val sharedPreferences =
            getSharedPreferences("Settings", Context.MODE_PRIVATE);
        var sock = sharedPreferences.getString("ip", "defaultValue")
            ?.let { WebSockets(it).socket.connect() }
        probandoVolley()
        var toolbar : Toolbar?= findViewById(R.id.toolbar2);
        toolbar!!?.title="LISTA DE VEHICULOS"
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar);
    }

    fun probandoVolley() {
        VisualizaCardview_(WebSocketData.data)
    }

    private fun VisualizaCardview_(userList: ArrayList<JSONObject>){
        val recyclerView_ : RecyclerView =findViewById(R.id.recycler_articulos)
        val adapter_=CustomerAdapter_Solicitud(this, userList)

        recyclerView_.layoutManager= LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        recyclerView_.adapter=adapter_

        val resId = R.anim.layout_animation_down_to_up
        val animation = AnimationUtils.loadLayoutAnimation(
            applicationContext,
            resId
        )
        recyclerView_.layoutAnimation = animation

    }

    inner class WebSockets(ip:String) {
        val socket = dev.icerock.moko.socket.Socket(
            endpoint = "http://$ip:8081",
            config = SocketOptions(
                queryParams = mapOf("room" to "a"),
                transport = SocketOptions.Transport.WEBSOCKET
            )
        ) {
            on(SocketEvent.Connect) {
                println("connect")
            }

            on(SocketEvent.Connecting) {
                println("connecting")
            }

            on(SocketEvent.Disconnect) {
                println("disconnect")
            }

            on(SocketEvent.Error) {
                println("error $it")
            }

            on("get_message") { args ->

                    var data: String = args.toString()
                    val jsonObj = JSONObject(data)
                    var message = jsonObj.getString("message");
                    var messageJSON = JSONObject(message);
                    WebSocketData.data.add(messageJSON)
                    System.out.println(WebSocketData.data);
                    notification(
                        messageJSON.getString("nombres"),
                        messageJSON.getString("apellidos")
                    );
                    recarga()
            }
        }
    }

    fun recarga(){
        val intent = Intent(this, Solicitud::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    fun notification(nombre:String, apellido:String){
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "myapp:notification_wakelock"
        )
        wakeLock.acquire(5000) // Enciende la pantalla durante 5 segundos

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "my_channel_id"
        val channelName = "My Channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance)
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Un vehículo acaba de ingresar.")
            .setContentText("Conductor: "+nombre+" "+apellido)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val pattern = longArrayOf(0, 1000, 500, 1000)
        builder.setVibrate(pattern)

        notificationManager.notify(0, builder.build())

    }

}