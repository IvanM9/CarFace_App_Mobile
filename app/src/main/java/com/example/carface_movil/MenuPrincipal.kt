package com.example.carface_movil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MenuPrincipal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)
    }

    fun escanearVehiculo(view: View){
        val intent = Intent(this, EscanearVehiculo::class.java);
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