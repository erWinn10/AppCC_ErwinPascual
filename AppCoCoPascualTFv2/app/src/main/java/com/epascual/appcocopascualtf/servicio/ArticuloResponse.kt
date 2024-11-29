package com.epascual.appcocopascualtf.servicio

import com.epascual.appcocopascualtf.entidades.Articulo
import com.google.gson.annotations.SerializedName

data class ArticuloResponse(
    @SerializedName("lstArticulo") var lstArticulo: ArrayList<Articulo>

)
