package com.example.carface_movil

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class RegistraVehiculo : AppCompatActivity() {

    var jsonObject:JSONObject= JSONObject()
    lateinit var placa: TextView
    lateinit var marca: TextView
    lateinit var modelo: TextView
    lateinit var tipoVehiculo: Spinner
    lateinit var color: TextView
    lateinit var anio:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registra_vehiculo)

        placa=findViewById(R.id.et_placa)
        marca=findViewById(R.id.et_marca)
        modelo=findViewById(R.id.et_modelo)
        tipoVehiculo=findViewById(R.id.sp_tipoVehiculo)
        anio=findViewById(R.id.et_año)
        color=findViewById(R.id.et_color)

    }

    fun registraVehiculo(view: View){
        jsonObject= JSONObject()
        jsonObject.put("placa",placa.text.toString().uppercase())
        jsonObject.put("marca",marca.text)
        jsonObject.put("modelo",modelo.text)
        jsonObject.put("año",anio.text)
        jsonObject.put("tipoVehiculo",tipoVehiculo.selectedItem.toString().uppercase())
        jsonObject.put("color",color.text)

        val queue = Volley.newRequestQueue(this)
        val sharedPreferences =
            getSharedPreferences("Settings", Context.MODE_PRIVATE);
        var token = sharedPreferences.getString("token", "")
        try {
            val loginRequest =
                object : JsonObjectRequest(
                    Request.Method.POST,
                    Constants.SERVER+"/vehiculo",
                    jsonObject,
                    Response.Listener { response ->
                        Toast.makeText(
                            this,
                            "Vehiculo Ingresado Correctamente",
                            Toast.LENGTH_LONG
                        ).show();
                        val intent = Intent(this, MenuChofer::class.java)
                        startActivity(intent)
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
}

