package com.example.carface_movil.admin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.carface_movil.Constants
import com.example.carface_movil.session.MainActivity
import com.example.carface_movil.servicios.MyService
import com.example.carface_movil.R
import com.example.carface_movil.utils.UTILS

class MenuAdmin : AppCompatActivity() {

    lateinit var mainLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_admin)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        mainLayout = findViewById(R.id.admin_layout)
        if (hasFocus) {
            Constants.width = mainLayout.width
            Constants.heigth = mainLayout.height
        }
    }
    override fun onBackPressed() {
        if (UTILS.obtieneToken(applicationContext)==""){
            UTILS.vuelveLogin(applicationContext)
        }
        showConfirmationDialog()
    }

    fun aggGuardia(view: View){
        val intent = Intent(this, AggGuardia::class.java)
        startActivity(intent)
    }

    fun aggDispositivo(view: View){
        val intent = Intent(this, AggDispositivo::class.java)
        startActivity(intent)
    }

    fun consultaDispositivos(view: View){
        val intent = Intent(this, ListarDispositivos::class.java)
        startActivity(intent)
    }

    fun consultaGuardias(view: View){
        val intent = Intent(this, ListarGuardias::class.java)
        startActivity(intent)
    }
    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Esta accion cerrará la sesión actual. ¿Desea continuar?")
        builder.setPositiveButton("Sí") { dialog, which ->
            UTILS.limpiaMemoria(applicationContext)
            stopService()
            UTILS.vuelveLogin(applicationContext)
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun stopService(){
        val intent1 = Intent(this, MyService::class.java)
        stopService(intent1)
    }

}