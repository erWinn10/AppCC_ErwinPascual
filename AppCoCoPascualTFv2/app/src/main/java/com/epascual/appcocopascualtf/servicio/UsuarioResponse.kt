package com.epascual.appcocopascualtf.servicio

import com.epascual.appcocopascualtf.entidades.Usuario
import com.google.gson.annotations.SerializedName

data class UsuarioResponse(
    @SerializedName("lstUsuarios") var lstUsuarios: ArrayList<Usuario>
)
