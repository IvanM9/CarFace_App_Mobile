package com.example.carface_movil.chofer

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carface_movil.R

class Adapter_Vehiculo(
    context_: Context,
    val userList: ArrayList<Cs_Vehiculo>
) : RecyclerView.Adapter<Adapter_Vehiculo.ViewHolder>() {
    val context: Context = context_

    @SuppressLint("MissingInflatedId")
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Adapter_Vehiculo.ViewHolder {
        val v =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.card_view_informacion, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: Adapter_Vehiculo.ViewHolder, i: Int) {
        viewHolder.tipo.text = userList[i].tipoVehiculo
        viewHolder.placa.text =  userList[i].placa
        viewHolder.marca.text =  userList[i].marca
        viewHolder.modelo.text =  userList[i].modelo
        viewHolder.anio.text =  userList[i].anio
        viewHolder.color.text =  userList[i].color
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var tipo: TextView
        var placa: TextView
        var marca: TextView
        var modelo: TextView
        var anio: TextView
        var color: TextView
        init {
            tipo=itemView.findViewById(R.id.cedula_guardia)
            placa=itemView.findViewById(R.id.correo_guardia)
            marca=itemView.findViewById(R.id.telefono_guardia)
            modelo=itemView.findViewById(R.id.nombre_guardia)
            anio=itemView.findViewById(R.id.anio)
            color=itemView.findViewById(R.id.color)
        }
    }



}