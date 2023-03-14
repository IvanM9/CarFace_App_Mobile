package com.example.carface_movil.chofer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.carface_movil.R
import com.example.carface_movil.utils.UTILS

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_primer_paso.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_primer_paso : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento para mostrar los campos necesarios
        val view = inflater.inflate(R.layout.fragment_primer_paso, container, false)

        // Obtener una referencia a los campos de entrada en el layout
        val cedulaEditText = view.findViewById<EditText>(R.id.cedula_edit_text)
        val nombreEditText = view.findViewById<EditText>(R.id.nombre_edit_text)
        val apellidoEditText = view.findViewById<EditText>(R.id.apellido_edit_text)
        val celularEditText = view.findViewById<EditText>(R.id.celular_edit_text)
        val tipoLicenciaSpinner = view.findViewById<Spinner>(R.id.tipo_licencia_spinner)

        // Obtener una referencia al botón "Siguiente"
        val siguienteButton = view.findViewById<Button>(R.id.siguiente_button)

        // Configurar el adaptador para el spinner de tipo de licencia
        val tiposLicencia = resources.getStringArray(R.array.tipos_licencia)
        val tipoLicenciaAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tiposLicencia)
        tipoLicenciaSpinner.adapter = tipoLicenciaAdapter

        // Establecer el listener del botón "Siguiente"
        siguienteButton.setOnClickListener {
            // Validar los campos de entrada
            val cedula = cedulaEditText.text.toString()
            val nombre = nombreEditText.text.toString()
            val apellido = apellidoEditText.text.toString()
            val celular = celularEditText.text.toString()
            val tipoLicencia = tipoLicenciaSpinner.selectedItem.toString()

            if (cedula.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || celular.isEmpty()) {
                UTILS.muestraMensaje(requireContext(),"Por favor, complete todos los campos")
            } else {
                // Guardar la información ingresada en un Bundle y cargar el siguiente Fragment
                val bundle = Bundle().apply {
                    putString("cedula", cedula)
                    putString("nombre", nombre)
                    putString("apellido", apellido)
                    putString("celular", celular)
                    putString("tipoLicencia", tipoLicencia)
                }
                val siguientePasoFragment = fragment_segundo_paso().apply { arguments = bundle }
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
         * @return A new instance of fragment fragment_primer_paso.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_primer_paso().apply {

            }
    }
}