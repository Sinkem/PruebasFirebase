package com.example.pruebafirebase.Models

data class Chat(
    var id: String = "",
    var nombre: String = "",
    var partcipantes: List<String> = emptyList()
)