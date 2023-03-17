package com.example.carface_movil.chofer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.carface_movil.Constants
import com.example.carface_movil.session.MainActivity
import com.example.carface_movil.R
import com.example.carface_movil.utils.UTILS
import org.json.JSONObject

class RegistraVehiculo : AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    lateinit var mainLayout: ConstraintLayout
    lateinit var blur:View
    var jsonObject: JSONObject = JSONObject()
    lateinit var placa: TextView
    lateinit var marca: TextView
    lateinit var modelo: TextView
    lateinit var tipoVehiculo: Spinner
    lateinit var color: TextView
    lateinit var anio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registra_vehiculo)
        inicializaCampos()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        mainLayout = findViewById(R.id.registra_vehiculo_layout)
        if (hasFocus) {
            Constants.width = mainLayout.width
            Constants.heigth = mainLayout.height
        }
    }

    fun iniciaBlur(){
        progressBar.visibility=View.VISIBLE
        UTILS.applyBlur(blur, mainLayout, 25f)
    }

    fun detieneBlur(){
        progressBar.visibility = View.GONE
        blur.background = null
    }

    fun inicializaCampos(){
        placa=findViewById(R.id.et_placa)
        marca=findViewById(R.id.et_marca)
        modelo=findViewById(R.id.et_modelo)
        tipoVehiculo=findViewById(R.id.sp_tipoVehiculo)
        anio=findViewById(R.id.et_año)
        color=findViewById(R.id.et_color)
        progressBar = findViewById(R.id.carga)
        progressBar.visibility=View.GONE
        blur = findViewById(R.id.blurViewaggvehiculo)
    }

    fun llenaJSON(){
        jsonObject= JSONObject()
        jsonObject.put("placa",placa.text.toString().uppercase())
        jsonObject.put("marca",marca.text)
        jsonObject.put("modelo",modelo.text)
        jsonObject.put("año",anio.text)
        jsonObject.put("tipoVehiculo",tipoVehiculo.selectedItem.toString().uppercase())
        jsonObject.put("color",color.text)
    }

    fun registraVehiculo(view: View){
        iniciaBlur()
        llenaJSON()
        val queue = Volley.newRequestQueue(this)
        try {
            val loginRequest =
                object : JsonObjectRequest(
                    Method.POST,
                    Constants.SERVER +"/vehiculo",
                    jsonObject,
                    Response.Listener { response ->
                        detieneBlur()
                        UTILS.muestraMensaje(applicationContext,"Vehiculo Ingresado Correctamente")
                        val intent = Intent(this, MenuChofer::class.java)
                        startActivity(intent)
                    },
                    Response.ErrorListener {
                        detieneBlur()
                        if(it.networkResponse.statusCode==403){
                            UTILS.limpiaMemoria(applicationContext)
                            UTILS.muestraMensaje(applicationContext,"La sesión caducó vuelva a ingresar")
                            UTILS.vuelveLogin(applicationContext)
                        }
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers.put("Content-Type", "application/json")
                        headers.put("Accept", "*/*")
                        headers.put("Authorization","Bearer "+ UTILS.obtieneToken(applicationContext))
                        return headers
                    }
                }
            queue.add(loginRequest)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}