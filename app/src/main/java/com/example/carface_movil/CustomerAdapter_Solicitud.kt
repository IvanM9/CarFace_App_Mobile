package com.example.carface_movil

import Persona
import WebSocketManager
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class CustomerAdapter_Solicitud constructor(context_: Context,
                                            val userList: ArrayList<Persona>) : RecyclerView.Adapter<CustomerAdapter_Solicitud.ViewHolder>() {
    val context: Context = context_

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CustomerAdapter_Solicitud.ViewHolder {
        val v =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.card_view_solicitud, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: CustomerAdapter_Solicitud.ViewHolder, i: Int) {

        viewHolder.ci.text = "Cédula: "+userList[i].ci
        viewHolder.nombre.text= userList[i].nombres+" "+userList[i].apellidos
        viewHolder.licencia.text= "Tipo de Licencia: "+userList[i].tipo_licencia
        viewHolder.vehiculos.text="Cantidad de Vehiculos: "+userList[i].vehiculos.size
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var nombre: TextView
        var ci: TextView
        var licencia: TextView
        var vehiculos:TextView
        init {
            ci=itemView.findViewById(R.id.ci_txt)
            nombre=itemView.findViewById(R.id.names_txt)
            licencia=itemView.findViewById(R.id.tipo_licencia_txt)
            vehiculos=itemView.findViewById(R.id.vehiculos_txt)

            //Enviar Datos
            itemView.setOnClickListener{ v: View ->
                try {
                    WebSocketManager.notificationManager.cancel(userList[position].id_chofer.toInt());
                }catch (ex:java.lang.Exception){
                    System.out.println(ex.message)
                }
                var position: Int = getAdapterPosition()
                    val intent = Intent(context, EscanearVehiculo::class.java);
                    intent.putExtra("persona", userList[position])
                    intent.putExtra("evento","SALIDA")
                    context.startActivity(intent);

            }
        }
    }



}