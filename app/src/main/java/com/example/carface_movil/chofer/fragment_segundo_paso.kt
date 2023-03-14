package com.example.carface_movil.chofer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.carface_movil.R

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_segundo_paso.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_segundo_paso : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento para mostrar los campos necesarios
        val view = inflater.inflate(R.layout.fragment_segundo_paso, container, false)

        // Obtener el Bundle del Fragment
        val bundle = arguments

        // Obtener los valores almacenados en el Bundle
        val cedula = bundle?.getString("cedula")
        val nombre = bundle?.getString("nombre")
        val apellido = bundle?.getString("apellido")
        val celular = bundle?.getString("celular")
        val tipoLicencia = bundle?.getString("tipoLicencia")

        // Obtener una referencia a los campos de entrada en el layout
        val correoEditText = view.findViewById<EditText>(R.id.correo_edit_text)
        val contrasenaEditText = view.findViewById<EditText>(R.id.contrasena_edit_text)
        val confirmacionContrasenaEditText = view.findViewById<EditText>(R.id.confirmacion_contrasena_edit_text)

        // Obtener una referencia al botón "Siguiente"
        val siguienteButton = view.findViewById<Button>(R.id.siguiente_button)

        // Establecer el listener del botón "Siguiente"
        siguienteButton.setOnClickListener {
            // Validar los campos de entrada
            val correo = correoEditText.text.toString()
            val contrasena = contrasenaEditText.text.toString()
            val confirmacionContrasena = confirmacionContrasenaEditText.text.toString()

            if (correo.isEmpty() || contrasena.isEmpty() || confirmacionContrasena.isEmpty()) {
                // Mostrar un mensaje de error si algún campo está vacío
                Toast.makeText(
                    requireContext(),
                    "Por favor, complete todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (contrasena != confirmacionContrasena) {
                // Mostrar un mensaje de error si la contraseña y la confirmación de contraseña no coinciden
                Toast.makeText(
                    requireContext(),
                    "La contraseña y la confirmación de contraseña no coinciden",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Guardar la información ingresada en un Bundle y cargar el siguiente Fragment
                val bundle = Bundle().apply {
                    putString("correo", correo)
                    putString("clave", contrasena)
                    putString("cedula", cedula)
                    putString("nombre", nombre)
                    putString("apellido", apellido)
                    putString("celular", celular)
                    putString("tipoLicencia", tipoLicencia)
                }
                Toast.makeText(requireContext(), "Usuario Registrado con Èxito", Toast.LENGTH_LONG).show()
                val siguientePasoFragment = fragment_tercer_paso().apply { arguments = bundle }
                val fragmentManager = parentFragmentManager
                fragmentManager.beginTransaction().replace(R.id.fragment_container, siguientePasoFragment).commit()
            }
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_segundo_paso.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_segundo_paso().apply {

            }
    }
}