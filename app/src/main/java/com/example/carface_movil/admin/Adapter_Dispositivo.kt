package com.example.carface_movil.admin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.carface_movil.Constants
import com.example.carface_movil.R
import com.example.carface_movil.utils.UTILS
import com.google.gson.Gson
import org.json.JSONObject

class Adapter_Dispositivo(
    context_: Context,
    val userList: ArrayList<Cs_Dispositivo>
) : RecyclerView.Adapter<Adapter_Dispositivo.ViewHolder>() {
    val context: Context = context_
    var json: JSONObject = JSONObject()

    @SuppressLint("MissingInflatedId")
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Adapter_Dispositivo.ViewHolder {
        val v =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.card_view_dispositivos, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: Adapter_Dispositivo.ViewHolder, i: Int) {
        viewHolder.ubicacion.text = "Ubicacion: "+userList[i].ubicacion
        viewHolder.codigo.text =  "Codigo: "+userList[i].codigo
        viewHolder.correo.text =  "Correo: "+userList[i].correo
        viewHolder.nombre.text =  "Nombre: "+userList[i].nombre
        viewHolder.editar.setOnClickListener{v: View ->
            val intent=Intent(context,EditarDispositivos::class.java)
            llenJSON(userList[i])
            val gson = Gson()
            val json = gson.toJson(json)
            intent.putExtra("json",json)
            context.startActivity(intent)
        }
        viewHolder.eliminar.setOnClickListener{v: View ->
            showConfirmationDialog(userList[i].id)
        }
    }

    fun showConfirmationDialog(id:String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Esta acción borrará el dispositivo elegido. ¿Desea continuar?")
        builder.setPositiveButton("Sí") { dialog, which ->
            borraDispositivo(id)
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun borraDispositivo(id:String){
        val queue = Volley.newRequestQueue(context)
        try {
            val loginRequest =
                object : StringRequest(
                    Method.DELETE,
                    Constants.SERVER +"/dispositivo/"+id,
                    Response.Listener { response:String ->
                        UTILS.muestraMensaje(context,"Eliminado Correctamente")
                    },
                    Response.ErrorListener {
                        if (it.networkResponse.statusCode==403){
                            UTILS.muestraMensaje(context,"Por favor inicie sesión nuevamente")
                            UTILS.limpiaMemoria(context)
                            UTILS.vuelveLogin(context)
                        }else{
                            UTILS.muestraMensaje(context,"Ocurrio un error al borrar el dispositivo")
                        }
                        System.out.println(it)
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers.put("Content-Type", "application/json")
                        headers.put("Accept", "*/*")
                        headers.put("Authorization","Bearer "+UTILS.obtieneToken(context))
                        return headers
                    }
                }
            queue.add(loginRequest)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun llenJSON(dispositivo:Cs_Dispositivo){
        json.put("id", dispositivo.id)
        json.put("ubicacion", dispositivo.ubicacion)
        json.put("codigo", dispositivo.codigo)
        json.put("correo", dispositivo.correo)
        json.put("nombre", dispositivo.nombre)
    }
    override fun getItemCount(): Int {
        return userList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var ubicacion: TextView
        var codigo: TextView
        var correo: TextView
        var nombre: TextView
        var editar: ImageView
        var eliminar: ImageView
        init {
            ubicacion=itemView.findViewById(R.id.cedula_guardia)
            nombre=itemView.findViewById(R.id.correo_guardia)
            correo=itemView.findViewById(R.id.telefono_guardia)
            codigo=itemView.findViewById(R.id.nombre_guardia)
            editar=itemView.findViewById(R.id.editar)
            eliminar=itemView.findViewById(R.id.eliminar)
        }
    }



}