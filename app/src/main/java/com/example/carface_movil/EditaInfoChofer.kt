package com.example.carface_movil

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject

class EditaInfoChofer : AppCompatActivity() {

    var jsonObject:JSONObject= JSONObject()
    lateinit var nombre: TextView
    lateinit var direccion: TextView
    lateinit var telefono: TextView
    lateinit var licencia: Spinner
    lateinit var apellido: TextView
    lateinit var id:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edita_info_chofer)

        val gson = Gson()
        val json = intent.extras?.getString("json")
        jsonObject = gson.fromJson(json, JSONObject::class.java)
        id=jsonObject.getString("id")
        nombre=findViewById(R.id.et_nombre)
        direccion=findViewById(R.id.et_direccion)
        telefono=findViewById(R.id.et_telefono)
        licencia=findViewById(R.id.sp_tipolicencia)
        apellido=findViewById(R.id.et_apellido)

        rellenaCampos()
    }

    fun guardaEdicion(view: View){
        jsonObject= JSONObject()
        jsonObject.put("nombre",nombre.text)
        jsonObject.put("apellido",apellido.text)
        jsonObject.put("direccion",direccion.text)
        jsonObject.put("telefono",telefono.text)
        jsonObject.put("tipolicencia",licencia.selectedItem.toString())
        val queue = Volley.newRequestQueue(this)
        val sharedPreferences =
            getSharedPreferences("Settings", Context.MODE_PRIVATE);
        var token = sharedPreferences.getString("token", "")
        try {
            val loginRequest =
                object : JsonObjectRequest(
                    Request.Method.PUT,
                    Constants.SERVER+"/chofer/"+id,
                    jsonObject,
                    Response.Listener { response ->
                        Toast.makeText(
                            this,
                            "Información Actualizada Correctamente",
                            Toast.LENGTH_LONG
                        ).show();
                        val intent = Intent(this, MenuChofer::class.java)
                        startActivity(intent)
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            this,
                            "Ocurrió un error al registrar la información",
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

}