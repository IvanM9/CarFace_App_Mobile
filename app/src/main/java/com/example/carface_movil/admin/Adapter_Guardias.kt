package com.example.carface_movil.admin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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

class Adapter_Guardias(
    context_: Context,
    val userList: ArrayList<Cs_Guardia>
) : RecyclerView.Adapter<Adapter_Guardias.ViewHolder>() {
    val context: Context = context_
    var json: JSONObject = JSONObject()

    @SuppressLint("MissingInflatedId")
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Adapter_Guardias.ViewHolder {
        val v =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.card_view_guardias, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: Adapter_Guardias.ViewHolder, i: Int) {
        if (userList[i].estado){
            viewHolder.ci.text = "Cedula: "+userList[i].ci
            viewHolder.telefono.text =  "Telefono: "+userList[i].telefono
            viewHolder.empresa.text =  "Empresa: "+userList[i].empresa
            viewHolder.correo.text =  "Correo: "+userList[i].correo
            viewHolder.nombre.text =  "Nombre: "+userList[i].nombre+" "+userList[i].apellido
            viewHolder.editar.setOnClickListener{v: View ->
                val intent=Intent(context,EditarGuardias::class.java)
                llenJSON(userList[i])
                obtiene_info(userList[i].id){
                    val gson = Gson()
                    val json = gson.toJson(json)
                    intent.putExtra("json",json)
                    context.startActivity(intent)
                }
            }
            viewHolder.eliminar.setOnClickListener{v: View ->
                showConfirmationDialog(userList[i].id)
            }
        }else{

        }
    }

    fun llenJSON(guardia:Cs_Guardia){
        json.put("id", guardia.id)
        json.put("ci", guardia.ci)
        json.put("nombre", guardia.nombre)
        json.put("apellido", guardia.apellido)
        json.put("compania", guardia.empresa)

    }

    fun showConfirmationDialog(id:String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Esta acción borrará el dispositivo elegido. ¿Desea continuar?")
        builder.setPositiveButton("Sí") { dialog, which ->
            eliminarGuardia(id)
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
    fun obtiene_info(id:String, callback: () -> Unit){
        val queue = Volley.newRequestQueue(context)
        try {
            val loginRequest =
                object : JsonObjectRequest(
                    Method.GET,
                    Constants.SERVER +"/admin/usuario/id?id="+id,
                    null,
                    Response.Listener { response ->
                        var guardia=response.getJSONObject("guardia")
                        json.put("fecha_inicio",guardia.getString("fecha_inicio").substring(0,10))
                        json.put("fecha_fin",guardia.getString("fecha_fin").substring(0,10))
                        callback()
                    },
                    Response.ErrorListener {
                        System.out.println(it)
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers.put("Content-Type", "application/json")
                        headers.put("Accept", "*/*")
                        headers.put("Authorization","Bearer "+ UTILS.obtieneToken(context))
                        return headers
                    }
                }
            queue.add(loginRequest)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun eliminarGuardia(id:String){
        val queue = Volley.newRequestQueue(context)
        try {
            val loginRequest =
                object : StringRequest(
                    Method.PUT,
                    Constants.SERVER +"/guardia/"+id+"/false",
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
        var editar: ImageView
        var eliminar: ImageView

        init {
            ci=itemView.findViewById(R.id.cedula_guardia)
            nombre=itemView.findViewById(R.id.nombre_guardia)
            correo=itemView.findViewById(R.id.correo_guardia)
            telefono=itemView.findViewById(R.id.telefono_guardia)
            empresa=itemView.findViewById(R.id.empresa_guardia)
            editar=itemView.findViewById(R.id.editar_guardia)
            eliminar=itemView.findViewById(R.id.eliminar_guardia)
        }
    }



}