package com.example.carface_movil

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject

class InformacionPersonal : AppCompatActivity() {

    lateinit var ci:TextView
    lateinit var nombre:TextView
    lateinit var direccion:TextView
    lateinit var telefono:TextView
    lateinit var licencia:TextView
    var json:JSONObject= JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informacion_personal)
        var toolbar : Toolbar?= findViewById(R.id.toolbar_info);
        toolbar!!?.title="Información"
        toolbar.setTitleTextColor(Color.WHITE)
        val btnEditInfo = toolbar.findViewById<ImageButton>(R.id.btn_edit_info)
        setSupportActionBar(toolbar);
        //inicializa textviews
        ci=findViewById(R.id.ci_info);
        nombre=findViewById(R.id.nombre_info)
        direccion=findViewById(R.id.direccion_info)
        telefono=findViewById(R.id.telefono_info)
        licencia=findViewById(R.id.licencia_info)
        consultaInfo()
    }

    fun VisualizaCardview(userList: ArrayList<Cs_Vehiculo>){
        val recyclerView_ : RecyclerView =findViewById(R.id.recycler_vehiculos)
        val adapter_=Adapter_Vehiculo(this, userList)
        recyclerView_.layoutManager= LinearLayoutManager(this, RecyclerView.VERTICAL,false)
        recyclerView_.adapter=adapter_
        val resId = R.anim.layout_animation_down_to_up
        val animation = AnimationUtils.loadLayoutAnimation(
            applicationContext,
            resId
        )
        recyclerView_.layoutAnimation = animation
    }

    fun consultaInfo(){
        val queue = Volley.newRequestQueue(this)
        val sharedPreferences =
            getSharedPreferences("Settings", Context.MODE_PRIVATE);
        var token = sharedPreferences.getString("token", "")
        try {
            val loginRequest =
                object : JsonObjectRequest(
                    Request.Method.GET,
                    Constants.SERVER+"/chofer",
                    null,
                    Response.Listener { response ->
                        ci.text=response.getString("ci")
                        json.put("ci",response.getString("ci"))
                        json.put("id",response.getString("id"))
                        nombre.text=response.getString("nombre")+" "+response.getString("apellido")
                        json.put("nombre",response.getString("nombre"))
                        json.put("apellido",response.getString("apellido"))
                        direccion.text=response.getString("direccion")
                        json.put("direccion",response.getString("direccion"))
                        telefono.text=response.getString("telefono")
                        json.put("telefono",response.getString("telefono"))
                        licencia.text="Tipo "+response.getJSONObject("chofer").getString("licencia")
                        json.put("licencia",response.getJSONObject("chofer").getString("licencia"))
                        val vehiculo=response.getJSONObject("chofer").getJSONArray("vehiculo")
                        val listado = Cs_Vehiculo.JsonObjectsBuild(vehiculo)
                        VisualizaCardview(listado)
                    },
                    Response.ErrorListener {
                        if(it.networkResponse.statusCode==403){
                            val sharedPreferences =
                                getSharedPreferences("Settings", Context.MODE_PRIVATE);
                            val editor: SharedPreferences.Editor = sharedPreferences.edit();
                            editor.putString("token", "")
                            editor.putString("rol", "")
                            editor.putString("ip", "")
                            editor.apply();
                            Toast.makeText(
                                this,
                                "La sesión caducó, vuelva a ingresar por favor",
                                Toast.LENGTH_LONG
                            ).show();
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
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

    fun editaInfo(view:View){
        val intent = Intent(this, EditaInfoChofer::class.java)
        val gson = Gson()
        val json = gson.toJson(json)
        intent.putExtra("json",json)
        startActivity(intent)
    }

}