package com.epascual.appcocopascualtf.entidades

data class Pedido(
    val ped_id: Int,
    val ped_cantidad: Int,
    val ped_fecha_registro: String,
    val art_id: Int,
    val usu_id: Int
)
