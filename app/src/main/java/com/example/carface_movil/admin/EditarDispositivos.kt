package com.example.carface_movil.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.carface_movil.Constants
import com.example.carface_movil.R
import com.example.carface_movil.utils.UTILS
import com.google.gson.Gson
import org.json.JSONObject

class EditarDispositivos : AppCompatActivity() {

    var jsonObject: JSONObject = JSONObject()
    var jsonObjectEnviar: JSONObject = JSONObject()
    private lateinit var ubicaciontxt: TextView
    private lateinit var nombretxt:TextView
    private lateinit var clavetxt:TextView
    lateinit var id:String
    lateinit var progressBar: ProgressBar
    lateinit var mainLayout: ConstraintLayout
    lateinit var blur: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_dispositivos)
        inicializaCampos(intent.extras?.getString("json"))
    }

    fun inicializaCampos(json:String?){
        val gson = Gson()
        jsonObject = gson.fromJson(json, JSONObject::class.java)
        id=jsonObject.getString("id")
        ubicaciontxt=findViewById(R.id.et_ubicacion_eddisp)
        ubicaciontxt.text=jsonObject.getString("ubicacion")
        nombretxt=findViewById(R.id.et_nombreeddisp)
        nombretxt.text=jsonObject.getString("nombre")
        clavetxt=findViewById(R.id.et_claveeddisp)
        progressBar = findViewById(R.id.carga)
        progressBar.visibility = View.GONE
        blur = findViewById(R.id.blurVieweditdisp)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        mainLayout = findViewById(R.id.edit_disp_layout)
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

    fun llenaJSON():String{
        jsonObjectEnviar.put("ubicacion",ubicaciontxt.text)
        jsonObjectEnviar.put("nombre",nombretxt.text)
        jsonObjectEnviar.put("clave",clavetxt.text)
        return jsonObjectEnviar.toString();
    }

    fun editarDispositivo(view: View){
        iniciaBlur()
        val queue = Volley.newRequestQueue(this)
        try {
            val loginRequest =
                object : StringRequest(
                    Method.PUT,
                    Constants.SERVER +"/dispositivo/"+id,
                    Response.Listener { response:String ->
                        detieneBlur()
                        UTILS.muestraMensaje(applicationContext,"Se modifico Correctamente")
                        val intent=Intent(applicationContext,ListarDispositivos::class.java);
                        startActivity(intent)
                    },
                    Response.ErrorListener {
                        detieneBlur()
                        if(it.networkResponse.statusCode==403){
                            UTILS.limpiaMemoria(applicationContext)
                            UTILS.muestraMensaje(applicationContext,"La sesión caducó, vuelva a ingresar por favor")
                            UTILS.vuelveLogin(applicationContext)
                        }
                        System.out.println(it)
                    }
                    ) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers.put("Content-Type", "application/json")
                        headers.put("Accept", "*/*")
                        headers.put("Authorization","Bearer "+ UTILS.obtieneToken(applicationContext))
                        return headers
                    }

                    override fun getBody(): ByteArray {
                        return llenaJSON().toByteArray(Charsets.UTF_8)
                    }
                }
            queue.add(loginRequest)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}