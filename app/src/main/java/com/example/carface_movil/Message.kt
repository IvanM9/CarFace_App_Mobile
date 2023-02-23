package com.example.carface_movil

import android.widget.Toast
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Message(a: JSONObject) {
    var nombres: String
    var apellidos: String
    var tipo_licencia: String
    var ci: String
    var vehiculos:JSONArray

    companion object {
        @Throws(JSONException::class)
        fun JsonObjectsBuild(datos: JSONArray): ArrayList<Message> {
            val publicacion: ArrayList<Message> = ArrayList<Message>()
            var i = 0
            while (i < datos.length()) {
                publicacion.add(Message(datos.getJSONObject(i)))
                i++
            }
            return publicacion
        }
    }

    init {
        nombres = a.getString("nombres").toString()
        tipo_licencia = a.getString("tipo_licencia").toString()
        apellidos = a.getString("apellidos").toString()
        ci=a.getString("ci").toString()
        vehiculos=a.getJSONArray("vehiculos")
    }

}