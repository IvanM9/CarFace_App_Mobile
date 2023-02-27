package com.example.carface_movil

import Persona
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.carface_movil.databinding.ActivityEscanearVehiculoBinding
import com.google.gson.Gson
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import org.json.JSONObject

class EscanearVehiculo : AppCompatActivity() {

    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var persona:Persona
    lateinit var evento:String
    lateinit var binding: ActivityEscanearVehiculoBinding
    private val REQUEST_IMAGE_CAPTURE=1
    private var imageBitmap: Bitmap? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_escanear_vehiculo)
        sharedPreferences = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        if(intent.hasExtra("persona")) {
            persona = intent.getParcelableExtra<Persona>("persona")!!
        }
        var sock = sharedPreferences.getString("token", "defaultValue")
            ?.let {
                System.out.println(it)
            }
        evento= intent.getStringExtra("evento").toString();
        binding= DataBindingUtil.setContentView(this,R.layout.activity_escanear_vehiculo)
        binding.apply {
            escanear.setOnClickListener {
                takeImage()
                respuesta.text = ""
            }
            button6.setOnClickListener {
                processImage()
            }
        }
    }

    private fun takeImage(){
        val intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(intent,REQUEST_IMAGE_CAPTURE)
        }
        catch (e:Exception){
        }
    }

    private fun processImage(){
        if (imageBitmap!=null) {
            val image = imageBitmap?.let {
                InputImage.fromBitmap(it, 0)
            }
            image?.let {
                recognizer.process(it)
                    .addOnSuccessListener { visionText ->
                        val url = "http://192.168.0.100:8080";
                        if (evento=="SALIDA"){
                            val OCR=visionText.text.replace("\n","").replace(" ","-").replace(":","-").replace(";","-");
                            var existe_vehiculo=false;
                            var num_vehiculo=0
                            for(i in 0..persona.vehiculos.size-1){
                                if(OCR.lowercase().contains(persona.vehiculos[i].placa.toLowerCase())){
                                    existe_vehiculo=true
                                    num_vehiculo=i
                                }
                            }
                            if (existe_vehiculo==true){
                                binding.respuesta.text = "Placa: "+persona.vehiculos[num_vehiculo].placa+"\n"+
                                        "Color: "+persona.vehiculos[num_vehiculo].color+"\n"+
                                        "Modelo: "+persona.vehiculos[num_vehiculo].modelo+"\n"+
                                        "Marca: "+persona.vehiculos[num_vehiculo].marca
                                binding.registroSalida.setOnClickListener{
                                    var observ=binding.editTextTextPersonName.text.toString();
                                    if(observ==""){
                                        observ="CORRECTO";
                                    }
                                    val jsonObject = JSONObject()
                                    jsonObject.put("id_vehiculo", persona.vehiculos[num_vehiculo].id_vehiculo)
                                    jsonObject.put("id_chofer", persona.id_chofer)
                                    jsonObject.put("observaciones", observ)
                                    jsonObject.put("tipo", "SALIDA")
                                    registrar_salida(url,jsonObject)
                                }
                            }else{
                                Toast.makeText(this,"No se encontro coincidencias",Toast.LENGTH_SHORT).show()
                            }
                        }else if(evento=="ENTRADA"){
                            val OCR=visionText.text.replace("\n","").replace(" ","-").replace(":","-").replace(";","-");
                            System.out.println(OCR);
                            Toast.makeText(this,verificarFormato(OCR),Toast.LENGTH_LONG).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this,"Ocurrio un error durante el escaneo",Toast.LENGTH_SHORT).show()
                    }
            }
        }
        else{
            Toast.makeText(this, "Please select photo", Toast.LENGTH_SHORT).show()
        }
    }

    fun registrar_salida(url:String,jsonRequest:JSONObject){
        val queue = Volley.newRequestQueue(this)
        val sharedPreferences =
            getSharedPreferences("Settings", Context.MODE_PRIVATE);
        var token:String=""
        var sock = sharedPreferences.getString("token", "defaultValue")
            ?.let {
                token=it;
            }
        try {
            System.out.println(jsonRequest)
            val loginRequest =
                object : JsonObjectRequest(
                    Request.Method.POST,
                    url+"/registro",
                    jsonRequest,
                    Response.Listener { response ->
                        Toast.makeText(
                            this,
                            "Se registró correctamente la salida del vehiculo",
                            Toast.LENGTH_LONG
                        ).show();
                        quitarVehiculo(persona);
                        val intent = Intent(this, Solicitud::class.java)
                        startActivity(intent)
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            this,
                            "Error al registrar salida del vehiculo",
                            Toast.LENGTH_LONG
                        ).show();
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers.put("Content-Type", "application/json")
                        headers.put("Accept", "*/*")
                        //headers.put("Authorization","Bearer "+token)
                        return headers
                    }
                }
            queue.add(loginRequest);
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun verificarFormato(texto: String): String {
        val formato1 = Regex("[a-zA-Z]{3}-\\d{4}")
        val formato2 = Regex("[a-zA-Z]{3}-\\d{3}")
        val formato3 = Regex("[a-zA-Z]{2}\\d{3}[a-zA-Z]{1}")
        var resultado = ""
        try {
            for (i in 0 until texto.length) {
                if(i+8<=texto.length){
                    val subcadena1 = texto.substring(i, i + 8)
                    if (formato1.matches(subcadena1)) {
                        resultado = subcadena1
                        System.out.println("CARRO CON 8 DIGITOS")
                        break
                    }
                }
                if (i+7<=texto.length){
                    val subcadena2 = texto.substring(i, i + 7)
                    if (formato2.matches(subcadena2)) {
                        resultado = subcadena2
                        System.out.println("CARRO CON 7 DIGITOS")
                        break
                    }
                }
                if(i+6<=texto.length){
                    val subcadena3 = texto.substring(i, i + 6)
                    if (formato3.matches(subcadena3)) {
                        resultado = subcadena3
                        System.out.println("MOTO")
                        break
                    }
                }
            }
        }catch (e:java.lang.Exception){
            System.out.println(e)
        }

        return if (resultado.isNotEmpty()) {
            resultado
        } else {
            "No se encontró ningún formato válido."
        }
    }

    fun quitarVehiculo(persona: Persona){
        try {
            WebSocketData.data.remove(persona);
            val gson = Gson()
            val json = gson.toJson(WebSocketData.data)
            with(sharedPreferences.edit()) {
                putString("data", json)
                apply()
            }
        }catch (ex:java.lang.Exception){
            Log.e("QUITAR VEHICULO",ex.message.toString())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode== RESULT_OK){
            val extras: Bundle? = data?.extras
            imageBitmap= extras?.get("data") as Bitmap
            if (imageBitmap!=null) {
                binding.imageView.setImageBitmap(imageBitmap)
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, Solicitud::class.java);
        startActivity(intent);
    }

    fun volver_menu(view:View){
        val intent = Intent(this, Solicitud::class.java);
        startActivity(intent);
    }
}