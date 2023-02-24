package com.example.carface_movil

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MenuPrincipal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)
        WebSocketManager.context=this;
        val sharedPreferences =
            getSharedPreferences("Settings", Context.MODE_PRIVATE);
        var sock = sharedPreferences.getString("ip", "defaultValue")
            ?.let {
                WebSocketManager.webSocket.connect()
            }
    }

    fun escanearVehiculo(view: View){
        val intent = Intent(this, Solicitud::class.java);
        startActivity(intent);
    }

    fun buscarVehiculo(view: View){
        val intent = Intent(this, MenuPrincipal::class.java);
        startActivity(intent);
    }

    fun registroVehiculos(view: View){
        val intent = Intent(this, MenuPrincipal::class.java);
        startActivity(intent);
    }
}