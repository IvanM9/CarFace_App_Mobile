package com.example.carface_movil.admin

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carface_movil.R

class Adapter_Guardias(
    context_: Context,
    val userList: ArrayList<Cs_Guardia>
) : RecyclerView.Adapter<Adapter_Guardias.ViewHolder>() {
    val context: Context = context_

    @SuppressLint("MissingInflatedId")
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Adapter_Guardias.ViewHolder {
        val v =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.card_view_guardias, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: Adapter_Guardias.ViewHolder, i: Int) {
        viewHolder.ci.text = "Ubicacion: "+userList[i].ci
        viewHolder.telefono.text =  "Codigo: "+userList[i].telefono
        viewHolder.empresa.text =  "Codigo: "+userList[i].empresa
        viewHolder.correo.text =  "Correo: "+userList[i].correo
        viewHolder.nombre.text =  "Nombre: "+userList[i].nombre
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var ci: TextView
        var telefono: TextView
        var empresa: TextView
        var correo: TextView
        var nombre: TextView
        init {
            ci=itemView.findViewById(R.id.cedula_guardia)
            nombre=itemView.findViewById(R.id.nombre_guardia)
            correo=itemView.findViewById(R.id.correo_guardia)
            telefono=itemView.findViewById(R.id.telefono_guardia)
            empresa=itemView.findViewById(R.id.empresa_guardia)
        }
    }



}