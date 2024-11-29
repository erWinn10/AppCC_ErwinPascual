package com.epascual.appcocopascualtf.servicio

import com.epascual.appcocopascualtf.entidades.Rol
import com.google.gson.annotations.SerializedName

data class RolResponse(
    @SerializedName("lstRoles") var lstRoles: ArrayList<Rol>
)
