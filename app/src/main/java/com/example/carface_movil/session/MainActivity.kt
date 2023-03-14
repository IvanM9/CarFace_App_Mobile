package com.example.carface_movil.session

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.carface_movil.Constants
import com.example.carface_movil.R
import com.example.carface_movil.admin.MenuAdmin
import com.example.carface_movil.chofer.MenuChofer
import com.example.carface_movil.chofer.RegistroActivity
import com.example.carface_movil.guardia.MenuPrincipal
import com.example.carface_movil.utils.UTILS.applyBlur
import com.example.carface_movil.utils.UTILS.muestraMensaje
import com.example.carface_movil.utils.UTILS.obtieneRol
import com.example.carface_movil.utils.UTILS.obtieneToken
import com.example.carface_movil.utils.UTILS.saveTokenAndRoleToPreferences
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    lateinit var mainLayout:ConstraintLayout
    lateinit var blur:View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById(R.id.carga)
        progressBar.visibility=View.GONE
        redirigeSiExiste()
    }

    fun redirigeSiExiste(){
        if (obtieneToken(applicationContext) !="" && obtieneRol(applicationContext) !=""){
            if (obtieneRol(applicationContext)=="GUARDIA"){
                val intent = Intent(this, MenuPrincipal::class.java)
                startActivity(intent)
            }else if(obtieneRol(applicationContext)=="CHOFER"){
                val intent = Intent(this, MenuChofer::class.java)
                startActivity(intent)
            }else if(obtieneRol(applicationContext)=="ADMINISTRADOR"){
                val intent = Intent(this, MenuAdmin::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        mainLayout = findViewById(R.id.main_layout)
        if (hasFocus) {
            Constants.width = mainLayout.width
            Constants.heigth = mainLayout.height
        }
    }

    fun iniciaBlur(){
        progressBar.visibility=View.VISIBLE
        blur = findViewById(R.id.blurView)
        applyBlur(blur,mainLayout,25f)
    }

    fun detieneBlur(){
        progressBar.visibility = View.GONE
        blur.background = null
    }

    fun login(view: View) {
        iniciaBlur()
        val correo = findViewById<TextView>(R.id.correo)
        val clave = findViewById<TextView>(R.id.clave)
        val queue = Volley.newRequestQueue(this)
        try {
            val loginRequest =
                object : JsonObjectRequest(
                    Method.POST,
                    Constants.SERVER +"/sesion/login",
                    null,
                    Response.Listener { response ->
                        detieneBlur()
                        muestraMensaje(applicationContext,"Inicio de sesión exitoso")
                        saveTokenAndRoleToPreferences(applicationContext,response.getString("token"),response.getString("rol"))
                        redirigeMenu(response.getString("rol"))
                    },
                    Response.ErrorListener {
                        detieneBlur()
                        muestraMensaje(applicationContext,"Error al iniciar sesión")
                        System.out.println(it)
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers.put("Content-Type", "application/text; utf-8")
                        headers.put("correo", correo.text.toString())
                        headers.put("Accept", "*/*")
                        headers.put("clave", clave.text.toString())
                        return headers
                    }
                }
            queue.add(loginRequest)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun redirigeMenu(rol:String){
        if(rol=="GUARDIA"){
            val intent = Intent(this, MenuPrincipal::class.java)
            startActivity(intent)
        }else if(rol=="CHOFER"){
            val intent = Intent(this, MenuChofer::class.java)
            startActivity(intent)
        }
        else if(rol=="ADMINISTRADOR"){
            val intent = Intent(this, MenuAdmin::class.java)
            startActivity(intent)
        }
    }

    fun registro_chofer(view: View){
        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
    }
}