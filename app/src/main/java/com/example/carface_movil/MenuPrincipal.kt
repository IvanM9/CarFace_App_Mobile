package com.example.carface_movil

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat

class MenuPrincipal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)
        WebSocketManager.context=this;
        val sharedPreferences =
            getSharedPreferences("Settings", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("token", "")!=""){
            WebSocketManager.webSocket.connect()
        }else{
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        }
    }

    fun escanearVehiculo(viewF: View){
        val intent = Intent(this, Solicitud::class.java);
        startActivity(intent);
    }
    fun buscarVehiculo(view: View){
        val intent = Intent(this, EscanearVehiculo::class.java);
        intent.putExtra("evento","BUSQUEDA")
        startActivity(intent);
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MyService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    override fun onBackPressed() {
        val sharedPreferences =
            getSharedPreferences("Settings", Context.MODE_PRIVATE);
        var token:String=""
        var sock = sharedPreferences.getString("token", "")
            ?.let {
                token=it;
            }
        if (token==""){
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        }
        showConfirmationDialog();
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Esta accion cerrará la sesión actual. ¿Desea continuar?")

        builder.setPositiveButton("Sí") { dialog, which ->
            // Acciones a realizar si el usuario confirma
            val sharedPreferences =
                getSharedPreferences("Settings", Context.MODE_PRIVATE);
            val editor: SharedPreferences.Editor = sharedPreferences.edit();
            editor.putString("token", "")
            editor.putString("rol", "")
            editor.putString("ip", "")
            editor.apply();
            val intent1 = Intent(this, MyService::class.java)
            stopService(intent1)
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        }
        builder.setNegativeButton("No") { dialog, which ->
            // Acciones a realizar si el usuario cancela
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }


    fun registroVehiculos(view: View){
        val intent = Intent(this, MenuPrincipal::class.java);
        startActivity(intent);
    }
}