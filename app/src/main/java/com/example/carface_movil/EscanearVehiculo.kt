package com.example.carface_movil

import Persona
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.carface_movil.databinding.ActivityEscanearVehiculoBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class EscanearVehiculo : AppCompatActivity() {

    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    lateinit var persona:Persona

    lateinit var binding: ActivityEscanearVehiculoBinding

    private val REQUEST_IMAGE_CAPTURE=1

    private var imageBitmap: Bitmap? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_escanear_vehiculo)

        persona = intent.getParcelableExtra<Persona>("persona")!!

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
                        System.out.println(visionText.text);
                        val OCR=visionText.text.replace("\n","");
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
                        }else{
                            Toast.makeText(this,"No se encontro coincidencias",Toast.LENGTH_SHORT)
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this,"Ocurrio un error durante el escaneo",Toast.LENGTH_SHORT)
                    }
            }
        }
        else{
            Toast.makeText(this, "Please select photo", Toast.LENGTH_SHORT).show()
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

    fun volver_menu(view:View){
        val intent = Intent(this, Solicitud::class.java);
        startActivity(intent);
    }
}