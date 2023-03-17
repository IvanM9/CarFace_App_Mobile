package com.example.carface_movil.admin

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.carface_movil.Constants
import com.example.carface_movil.R
import com.example.carface_movil.utils.UTILS
import com.google.gson.Gson
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class EditarGuardias : AppCompatActivity() {

    var jsonObject: JSONObject = JSONObject()
    lateinit var id: String
    lateinit var ci: TextView
    lateinit var nombre: TextView
    lateinit var apellido: TextView
    lateinit var compañia: TextView
    lateinit var fechaInicio: TextView
    lateinit var fechaFin: TextView
    private val fechaInicioSeleccionada = Calendar.getInstance()
    private val fechaFinSeleccionada = Calendar.getInstance()
    lateinit var progressBar: ProgressBar
    lateinit var mainLayout: ConstraintLayout
    lateinit var blur: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_guardias)
        inicializaCampos(intent.extras?.getString("json"))
        clickFechaFin()
        clickFechaInicio()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        mainLayout = findViewById(R.id.edit_guardias_layout)
        if (hasFocus) {
            Constants.width = mainLayout.width
            Constants.heigth = mainLayout.height
        }
    }

    fun iniciaBlur() {
        progressBar.visibility = View.VISIBLE
        UTILS.applyBlur(blur, mainLayout, 25f)
    }

    fun detieneBlur() {
        progressBar.visibility = View.GONE
        blur.background = null
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

    fun inicializaCampos(json:String?){
        val gson = Gson()
        jsonObject = gson.fromJson(json, JSONObject::class.java)
        id=jsonObject.getString("id")
        nombre=findViewById(R.id.edit_nombre_guardia)
        nombre.text=jsonObject.getString("nombre")
        ci=findViewById(R.id.edit_ci_guardia)
        ci.text=jsonObject.getString("ci")
        apellido=findViewById(R.id.edit_apellido_guardia)
        apellido.text=jsonObject.getString("apellido")
        compañia=findViewById(R.id.editTextCompania_guardia)
        compañia.text=jsonObject.getString("compania")
        fechaInicio=findViewById(R.id.editTextFechaInicio_guardia)
        fechaInicio.text=jsonObject.getString("fecha_inicio")
        fechaFin=findViewById(R.id.editTextFechaFin_guarda)
        fechaFin.text=jsonObject.getString("fecha_fin")

        fechaInicioSeleccionada.set(Calendar.YEAR,jsonObject.getString("fecha_inicio").substring(0,4).toInt())
        fechaInicioSeleccionada.set(Calendar.MONTH,jsonObject.getString("fecha_inicio").substring(6,7).toInt())
        fechaInicioSeleccionada.set(Calendar.DAY_OF_MONTH,jsonObject.getString("fecha_inicio").substring(9,10).toInt())

        fechaFinSeleccionada.set(Calendar.YEAR,jsonObject.getString("fecha_fin").substring(0,4).toInt())
        fechaFinSeleccionada.set(Calendar.MONTH,jsonObject.getString("fecha_fin").substring(6,7).toInt())
        fechaFinSeleccionada.set(Calendar.DAY_OF_MONTH,jsonObject.getString("fecha_fin").substring(9,10).toInt())

        progressBar = findViewById(R.id.carga)
        progressBar.visibility = View.GONE
        blur = findViewById(R.id.blurVieweditguardias)
    }

    fun llenaJSONRespuesta():String{
        var body=JSONObject()
        body.put("ci",ci.text)
        body.put("nombre",nombre.text)
        body.put("apellido",apellido.text)
        body.put("compania",compañia.text)
        body.put("fecha_inicio",fechaInicio.text)
        body.put("fecha_fin",fechaFin.text)
        return body.toString()
    }

    fun editar(view: View){
        iniciaBlur()
        val queue = Volley.newRequestQueue(applicationContext)
        try {
            val loginRequest =
                object : StringRequest(
                    Method.PUT,
                    Constants.SERVER +"/guardia/editar/"+id,
                    Response.Listener { response ->
                        detieneBlur()
                        UTILS.muestraMensaje(applicationContext,"Editado con exito")
                        val intent= Intent(applicationContext,ListarGuardias::class.java)
                        startActivity(intent)
                    },
                    Response.ErrorListener {
                        UTILS.muestraMensaje(applicationContext,"No pudo editarse correctamente la información")
                        System.out.println(it)
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers.put("Content-Type", "application/json")
                        headers.put("Accept", "*/*")
                        headers.put("Authorization","Bearer "+ UTILS.obtieneToken(applicationContext))
                        return headers
                    }

                    override fun getBody(): ByteArray {
                        return llenaJSONRespuesta().toByteArray(Charsets.UTF_8)
                    }
                }
            queue.add(loginRequest)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    override fun onBackPressed() {
        val intent= Intent(applicationContext,ListarGuardias::class.java)
        startActivity(intent)
    }
}