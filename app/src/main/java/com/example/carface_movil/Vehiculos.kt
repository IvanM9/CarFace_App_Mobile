package com.example.carface_movil

import android.widget.Toast
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Vehiculos{
    var marca: String
    var color: String
    var modelo: String
    var placa: String

    constructor(marca: String, color: String, modelo: String, placa: String) {
        this.marca = marca
        this.color = color
        this.modelo = modelo
        this.placa = placa
    }
}