package com.example.carface_movil.admin

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
import com.android.volley.toolbox.Volley
import com.example.carface_movil.Constants
import com.example.carface_movil.R
import com.example.carface_movil.chofer.MenuChofer
import com.example.carface_movil.utils.UTILS
import org.json.JSONObject

class AggDispositivo : AppCompatActivity() {

    private lateinit var ubicaciontxt:TextView
    private lateinit var codigotxt:TextView
    private lateinit var nombretxt:TextView
    private lateinit var clavetxt:TextView
    lateinit var mainLayout: ConstraintLayout
    lateinit var blur:View
    lateinit var progressBar: ProgressBar
    var jsonObject: JSONObject = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agg_dispositivo)
        inicializaCampos()
    }

    fun inicializaCampos(){
        ubicaciontxt=findViewById(R.id.etUbicaciondisp)
        codigotxt=findViewById(R.id.etCodigodisp)
        nombretxt=findViewById(R.id.etNombredisp)
        clavetxt=findViewById(R.id.etClavedisp)
        progressBar = findViewById(R.id.carga)
        progressBar.visibility=View.GONE
        blur = findViewById(R.id.blurViewaggdisp)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        mainLayout = findViewById(R.id.agg_disp_layout)
        if (hasFocus) {
            Constants.width = mainLayout.width
            Constants.heigth = mainLayout.height
        }
    }

    fun iniciaBlur(){
        progressBar.visibility= View.VISIBLE
        UTILS.applyBlur(blur, mainLayout, 25f)
    }

    fun detieneBlur(){
        progressBar.visibility = View.GONE
        blur.background = null
    }

    fun llenaJSON(){
        jsonObject= JSONObject()
        jsonObject.put("ubicacion",ubicaciontxt.text.toString())
        jsonObject.put("clave",clavetxt.text)
        jsonObject.put("codigo",codigotxt.text)
        jsonObject.put("nombre",nombretxt.text)
    }

    fun registraDispositivo(view: View){
        iniciaBlur()
        llenaJSON()
        val queue = Volley.newRequestQueue(this)
        try {
            val loginRequest =
                object : JsonObjectRequest(
                    Method.POST,
                    Constants.SERVER +"/dispositivo",
                    jsonObject,
                    Response.Listener { response ->
                        detieneBlur()
                        UTILS.muestraMensaje(applicationContext,"Dispositivo Ingresado Correctamente")
                        val intent = Intent(this, MenuAdmin::class.java)
                        startActivity(intent)
                    },
                    Response.ErrorListener {
                        detieneBlur()
                        if(it.networkResponse.statusCode==403){
                            UTILS.limpiaMemoria(applicationContext)
                            UTILS.muestraMensaje(applicationContext,"La sesión caducó vuelva a ingresar")
                            UTILS.vuelveLogin(applicationContext)
                        }else{
                            UTILS.muestraMensaje(applicationContext,"Algo salió mal")
                        }
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
                }
            queue.add(loginRequest)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}