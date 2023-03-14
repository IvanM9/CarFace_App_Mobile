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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
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