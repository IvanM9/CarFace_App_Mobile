package com.example.carface_movil.chofer

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.carface_movil.Constants
import com.example.carface_movil.R
import com.example.carface_movil.utils.UTILS
import com.google.gson.Gson
import org.json.JSONObject

class InformacionPersonal : AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    lateinit var mainLayout: ConstraintLayout
    lateinit var blur: View
    lateinit var ci: TextView
    lateinit var nombre: TextView
    lateinit var direccion: TextView
    lateinit var telefono: TextView
    lateinit var licencia: TextView
    var json: JSONObject = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informacion_personal)
        configToolbar()
        //inicializa textviews
        inicializaCampos()

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        mainLayout = findViewById(R.id.info_layout)
        if (hasFocus) {
            Constants.width = mainLayout.width
            Constants.heigth = mainLayout.height
            consultaInfo()
        }
    }

    fun inicializaCampos() {
        ci = findViewById(R.id.ci_info)
        nombre = findViewById(R.id.nombre_info)
        direccion = findViewById(R.id.direccion_info)
        telefono = findViewById(R.id.telefono_info)
        licencia = findViewById(R.id.licencia_info)
        progressBar = findViewById(R.id.carga)
        progressBar.visibility = View.GONE
        blur = findViewById(R.id.blurViewInfo)
    }

    fun VisualizaCardview(userList: ArrayList<Cs_Vehiculo>) {
        val recyclerView_: RecyclerView = findViewById(R.id.recycler_vehiculos)
        val adapter_ = Adapter_Vehiculo(this, userList)
        recyclerView_.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView_.adapter = adapter_
        val resId = R.anim.layout_animation_down_to_up
        val animation = AnimationUtils.loadLayoutAnimation(
            applicationContext,
            resId
        )
        recyclerView_.layoutAnimation = animation
    }

    fun configToolbar() {
        var toolbar: Toolbar? = findViewById(R.id.toolbar_info)
        toolbar!!.title = "Información"
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
    }

    fun iniciaBlur() {
        progressBar.visibility = View.VISIBLE
        UTILS.applyBlur(blur, mainLayout, 25f)
    }

    fun detieneBlur() {
        progressBar.visibility = View.GONE
        blur.background = null
    }

    fun llenaJSON(response: JSONObject) {
        ci.text = response.getString("ci")
        json.put("ci", response.getString("ci"))
        json.put("id", response.getString("id"))
        nombre.text = response.getString("nombre") + " " + response.getString("apellido")
        json.put("nombre", response.getString("nombre"))
        json.put("apellido", response.getString("apellido"))
        direccion.text = response.getString("direccion")
        json.put("direccion", response.getString("direccion"))
        telefono.text = response.getString("telefono")
        json.put("telefono", response.getString("telefono"))
        licencia.text = "Tipo " + response.getJSONObject("chofer").getString("licencia")
        json.put("licencia", response.getJSONObject("chofer").getString("licencia"))
    }

    fun consultaInfo(){
        iniciaBlur()
        val queue = Volley.newRequestQueue(this)
        try {
            val loginRequest =
                object : JsonObjectRequest(
                    Method.GET,
                    Constants.SERVER +"/chofer",
                    null,
                    Response.Listener { response ->
                        llenaJSON(response)
                        detieneBlur()
                        val vehiculo=response.getJSONObject("chofer").getJSONArray("vehiculo")
                        val listado = Cs_Vehiculo.JsonObjectsBuild(vehiculo)
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
            queue.add(loginRequest)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun editaInfo(view: View){
        val intent = Intent(this, EditaInfoChofer::class.java)
        val gson = Gson()
        val json = gson.toJson(json)
        intent.putExtra("json",json)
        startActivity(intent)
    }

}