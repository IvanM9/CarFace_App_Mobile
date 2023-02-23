package com.example.carface_movil

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class CustomerAdapter_Solicitud constructor(context_: Context,
                                            val userList: ArrayList<JSONObject>) : RecyclerView.Adapter<CustomerAdapter_Solicitud.ViewHolder>() {
    val context: Context = context_

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CustomerAdapter_Solicitud.ViewHolder {
        val v =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.card_view_solicitud, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: CustomerAdapter_Solicitud.ViewHolder, i: Int) {

        viewHolder.ci.text = userList[i].getString("ci")
        viewHolder.nombre.text= userList[i].getString("nombres")+" "+userList[i].getString("apellidos")
        viewHolder.licencia.text= "Tipo de Licencia: "+userList[i].getString("tipo_licencia")
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var nombre: TextView
        var ci: TextView
        var licencia: TextView
        init {
            ci=itemView.findViewById(R.id.geek_item_articulo_id)
            nombre=itemView.findViewById(R.id.geek_item_articulo_doi)
            licencia=itemView.findViewById(R.id.geek_item_articulo_link)


            //Enviar Datos
            itemView.setOnClickListener{ v: View ->

            }
        }
    }



}