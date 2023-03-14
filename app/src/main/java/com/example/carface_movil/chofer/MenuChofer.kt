package com.example.carface_movil.chofer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.carface_movil.Constants
import com.example.carface_movil.session.MainActivity
import com.example.carface_movil.servicios.MyService
import com.example.carface_movil.R
import com.example.carface_movil.utils.UTILS

class MenuChofer : AppCompatActivity() {

    lateinit var mainLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_chofer)
        if(UTILS.obtieneToken(applicationContext)==""){
            UTILS.vuelveLogin(applicationContext)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        mainLayout = findViewById(R.id.chofer_layout)
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

    fun verInfo(view: View){
        val intent = Intent(this, InformacionPersonal::class.java)
        startActivity(intent)
    }

    fun mostrarRegistros(view: View){

    }

    fun aggVehiculo(view: View){
        val intent = Intent(this, RegistraVehiculo::class.java)
        startActivity(intent)
    }

}