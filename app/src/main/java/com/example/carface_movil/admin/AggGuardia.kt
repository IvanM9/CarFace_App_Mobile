package com.example.carface_movil.admin

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.carface_movil.Constants
import com.example.carface_movil.chofer.MenuChofer
import com.example.carface_movil.R
import com.example.carface_movil.session.MainActivity
import com.example.carface_movil.utils.UTILS
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class AggGuardia : AppCompatActivity() {

    //variables globales
    var jsonObject:JSONObject= JSONObject()
    lateinit var ci: TextView
    lateinit var nombre: TextView
    lateinit var apellido: TextView
    lateinit var correo: TextView
    lateinit var clave: TextView
    lateinit var compañia: TextView
    lateinit var fechaInicio: TextView
    lateinit var fechaFin: TextView
    private val fechaInicioSeleccionada = Calendar.getInstance()
    private val fechaFinSeleccionada = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agg_guardia)
        inicializaCampos()
        clickFechaInicio()
        clickFechaFin()
    }

    fun registraChofer(view: View){
        llenaJSON()
        val queue = Volley.newRequestQueue(this)
        try {
            val agregaGuardiaRequest =
                object : JsonObjectRequest(
                    Method.POST,
                    Constants.SERVER +"/guardia",
                    jsonObject,
                    Response.Listener { response ->
                        UTILS.muestraMensaje(applicationContext,"Guardia Registrado Correctamente")
                        val intent = Intent(this, MenuAdmin::class.java)
                        startActivity(intent)
                    },
                    Response.ErrorListener {
                        if (it.networkResponse.statusCode==403){
                            UTILS.muestraMensaje(applicationContext,"Por favor inicie sesión nuevamente")
                            UTILS.limpiaMemoria(applicationContext)
                            UTILS.vuelveLogin(applicationContext)
                        }else{
                            UTILS.muestraMensaje(applicationContext,"Ocurrio un error al registrar el guardia")
                        }
                        System.out.println(it)
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers.put("Content-Type", "application/json")
                        headers.put("Accept", "*/*")
                        headers.put("Authorization","Bearer "+UTILS.obtieneToken(applicationContext))
                        return headers
                    }
                }
            queue.add(agregaGuardiaRequest)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun llenaJSON(){
        jsonObject= JSONObject()
        jsonObject.put("ci",ci.text.toString())
        jsonObject.put("nombre",nombre.text)
        jsonObject.put("apellido",apellido.text)
        jsonObject.put("correo",correo.text)
        jsonObject.put("clave",clave.text)
        jsonObject.put("compania",compañia.text)
        jsonObject.put("fecha_fin",fechaFin.text)
        jsonObject.put("fecha_inicio",fechaInicio.text)
    }

    fun inicializaCampos(){
        ci=findViewById(R.id.etCIG)
        nombre=findViewById(R.id.etNombreG)
        apellido=findViewById(R.id.etApellidoG)
        correo=findViewById(R.id.etCorreoG)
        clave=findViewById(R.id.etClaveG)
        compañia=findViewById(R.id.etCompaniaG)
        fechaInicio=findViewById(R.id.etFechaInicioG)
        fechaFin=findViewById(R.id.etFechaFinG)
    }

    fun clickFechaInicio(){
        fechaInicio.setOnClickListener {
            val dialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    fechaInicioSeleccionada.set(Calendar.YEAR, year)
                    fechaInicioSeleccionada.set(Calendar.MONTH, month)
                    fechaInicioSeleccionada.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    fechaInicio.text = sdf.format(fechaInicioSeleccionada.time)
                },
                fechaInicioSeleccionada.get(Calendar.YEAR),
                fechaInicioSeleccionada.get(Calendar.MONTH),
                fechaInicioSeleccionada.get(Calendar.DAY_OF_MONTH))
            dialog.show()
        }
    }

    fun clickFechaFin(){
        fechaFin.setOnClickListener {
            val dialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    fechaFinSeleccionada.set(Calendar.YEAR, year)
                    fechaFinSeleccionada.set(Calendar.MONTH, month)
                    fechaFinSeleccionada.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    fechaFin.text = sdf.format(fechaFinSeleccionada.time)
                },
                fechaFinSeleccionada.get(Calendar.YEAR),
                fechaFinSeleccionada.get(Calendar.MONTH),
                fechaFinSeleccionada.get(Calendar.DAY_OF_MONTH))
            dialog.show()
        }
    }
}