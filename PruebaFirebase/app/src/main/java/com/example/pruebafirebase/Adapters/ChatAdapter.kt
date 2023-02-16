package com.example.pruebafirebase.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebafirebase.Models.Chat
import com.example.pruebafirebase.R

class ChatAdapter(val chatClick: (Chat) -> Unit):RecyclerView.Adapter<ChatViewHolder>()  {
    var chats: List<Chat> = emptyList()

    fun setData(list: List<Chat>){
        chats = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_chat,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.binding.chatNombreTxT.text = chats[position].nombre
        holder.binding.usuarioTextView.text = chats[position].partcipantes.toString()

        holder.itemView.setOnClickListener {
            chatClick(chats[position])
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }
}