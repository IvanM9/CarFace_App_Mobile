package com.example.carface_movil.admin

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Cs_Guardia(a: JSONObject) {
    var estado: Boolean
    var ci: String
    var correo: String
    var nombre: String
    var apellido: String
    var telefono: String
    var empresa: String
    var id:String

    companion object {
        @Throws(JSONException::class)
        fun JsonObjectsBuild(datos: JSONArray): ArrayList<Cs_Guardia> {
            val dispositivos: ArrayList<Cs_Guardia> = ArrayList<Cs_Guardia>()
            var i = 0
            while (i < datos.length()) {
                System.out.println("Dato # "+i+": "+datos.getJSONObject(i))
                if(datos.getJSONObject(i).getBoolean("estado")){
                    dispositivos.add(Cs_Guardia(datos.getJSONObject(i)))
                }
                i++
            }
            return dispositivos
        }
    }

    init {
        estado = a.getBoolean("estado")
        ci = a.getString("ci").toString()
        correo = a.getString("correo").toString()
        nombre = a.getString("nombres").toString()
        apellido = a.getString("apellidos").toString()
        telefono = a.getString("telefono").toString()
        empresa = a.getString("empresa").toString()
        id=a.getString("id")
    }
}