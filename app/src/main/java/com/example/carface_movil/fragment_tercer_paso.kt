package com.example.carface_movil

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
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
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_tercer_paso.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_tercer_paso : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewFinder: PreviewView
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var imageCounter: Int = 0
    private var imageList: ArrayList<String> = ArrayList()

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

    private lateinit var jsonObject: JSONObject

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
                    if (imageCounter == 5) {
                        photoFile.delete()


                        registroChofer(jsonObject)
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.putStringArrayListExtra("imageList", imageList)
                        startActivity(intent)
                    }
                    faceDetector.process(InputImage.fromBitmap(bitmap, 0)).addOnSuccessListener { faces ->
                        if (faces.isNotEmpty()) {
                            // At least one face was detected
                            // Increment the image counter
                            imageCounter++
                            // Add the photo file path to the image list
                            imageList.add(photoFile.absolutePath)
                        } else {
                            // No faces were detected
                            photoFile.delete();
                        }
                    }.addOnFailureListener { e ->
                        // Failed to detect faces
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

        val url = "http://192.168.0.104:8080/chofer"
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
        val url = "http://192.168.0.104:8080/sesion/login"
        try {
            val loginRequest =
                object : JsonObjectRequest(
                    Method.POST,
                    url,
                    null,
                    Response.Listener { response ->
                        Toast.makeText(requireContext(), "Inicio de sesión exitoso", Toast.LENGTH_LONG).show()
                        val sharedPreferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
                        val editor: SharedPreferences.Editor = sharedPreferences.edit();
                        editor.putString("token", response.getString("token"))
                        editor.putString("rol", response.getString("rol"))
                        editor.putString("ip", "192.168.0.104")
                        editor.apply();
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