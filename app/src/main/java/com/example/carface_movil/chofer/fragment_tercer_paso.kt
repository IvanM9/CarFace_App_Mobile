package com.example.carface_movil.chofer

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.carface_movil.Constants
import com.example.carface_movil.session.MainActivity
import com.example.carface_movil.R
import com.example.carface_movil.utils.UTILS
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_tercer_paso.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_tercer_paso : Fragment() {

    private lateinit var viewFinder: PreviewView
    private var imageCapture: ImageCapture? = null
    private lateinit var progressH:ProgressBar
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var imageCounter: Int = 0
    private var imagenes: ArrayList<File> = ArrayList()
    private var jsonObject: JSONObject = JSONObject()
    private val faceDetector = FaceDetection.getClient()
    lateinit var progressBar: ProgressBar
    lateinit var mainLayout: ConstraintLayout
    lateinit var blur: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tercer_paso, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Obtener los valores almacenados en el Bundle
        obtenerValoresBundle(arguments)
        // Request camera permissions
        permisoCamara()
        iniciaCampos(view)
        tomaFotoCada5Sg()
    }

    fun obtenerValoresBundle(bundle: Bundle?){
        val cedula = bundle?.getString("cedula")
        val nombre = bundle?.getString("nombre")
        val apellido = bundle?.getString("apellido")
        val celular = bundle?.getString("celular")
        val tipoLicencia = bundle?.getString("tipoLicencia")
        val correo = bundle?.getString("correo")
        val clave = bundle?.getString("clave")

        jsonObject.put("ci", cedula)
        jsonObject.put("nombre", nombre)
        jsonObject.put("apellido", apellido)
        jsonObject.put("correo", correo)
        jsonObject.put("clave", clave)
        jsonObject.put("tipolicencia", tipoLicencia)
        jsonObject.put("direccion", null)
        jsonObject.put("telefono", celular)
    }

    fun iniciaCampos(view: View){
        progressH = view.findViewById(R.id.progressBarFotos_H)
        viewFinder = view.findViewById(R.id.view_finder)
        progressBar = view.findViewById(R.id.carga)
        progressBar.visibility=View.GONE
        mainLayout = view.findViewById(R.id.foto_layout)
        blur = view.findViewById(R.id.blurViewFoto)
        // Set up the directory where the photos will be stored
        outputDirectory = getOutputDirectory()
        // Set up the camera executor service
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun permisoCamara(){
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    fun tomaFotoCada5Sg(){
        // Creamos un Timer para ejecutar la función cada 1.5 segundos
        val timer = Timer()
        var contador = 0
        timer.schedule(object : TimerTask() {
            override fun run() {
                takePhoto()
                if (imageCounter == 5) {
                    timer.cancel() // Cancelamos el Timer después de 5 ejecuciones
                }
            }
        }, 0L, 1500L) // Ejecutamos la tarea cada 1.5 segundos
    }

    private fun startCamera() {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            configuraImagenCapturada()
            try {
                // Unbind any previous bindings before binding new use cases
                cameraProvider.unbindAll()
                // Bind all use cases to the camera
                cameraProvider.bindToLifecycle(
                    this,  CameraSelector.DEFAULT_FRONT_CAMERA, configuraPreviewCamara(), imageCapture
                )

            } catch (e: Exception) {
                Log.e(TAG, "Camera binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    fun configuraPreviewCamara():Preview{
        return Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }
    }

    fun configuraImagenCapturada(){
        val targetResolution = Size(1920, 1080)
        imageCapture = ImageCapture.Builder()
            .setJpegQuality(80)
            .setTargetResolution(targetResolution)
            .build()
    }

    private fun takePhoto() {

        val photoFile = File(outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture?.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()), object :
                ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    if (imagenes.size == 5) {
                        registroChofer(jsonObject)
                    }
                    if(bitmap!=null)
                        detectorRostros(bitmap,photoFile)
                }
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }
                })
    }

    fun detectorRostros(bitmap: Bitmap,photo:File){
        faceDetector.process(
            InputImage.fromBitmap(
                bitmap,
                0
            )
        ).addOnSuccessListener { faces ->
            if (faces.isNotEmpty()) {
                imageCounter++
                imagenes.add(photo)
                val progress = (imageCounter.toFloat() / 5) * 100
                progressH.progress = progress.toInt()
            } else {
                photo.delete()
            }
        }.addOnFailureListener { e ->
            photo.delete()
            Log.e(TAG, "Failed to detect faces: ${e.message}", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    fun iniciaBlur() {
        progressBar.visibility = View.VISIBLE
        UTILS.applyBlur(blur, mainLayout, 25f)
    }

    fun detieneBlur() {
        progressBar.visibility = View.GONE
        blur.background = null
    }

    fun registroChofer(jsonObject: JSONObject) {
        iniciaBlur()
        val queue = Volley.newRequestQueue(requireContext())
        val url = Constants.SERVER +"/chofer"
        try {
            val RegistroRequest =
                object : JsonObjectRequest(
                    Method.POST,
                    url,
                    jsonObject,
                    Response.Listener { response ->
                        System.out.println("SE REGISTRÓ")
                        login(jsonObject.getString("correo"),jsonObject.getString("clave"),queue)
                    },
                    Response.ErrorListener {
                        System.out.println("NO SE REGISTRÓ: "+it)
                        UTILS.muestraMensaje(requireContext(),"Error al iniciar sesión")
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers.put("Content-Type", "application/json; utf-8")
                        headers.put("Accept", "*/*")
                        return headers
                    }
                }
            queue.add(RegistroRequest)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun login(correo:String,clave:String,cola: RequestQueue) {
        val url = Constants.SERVER +"/sesion/login"
        try {
            val loginRequest =
                object : JsonObjectRequest(
                    Method.POST,
                    url,
                    null,
                    Response.Listener { response ->
                        UTILS.saveTokenAndRoleToPreferences(requireContext(),response.getString("token"),response.getString("rol"))
                        uploadImages()
                    },
                    Response.ErrorListener {
                        UTILS.muestraMensaje(requireContext(),"Error al Iniciar Sesión")
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers.put("Content-Type", "application/text; utf-8")
                        headers.put("correo", correo)
                        headers.put("Accept", "*/*")
                        headers.put("clave", clave)
                        return headers
                    }
                }
            cola.add(loginRequest)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun removeLastElement(){
        while (imagenes.size > 5) {
            imagenes[imagenes.lastIndex].delete()
            imagenes.removeAt(imagenes.lastIndex) // Elimina el último elemento del array
        }
    }

    fun creaRequest():Request{
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        imagenes.forEach { image ->
            val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), image)
            builder.addFormDataPart("files", image.name, requestFile)
        }
        val requestBody = builder.build()
        return Request.Builder()
            .url(Constants.SERVER +"/chofer/fotos")
            .put(requestBody)
            .addHeader("Authorization", "Bearer "+ UTILS.obtieneToken(requireContext()))
            .addHeader("Accept","*/*")
            .addHeader("Content-Type","multipart/form-data")
            .build()
    }

    fun uploadImages() {
        removeLastElement()
        val client = OkHttpClient().newBuilder()
            .callTimeout(3, TimeUnit.MINUTES)
            .build()
        try {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val response = client.newCall(creaRequest()).execute()
                    if (response.isSuccessful) {
                        requireActivity().runOnUiThread {
                            removeImages()
                            detieneBlur()
                            UTILS.muestraMensaje(requireContext(),"REGISTRADO CON EXITO")
                            UTILS.vuelveLogin(requireContext())
                        }
                    } else {
                        removeImages()
                        System.out.println("NO se agregaron")
                    }
                }
            }
        }catch (e:java.lang.Exception){
            System.out.println(e)
        }
    }

    fun removeImages(){
        while (imagenes.size != 0) {
            imagenes[imagenes.lastIndex].delete()
            imagenes.removeAt(imagenes.lastIndex)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }

    companion object {
        private const val TAG = "TercerPasoFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_tercer_paso.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_tercer_paso().apply {

            }
    }
}