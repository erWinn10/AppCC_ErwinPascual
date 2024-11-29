package com.epascual.appcocopascualtf.servicio

import com.epascual.appcocopascualtf.entidades.Pedido
import com.google.gson.annotations.SerializedName

data class PedidoResponse(
    @SerializedName("lstPedidos") var lstPedidos: ArrayList<Pedido>
)
