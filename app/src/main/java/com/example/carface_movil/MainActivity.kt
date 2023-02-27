package com.example.carface_movil

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import dev.icerock.moko.socket.Socket
import dev.icerock.moko.socket.SocketEvent
import dev.icerock.moko.socket.SocketOptions
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreferences =
            getSharedPreferences("Settings", Context.MODE_PRIVATE);
        var token:String=""
        var sock = sharedPreferences.getString("token", "")
            ?.let {
                token=it;
            }
        if (token!=""){
            val intent = Intent(this, MenuPrincipal::class.java);
            startActivity(intent);
        }

    }

    fun login(view: View) {
        val correo = findViewById<TextView>(R.id.correo);
        val clave = findViewById<TextView>(R.id.clave);
        val queue = Volley.newRequestQueue(this)

        val url = "http://192.168.0.100:8080/sesion/login"
        try {
            val loginRequest =
                object : JsonObjectRequest(
                    Method.POST,
                    url,
                    null,
                    Response.Listener { response ->
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show()
                        val sharedPreferences =
                            getSharedPreferences("Settings", Context.MODE_PRIVATE);
                        val editor: SharedPreferences.Editor = sharedPreferences.edit();
                        editor.putString("token", response.getString("token"))
                        editor.putString("ip", "192.168.0.100")
                        editor.apply();
                        val intent = Intent(this, MenuPrincipal::class.java);
                        startActivity(intent);
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            this,
                            "Error al iniciar sesión",
                            Toast.LENGTH_LONG
                        ).show();
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