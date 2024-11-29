package com.epascual.appcocopascualtf.entidades

data class Usuario(
    val usu_id: Int,
    val usu_nombre: String,
    val usu_correo: String,
    val usu_contraseña: String,
    val rol_id: Int
)
