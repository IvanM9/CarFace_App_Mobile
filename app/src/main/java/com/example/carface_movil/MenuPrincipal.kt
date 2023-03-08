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
        var sock = sharedPreferences.getString("ip", "")
            ?.let {
                WebSocketManager.webSocket.connect()
            }
    }

    fun escanearVehiculo(viewF: View){
        val intent = Intent(this, Solicitud::class.java);
        startActivity(intent);
    }

    private fun startService() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "MyService"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "My Backgrou9nd Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, MenuPrincipal::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("My Background Service")
            .setContentText("Estamos ejecutando un servicio en segundo plano")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(145687148, notification)

        val serviceIntent = Intent(this, MyService::class.java)
        startService(serviceIntent)
    }

    fun buscarVehiculo(view: View){
        val intent = Intent(this, EscanearVehiculo::class.java);
        intent.putExtra("evento","BUSQUEDA")
        startActivity(intent);
    }

    override fun onStart() {
        super.onStart()
        // En el método onStart() de tu actividad:
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
        var sock = sharedPreferences.getString("token", "defaultValue")
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