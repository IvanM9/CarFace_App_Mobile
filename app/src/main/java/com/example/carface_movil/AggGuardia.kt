package com.example.carface_movil

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
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
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class AggGuardia : AppCompatActivity() {

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
        ci=findViewById(R.id.etCIG)
        nombre=findViewById(R.id.etNombreG)
        apellido=findViewById(R.id.etApellidoG)
        correo=findViewById(R.id.etCorreoG)
        clave=findViewById(R.id.etClaveG)
        compañia=findViewById(R.id.etCompaniaG)
        fechaInicio=findViewById(R.id.etFechaInicioG)
        fechaFin=findViewById(R.id.etFechaFinG)
        fechaInicio.setOnClickListener {
            val dialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    fechaInicioSeleccionada.set(Calendar.YEAR, year)
                    fechaInicioSeleccionada.set(Calendar.MONTH, month)
                    fechaInicioSeleccionada.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    fechaInicio.setText(sdf.format(fechaInicioSeleccionada.time))
                },
                fechaInicioSeleccionada.get(Calendar.YEAR),
                fechaInicioSeleccionada.get(Calendar.MONTH),
                fechaInicioSeleccionada.get(Calendar.DAY_OF_MONTH))
            dialog.show()
        }

        fechaFin.setOnClickListener {
            val dialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    fechaFinSeleccionada.set(Calendar.YEAR, year)
                    fechaFinSeleccionada.set(Calendar.MONTH, month)
                    fechaFinSeleccionada.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    fechaFin.setText(sdf.format(fechaFinSeleccionada.time))
                },
                fechaFinSeleccionada.get(Calendar.YEAR),
                fechaFinSeleccionada.get(Calendar.MONTH),
                fechaFinSeleccionada.get(Calendar.DAY_OF_MONTH))
            dialog.show()
        }

    }


    fun registraChofer(view: View){
        jsonObject= JSONObject()
        jsonObject.put("ci",ci.text.toString())
        jsonObject.put("apellido",nombre.text)
        jsonObject.put("apellido",apellido.text)
        jsonObject.put("correo",correo.text)
        jsonObject.put("clave",clave.text)
        jsonObject.put("compañia",compañia.text)
        jsonObject.put("fechaFin",fechaFin.text)
        jsonObject.put("fechaInicio",fechaInicio.text)

        val queue = Volley.newRequestQueue(this)
        val sharedPreferences =
            getSharedPreferences("Settings", Context.MODE_PRIVATE);
        var token = sharedPreferences.getString("token", "")
        try {
            val loginRequest =
                object : JsonObjectRequest(
                    Request.Method.POST,
                    Constants.SERVER+"/guardia",
                    jsonObject,
                    Response.Listener { response ->
                        Toast.makeText(
                            this,
                            "Guardia Registrado Correctamente",
                            Toast.LENGTH_LONG
                        ).show();
                        val intent = Intent(this, MenuChofer::class.java)
                        startActivity(intent)
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            this,
                            "Ocurrió un error al registrar el guardia",
                            Toast.LENGTH_LONG
                        ).show();
                        System.out.println(it);
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers.put("Content-Type", "application/json")
                        headers.put("Accept", "*/*")
                        headers.put("Authorization","Bearer "+token)
                        return headers
                    }
                }
            queue.add(loginRequest);
        } catch (e: Exception) {
            println(e.message)
        }
    }
}