package com.example.carface_movil

import Persona
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Solicitud : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitud)

        var toolbar : Toolbar?= findViewById(R.id.toolbar2);
        toolbar!!?.title="VEHICULOS EN ESPERA DE SALIDA"
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar);
        // Obtener la instancia de SharedPreferences
        sharedPreferences = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        // Actualizar WebSocketData.data con la información guardada en SharedPreferences
        val gson = Gson()
        val json = sharedPreferences?.getString("data", null)
        if (json != null) {
            val data: ArrayList<Persona> = gson.fromJson(json, object : TypeToken<ArrayList<Persona>>() {}.type)
            WebSocketData.data = data
        }
        VisualizaCardview_(WebSocketData.data)
    }

    fun registra_entrada(view: View){
        val intent = Intent(this, EscanearVehiculo::class.java);
        intent.putExtra("evento","ENTRADA")
        startActivity(intent);
    }

    private fun VisualizaCardview_(userList: ArrayList<Persona>){
        val recyclerView_ : RecyclerView =findViewById(R.id.recyler_cola)
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

    override fun onBackPressed() {
        val intent = Intent(this, MenuPrincipal::class.java);
        startActivity(intent);
    }
}