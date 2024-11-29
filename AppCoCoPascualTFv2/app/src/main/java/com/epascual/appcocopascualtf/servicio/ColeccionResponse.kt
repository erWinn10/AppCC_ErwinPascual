package com.epascual.appcocopascualtf.servicio

import com.epascual.appcocopascualtf.entidades.Coleccion
import com.google.gson.annotations.SerializedName

data class ColeccionResponse(
    @SerializedName("lstColeccion") var lstColeccion: ArrayList<Coleccion>
)
