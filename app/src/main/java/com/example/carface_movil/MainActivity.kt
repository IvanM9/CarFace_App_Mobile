package com.example.carface_movil

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun login(view: View) {
        val correo = findViewById<TextView>(R.id.correo);
        val clave = findViewById<TextView>(R.id.clave);
        val queue = Volley.newRequestQueue(this)
        val ip = findViewById<TextView>(R.id.txt_ip);

        val url = "http://" + ip.text + ":8080/sesion/login"
        try {
            val loginRequest =
                object : JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    null,
                    Response.Listener { response ->
                        val mensaje_error =
                            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_LONG);
                        mensaje_error.show();
                        val sharedPreferences =
                            getSharedPreferences("Settings", Context.MODE_PRIVATE);
                        val editor: SharedPreferences.Editor = sharedPreferences.edit();
                        editor.putString("token", "response")
                        editor.putString("ip", ip.text.toString())
                        editor.apply();
                        val intent = Intent(this, Solicitud::class.java);
                        startActivity(intent);
                    },
                    Response.ErrorListener {
                        val mensaje_error = Toast.makeText(
                            this,
                            "Error al iniciar sesión",
                            Toast.LENGTH_LONG
                        );
                        mensaje_error.show();
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
            queue.add(loginRequest);
        } catch (e: Exception) {
            println(e.message)
        }
    }
}