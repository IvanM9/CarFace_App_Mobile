package com.example.carface_movil.guardia

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.carface_movil.Constants
import com.example.carface_movil.servicios.MyService
import com.example.carface_movil.R
import com.example.carface_movil.session.MainActivity
import com.example.carface_movil.socket.WebSocketManager
import com.example.carface_movil.utils.UTILS

class MenuPrincipal : AppCompatActivity() {

    lateinit var mainLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)
        WebSocketManager.context=this
        if(UTILS.obtieneToken(applicationContext)!=""){
            WebSocketManager.webSocket.connect()
        }else{
            UTILS.vuelveLogin(applicationContext)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        mainLayout = findViewById(R.id.guardia_layout)
        if (hasFocus) {
            Constants.width = mainLayout.width
            Constants.heigth = mainLayout.height
        }
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
        if (UTILS.obtieneToken(applicationContext)==""){
            UTILS.vuelveLogin(applicationContext)
        }
        showConfirmationDialog()
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Esta accion cerrará la sesión actual. ¿Desea continuar?")
        builder.setPositiveButton("Sí") { dialog, which ->
            UTILS.limpiaMemoria(applicationContext)
            UTILS.detieneServicio(applicationContext)
            UTILS.vuelveLogin(applicationContext)
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun registroVehiculos(view: View){
        val intent = Intent(this, MenuPrincipal::class.java)
        startActivity(intent)
    }

    fun escanearVehiculo(viewF: View){
        val intent = Intent(this, Solicitud::class.java)
        startActivity(intent)
    }

    fun buscarVehiculo(view: View){
        val intent = Intent(this, EscanearVehiculo::class.java)
        intent.putExtra("evento","BUSQUEDA")
        startActivity(intent)
    }

}