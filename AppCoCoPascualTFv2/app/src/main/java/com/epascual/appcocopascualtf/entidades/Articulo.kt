package com.epascual.appcocopascualtf.entidades

import java.util.Date

data class Articulo(
    val art_id: Int,
    val art_nombre: String,
    val art_precio: Double,
    val art_fecha_registro: String,
    val art_imagen: String,
    val col_id: Int
)
