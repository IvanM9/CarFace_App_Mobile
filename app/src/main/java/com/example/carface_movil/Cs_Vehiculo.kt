package com.example.carface_movil

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Cs_Vehiculo(a: JSONObject) {
    var tipoVehiculo: String
    var placa: String
    var marca: String
    var modelo: String
    var color:String
    var anio:String

    companion object {
        @Throws(JSONException::class)
        fun JsonObjectsBuild(datos: JSONArray): ArrayList<Cs_Vehiculo> {
            val volumen: ArrayList<Cs_Vehiculo> = ArrayList<Cs_Vehiculo>()
            var i = 0
            while (i < datos.length()) {
                volumen.add(Cs_Vehiculo(datos.getJSONObject(i)))
                i++
            }
            return volumen
        }
    }

    init {
        tipoVehiculo = a.getString("tipoVehiculo").toString()
        placa = a.getString("placa").toString()
        marca = a.getString("marca").toString()
        color = a.getString("color").toString()
        modelo=a.getString("modelo").toString()
        anio=a.getString("anio").toString()
    }
}