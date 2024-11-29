package com.epascual.appcocopascualtf.interfaz

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epascual.appcocopascualtf.R
import com.epascual.appcocopascualtf.entidades.Articulo
import com.epascual.appcocopascualtf.entidades.Coleccion
import com.epascual.appcocopascualtf.entidades.Pedido

class AdaptadorDArticulo:RecyclerView.Adapter<AdaptadorDArticulo.MiViewHolder>() {

    private var lstArticulo = ArrayList<Articulo>()
    private lateinit var context: Context


    fun setContext(context: Context) {
        this.context = context
    }

    fun setlistaArticulo(lista: ArrayList<Articulo>) {
        this.lstArticulo = lista
        notifyDataSetChanged() // Notificar cambios
    }


    private var eliminarItem: ((Articulo) -> Unit)? = null

    fun setEliminarItem(eliminarItem: (Articulo) -> Unit) {
        this.eliminarItem = eliminarItem
    }

    class MiViewHolder(view:View):RecyclerView.ViewHolder(view) {
        private var filaDArtId = view.findViewById<TextView>(R.id.filaDArtId)
        private var filaDArtNombre = view.findViewById<TextView>(R.id.filaDArtNombre)
        private var filaDArtPrecio = view.findViewById<TextView>(R.id.filaDArtPrecio)
        private var filaDArtImg = view.findViewById<ImageView>(R.id.filaDArtImg)

        var filaEliminar = view.findViewById<ImageView>(R.id.filaDArtBtnEliminar)

        fun rellenarFila(articulo: Articulo) {
            filaDArtId.text = articulo.art_id.toString()
            filaDArtNombre.text = articulo.art_nombre.substringBefore(" -")
            filaDArtPrecio.text = articulo.art_precio.toString()

            val resourceId = filaDArtImg.context.resources.getIdentifier(
                articulo.art_imagen,
                "drawable",
                filaDArtImg.context.packageName
            )

            filaDArtImg.setImageResource(if (resourceId != 0) resourceId else R.drawable.elefanteparaguaceleste)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MiViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.fila_darticulo, parent, false)
    )

    override fun onBindViewHolder(holder: AdaptadorDArticulo.MiViewHolder, position: Int) {
        val darticuloItem = lstArticulo[position]
        holder.rellenarFila(darticuloItem)

        holder.filaEliminar.setOnClickListener {
            eliminarItem?.invoke(darticuloItem)
        }
    }

    override fun getItemCount(): Int {
        return lstArticulo.size
    }
}