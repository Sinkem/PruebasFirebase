package com.example.pruebafirebase.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebafirebase.Models.Mensaje
import com.example.pruebafirebase.R

class MensajeAdapter(private val user: String): RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder>() {

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
            holder.itemView.otherMessageLayout.visibility = View.GONE
            holder.itemView.myMessageTextView.text = mensaje.mensaje
        } else {
            holder.itemView.myMessageLayout.visibility = View.GONE
            holder.itemView.otherMessageLayout.visibility = View.VISIBLE

            holder.itemView.othersMessageTextView.text = mensaje.mensaje
        }

    }

    override fun getItemCount(): Int {
        return mensajes.size
    }

    class MensajeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}