package com.example.carface_movil.admin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.carface_movil.Constants
import com.example.carface_movil.R
import com.example.carface_movil.utils.UTILS

class ListarGuardias : AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    lateinit var mainLayout: ConstraintLayout
    lateinit var blur: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_guardias)
        configToolbar()
        inicializaCampos()
    }

    fun configToolbar() {
        var toolbar: Toolbar? = findViewById(R.id.toolbar_listar_guardias)
        toolbar!!.title = "Guardias"
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
    }

    fun inicializaCampos() {
        progressBar = findViewById(R.id.carga)
        progressBar.visibility = View.GONE
        blur = findViewById(R.id.blurViewlistarguardias)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        mainLayout = findViewById(R.id.listar_guardias_layout)
        if (hasFocus) {
            Constants.width = mainLayout.width
            Constants.heigth = mainLayout.height
            consultaGuardias()
        }
    }

    fun consultaGuardias(){
        iniciaBlur()
        val queue = Volley.newRequestQueue(this)
        try {
            val obtieneGuardiasRequest =
                object : JsonArrayRequest(
                    Method.GET,
                    Constants.SERVER +"/guardia/all",
                    null,
                    Response.Listener { response ->
                        detieneBlur()
                        val listado = Cs_Guardia.JsonObjectsBuild(response)
                        VisualizaCardview(listado)
                    },
                    Response.ErrorListener {
                        detieneBlur()
                        if(it.networkResponse.statusCode==403){
                            UTILS.limpiaMemoria(applicationContext)
                            UTILS.muestraMensaje(applicationContext,"La sesión caducó, vuelva a ingresar por favor")
                            UTILS.vuelveLogin(applicationContext)
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
            queue.add(obtieneGuardiasRequest)
        } catch (e: Exception) {
            println(e.message)
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

    fun VisualizaCardview(userList: ArrayList<Cs_Guardia>) {
        val recyclerView_: RecyclerView = findViewById(R.id.recycler_listar_guardias)
        val adapter_ = Adapter_Guardias(this, userList)
        recyclerView_.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView_.adapter = adapter_
        val resId = R.anim.layout_animation_down_to_up
        val animation = AnimationUtils.loadLayoutAnimation(
            applicationContext,
            resId
        )
        recyclerView_.layoutAnimation = animation
    }

    override fun onBackPressed() {
        val intent= Intent(applicationContext,MenuAdmin::class.java)
        startActivity(intent)
    }
}