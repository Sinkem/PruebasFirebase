package com.example.pruebafirebase.Models

data class ChatModel(
    var id: String = "",
    var name: String = "",
    var partcipantes: List<String> = emptyList()
)
