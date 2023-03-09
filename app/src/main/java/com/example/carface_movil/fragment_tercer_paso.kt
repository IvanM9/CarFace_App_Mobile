package com.example.carface_movil

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.android.volley.AuthFailureError
import com.android.volley.Request
import  okhttp3.Request as RequestOK
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_tercer_paso.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_tercer_paso() : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewFinder: PreviewView
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var imageCounter: Int = 0
    private var imageList: ArrayList<String> = ArrayList()
    private var imagenes: ArrayList<File> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tercer_paso, container, false)
    }

    private var jsonObject: JSONObject= JSONObject()

    private val faceDetector = FaceDetection.getClient()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewFinder = view.findViewById(R.id.view_finder)

        // Obtener el Bundle del Fragment
        val bundle = arguments

        // Obtener los valores almacenados en el Bundle
        val cedula = bundle?.getString("cedula")
        val nombre = bundle?.getString("nombre")
        val apellido = bundle?.getString("apellido")
        val celular = bundle?.getString("celular")
        val tipoLicencia = bundle?.getString("tipoLicencia")
        val correo = bundle?.getString("correo")
        val clave = bundle?.getString("clave")

        jsonObject.put("ci", cedula);
        jsonObject.put("nombre", nombre);
        jsonObject.put("apellido", apellido);
        jsonObject.put("correo", correo);
        jsonObject.put("clave", clave);
        jsonObject.put("tipolicencia", tipoLicencia);
        jsonObject.put("direccion", null);
        jsonObject.put("telefono", celular);

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        // Set up the directory where the photos will be stored
        outputDirectory = getOutputDirectory()

        // Set up the camera executor service
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Set up the button listener to take photos
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

            // Set up the preview use case to display camera output
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            // Set up the image capture use case to capture photos
            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind any previous bindings before binding new use cases
                cameraProvider.unbindAll()

                // Bind all use cases to the camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (e: Exception) {
                Log.e(TAG, "Camera binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        // Create a timestamped output file to store the photo
        val photoFile = File(outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Set up the options to capture the photo
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture?.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()), object :
                ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Detect faces in the captured photo
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    // If five photos have been taken, go back to MainActivity with the image list
                    if (imageList.size == 5) {
                        photoFile.delete()
                        registroChofer(jsonObject)
                    }
                    faceDetector.process(InputImage.fromBitmap(bitmap, 0)).addOnSuccessListener { faces ->
                        if (faces.isNotEmpty()) {
                            // At least one face was detected
                            // Increment the image counter
                            imageCounter++
                            // Add the photo file path to the image list
                            imageList.add(photoFile.absolutePath)
                            imagenes.add(photoFile);
                        } else {
                            // No faces were detected
                            photoFile.delete();
                        }
                    }.addOnFailureListener { e ->
                        photoFile.delete()
                        Log.e(TAG, "Failed to detect faces: ${e.message}", e)
                    }



                }
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    fun registroChofer(jsonObject: JSONObject) {
        val queue = Volley.newRequestQueue(requireContext())
        val url = Constants.SERVER+"/chofer"
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
                        Toast.makeText(
                            requireContext(),
                            "Error al iniciar sesión",
                            Toast.LENGTH_LONG
                        ).show();
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers.put("Content-Type", "application/json; utf-8")
                        headers.put("Accept", "*/*")
                        return headers
                    }
                }
            queue.add(RegistroRequest);
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun login(correo:String,clave:String,cola: RequestQueue) {
        val url = Constants.SERVER+"/sesion/login"
        try {
            val loginRequest =
                object : JsonObjectRequest(
                    Method.POST,
                    url,
                    null,
                    Response.Listener { response ->
                        //Toast.makeText(requireContext(), "Inicio de sesión exitoso", Toast.LENGTH_LONG).show()
                        val snackbar =
                            view?.let { Snackbar.make(it, "Registrado correctamente", Snackbar.ANIMATION_MODE_FADE) }
                        if (snackbar != null) {
                            snackbar.show()
                        }
                        val sharedPreferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
                        val editor: SharedPreferences.Editor = sharedPreferences.edit();
                        editor.putString("token", response.getString("token"))
                        editor.putString("rol", response.getString("rol"))
                        editor.putString("ip", "192.168.0.104")
                        editor.apply();
                        uploadImages(response.getString("token"))
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            requireContext(),
                            "Error al iniciar sesión",
                            Toast.LENGTH_LONG
                        ).show();
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
            cola.add(loginRequest);
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun uploadImages(token:String) {
        while (imagenes.size > 5) {
            imagenes.removeAt(imagenes.lastIndex) // Elimina el último elemento del array
        }

        val maxSizeBytes = 4 * 1024 * 1024 // 4 MB en bytes

        val resizedImages = ArrayList<File>()
        for (imageFile in imagenes) {
            var outputStream: ByteArrayOutputStream? = null
            try {
                outputStream = ByteArrayOutputStream()
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                    BitmapFactory.decodeFile(imageFile.absolutePath, this)
                    val imageSizeBytes = outHeight * outWidth * 4 // 4 bytes por pixel (ARGB)
                    val maxDimension = Math.sqrt((maxSizeBytes / imageSizeBytes).toDouble()).toInt()
                    val imageHeight = Math.min(outHeight, maxDimension)
                    val imageWidth = Math.min(outWidth, maxDimension)
                    inSampleSize = calculateInSampleSize(this, imageWidth, imageHeight)
                    inJustDecodeBounds = false
                }
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                val imageBytes = outputStream.toByteArray()
                if (imageBytes.size <= maxSizeBytes) {
                    resizedImages.add(imageFile)
                    continue
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                outputStream?.close()
            }

            // Escalar la imagen hasta que tenga un tamaño menor o igual a 4 MB
            var scaleFactor = 0.9f
            while (outputStream != null && outputStream.size() > maxSizeBytes) {
                outputStream.reset()
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                    BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size(), this)
                    val imageHeight = Math.round(outHeight * scaleFactor)
                    val imageWidth = Math.round(outWidth * scaleFactor)
                    inSampleSize = calculateInSampleSize(this, imageWidth, imageHeight)
                    inJustDecodeBounds = false
                }
                val bitmap = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size(), options)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                scaleFactor *= 0.9f
            }

            if (outputStream != null && outputStream.size() <= maxSizeBytes) {
                // Guardar la imagen redimensionada en un archivo temporal
                val tempFile = createTempFile("resized_image_", ".jpg")
                tempFile.writeBytes(outputStream.toByteArray())
                resizedImages.add(tempFile)
            }
        }

        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)

        while (imagenes.size != 0) {

            imagenes[imagenes.lastIndex].delete()
            imagenes.removeAt(imagenes.lastIndex)
        }

        resizedImages.forEach { image ->
            val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), image)
            builder.addFormDataPart("files", image.name, requestFile)
        }

        val client = OkHttpClient().newBuilder()
            .callTimeout(3, TimeUnit.MINUTES)
            .build()

        val requestBody = builder.build()

        val request = RequestOK.Builder()
            .url(Constants.SERVER+"/chofer/fotos")
            .put(requestBody)
            .addHeader("Authorization", "Bearer "+token)
            .addHeader("Accept","*/*")
            .addHeader("Content-Type","multipart/form-data")
            .build()
        try {

            val loadingDialog = Dialog(requireContext())
            loadingDialog.setContentView(R.layout.loading_layout)
            loadingDialog.setCancelable(false)
            loadingDialog.show()

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(),"REGISTRADO CON EXITO",Toast.LENGTH_LONG).show()
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            intent.putStringArrayListExtra("imageList", imageList)
                            startActivity(intent)
                        }
                    } else {
                        System.out.println("NO se agregaron")
                    }
                }
                loadingDialog.dismiss()
            }
        }catch (e:java.lang.Exception){
            System.out.println(e)
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
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
        private const val PHOTO_EXTENSION = ".jpg"
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
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}