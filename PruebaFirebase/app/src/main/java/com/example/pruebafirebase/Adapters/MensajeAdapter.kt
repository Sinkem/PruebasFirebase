package com.example.pruebafirebase.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebafirebase.Models.Mensaje
import com.example.pruebafirebase.R

class MensajeAdapter(private val user: String): RecyclerView.Adapter<MensajeViewHolder>() {

    private var mensajes: List<Mensaje> = emptyList()

    fun setData(list: List<Mensaje>){
        mensajes = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensajeViewHolder {
        return MensajeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_mensaje,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MensajeViewHolder, position: Int) {
        val mensaje = mensajes[position]

        if(user == mensaje.emisor){
            holder.binding.miMensajeTxt.visibility = View.VISIBLE
            holder.binding.otroMensajeLayout.visibility = View.GONE

            holder.binding.miMensajeTxt.text = mensaje.mensaje
        } else {

            holder.binding.otroMensajeLayout.visibility = View.VISIBLE
            holder.binding.miMensajeTxt.visibility = View.GONE

            holder.binding.otroMensajeTxt.text = mensaje.mensaje
        }

    }

    override fun getItemCount(): Int {
        return mensajes.size
    }

}