package com.example.carface_movil.admin

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Cs_Guardia(a: JSONObject) {
    var estado: Boolean
    var ci: String
    var correo: String
    var nombre: String
    var telefono: String
    var empresa: String
    var id:Long

    companion object {
        @Throws(JSONException::class)
        fun JsonObjectsBuild(datos: JSONArray): ArrayList<Cs_Guardia> {
            val dispositivos: ArrayList<Cs_Guardia> = ArrayList<Cs_Guardia>()
            var i = 0
            while (i < datos.length()) {
                dispositivos.add(Cs_Guardia(datos.getJSONObject(i)))
                i++
            }
            return dispositivos
        }
    }

    init {
        estado = a.getBoolean("estado")
        ci = a.getString("ci").toString()
        correo = a.getString("correo").toString()
        nombre = a.getString("nombre").toString()
        telefono = a.getString("telefono").toString()
        empresa = a.getString("empresa").toString()
        id=a.getLong("id")
    }
}