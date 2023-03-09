package com.example.carface_movil

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog

class MenuChofer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_chofer)
        val sharedPreferences =
            getSharedPreferences("Settings", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("token", "")==""){
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        }
    }

    fun verInfo(view:View){
        val intent = Intent(this, InformacionPersonal::class.java);
        startActivity(intent);
    }

    fun mostrarRegistros(view: View){

    }

    fun aggVehiculo(view: View){
        val intent = Intent(this, RegistraVehiculo::class.java);
        startActivity(intent);
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
}