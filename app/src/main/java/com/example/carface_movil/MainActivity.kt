package com.example.carface_movil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun login(view: View){
        val correo=findViewById<TextView>(R.id.correo);
        val clave = findViewById<TextView>(R.id.clave);
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.1.102:8080/sesion/login"

        var loginRequest = object : StringRequest(
            Method.POST,url,
            { response ->
                val mensaje_error=Toast.makeText(this,"Token generado:"+response,Toast.LENGTH_LONG);
                mensaje_error.show();
                val intent = Intent(this, MenuPrincipal::class.java);
                startActivity(intent);
            },
            { error ->
                val mensaje_error=Toast.makeText(this,"Error al conectar el servidor",Toast.LENGTH_LONG);
                mensaje_error.show();
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String>? {
                val headers = HashMap<String, String>()
                headers.put("Content-Type", "application/text; utf-8")
                headers.put("correo", correo.text.toString())
                headers.put("Accept", "*/*")
                headers.put("clave",clave.text.toString())
                return headers
            }
        }
        queue.add(loginRequest);
    }
}