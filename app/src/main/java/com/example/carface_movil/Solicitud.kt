package com.example.carface_movil

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import java.net.URI


class Solicitud : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitud)
    }


    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        val sharedPreferences =
            getSharedPreferences("Settings", Context.MODE_PRIVATE);
        var sock = sharedPreferences.getString("ip", "defaultValue")
            ?.let { WebSockets(it).socket.connect() }

        super.onPostCreate(savedInstanceState, persistentState)
    }

    override fun onStart() {
        val sharedPreferences =
            getSharedPreferences("Settings", Context.MODE_PRIVATE);
        val options =
            IO.Options.builder().setPath("/socket.io").setPort(8081).setQuery("room=a").setForceNew(false).setTransports(
                arrayOf(WebSocket.NAME)
            ).build()
        val ip = sharedPreferences.getString("ip", "defalutValue")
        val mSocket =
            IO.socket(URI.create("http://$ip"), options);
        mSocket.connect();
        if (mSocket.connected())
            System.out.println("Conectado")
        /*var sock = sharedPreferences.getString("ip","defaultValue")
            ?.let { WebSockets(it).socket.connect() }*/

        super.onStart()
    }
}