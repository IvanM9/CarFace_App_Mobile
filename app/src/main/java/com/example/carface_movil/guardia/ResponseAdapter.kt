package com.example.carface_movil.guardia

import Persona
import com.example.carface_movil.socket.WebSocketManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carface_movil.R

class ResponseAdapter constructor(context_: Context,
                                  val userList: ArrayList<Persona>) : RecyclerView.Adapter<ResponseAdapter.ViewHolder>() {
    val context: Context = context_

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.card_view_solicitud, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

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
            ci=itemView.findViewById(R.id.cedula_guardia)
            nombre=itemView.findViewById(R.id.correo_guardia)
            licencia=itemView.findViewById(R.id.nombre_guardia)
            vehiculos=itemView.findViewById(R.id.telefono_guardia)
            //Enviar Datos
            itemView.setOnClickListener{ v: View ->
                try {
                    WebSocketManager.notificationManager.cancel(userList[position].id_chofer.toInt())
                }catch (ex:java.lang.Exception){
                    System.out.println(ex.message)
                }
                var position: Int = adapterPosition
                    val intent = Intent(context, EscanearVehiculo::class.java)
                intent.putExtra("persona", userList[position])
                    intent.putExtra("evento","SALIDA")
                    context.startActivity(intent)

            }
        }
    }



}