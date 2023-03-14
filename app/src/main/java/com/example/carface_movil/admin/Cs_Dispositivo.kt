package com.example.carface_movil.admin

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Cs_Dispositivo(a: JSONObject) {
    var ubicacion: String
    var codigo: String
    var correo: String
    var nombre: String
    var id:String
    companion object {
        @Throws(JSONException::class)
        fun JsonObjectsBuild(datos: JSONArray): ArrayList<Cs_Dispositivo> {
            val dispositivos: ArrayList<Cs_Dispositivo> = ArrayList<Cs_Dispositivo>()
            var i = 0
            while (i < datos.length()) {
                dispositivos.add(Cs_Dispositivo(datos.getJSONObject(i)))
                i++
            }
            return dispositivos
        }
    }

    init {
        ubicacion = a.getString("ubicacion").toString()
        codigo = a.getString("codigo").toString()
        correo = a.getString("correo").toString()
        nombre = a.getString("nombre").toString()
        id=a.getString("id")
    }
}