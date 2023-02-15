package com.example.pruebafirebase.Models

import java.util.Date

data class Mensaje(
    var emisor: String = "",
    var mensaje: String = "",
    var fechaEnvio: Date
)
