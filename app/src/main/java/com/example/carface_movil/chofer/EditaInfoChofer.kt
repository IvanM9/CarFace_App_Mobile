package com.example.carface_movil.chofer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.carface_movil.Constants
import com.example.carface_movil.R
import com.example.carface_movil.utils.UTILS
import com.google.gson.Gson
import org.json.JSONObject

class EditaInfoChofer : AppCompatActivity() {

    var jsonObject: JSONObject = JSONObject()
    lateinit var nombre: TextView
    lateinit var direccion: TextView
    lateinit var telefono: TextView
    lateinit var licencia: Spinner
    lateinit var apellido: TextView
    lateinit var id:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edita_info_chofer)
        inicializaCampos(intent.extras?.getString("json"))
        rellenaCampos()
    }

    fun guardaEdicion(view: View){
        llenaJSON()
        val queue = Volley.newRequestQueue(this)
        try {
            val loginRequest =
                object : JsonObjectRequest(
                    Method.PUT,
                    Constants.SERVER +"/chofer/"+id,
                    jsonObject,
                    Response.Listener { response ->
                        UTILS.muestraMensaje(applicationContext,"Información Actualizada Correctamente")
                        val intent = Intent(this, MenuChofer::class.java)
                        startActivity(intent)
                    },
                    Response.ErrorListener {
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
                        headers.put("Authorization","Bearer "+UTILS.obtieneToken(applicationContext))
                        return headers
                    }
                }
            queue.add(loginRequest)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun rellenaCampos(){
        val licenciaList = listOf("A", "B", "C")
        nombre.text=jsonObject.get("nombre").toString()
        direccion.text=jsonObject.get("direccion").toString()
        telefono.text=jsonObject.get("telefono").toString()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, licenciaList)
        licencia.adapter = adapter
        val indiceSeleccionado = licenciaList.indexOf(jsonObject.getString("licencia"))
        licencia.setSelection(indiceSeleccionado,true)
        apellido.text=jsonObject.get("apellido").toString()
    }

    fun inicializaCampos(json:String?){
        val gson = Gson()
        jsonObject = gson.fromJson(json, JSONObject::class.java)
        id=jsonObject.getString("id")
        nombre=findViewById(R.id.et_nombreeddisp)
        direccion=findViewById(R.id.et_direccion)
        telefono=findViewById(R.id.et_telefono)
        licencia=findViewById(R.id.sp_tipolicencia)
        apellido=findViewById(R.id.et_apellido)
    }

    fun llenaJSON(){
        jsonObject= JSONObject()
        jsonObject.put("nombre",nombre.text)
        jsonObject.put("apellido",apellido.text)
        jsonObject.put("direccion",direccion.text)
        jsonObject.put("telefono",telefono.text)
        jsonObject.put("tipolicencia",licencia.selectedItem.toString())
    }

}