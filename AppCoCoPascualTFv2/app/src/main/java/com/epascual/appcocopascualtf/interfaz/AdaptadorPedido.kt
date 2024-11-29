package com.epascual.appcocopascualtf.interfaz

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epascual.appcocopascualtf.R
import com.epascual.appcocopascualtf.entidades.Articulo
import com.epascual.appcocopascualtf.entidades.Pedido

class AdaptadorPedido : RecyclerView.Adapter<AdaptadorPedido.MiViewHolder>() {

    private var lstPedido = ArrayList<Pedido>()
    private lateinit var context: Context
    private var lstArticulo = ArrayList<Articulo>() // Lista de artículos


    fun setContext(context: Context) {
        this.context = context
    }

    fun setListaPedido(lista: ArrayList<Pedido>) {
        this.lstPedido = lista
        notifyDataSetChanged() // Notificar cambios
    }

    fun setListaArticulos(lista: ArrayList<Articulo>) {
        this.lstArticulo = lista
    }

    private var eliminarItem: ((Pedido) -> Unit)? = null
    fun setEliminarItem(eliminarItem: (Pedido) -> Unit) {
        this.eliminarItem = eliminarItem
    }

    class MiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var filaPedId = view.findViewById<TextView>(R.id.filaPedId)
        private var filaPedNombreArt = view.findViewById<TextView>(R.id.filaPedNombreArt)
        private var filaPedCantidad = view.findViewById<TextView>(R.id.filaPedCantidad)
        private var filaPedImg = view.findViewById<ImageView>(R.id.filaPedImg)

        var filaEliminar = view.findViewById<ImageView>(R.id.filaPedBtnEliminar)

        fun rellenarFila(context: Context, pedido: Pedido, articulo: Articulo?) {
            filaPedId.text = pedido.ped_id.toString()
            filaPedCantidad.text = pedido.ped_cantidad.toString()

            if (articulo != null) {
                // Mostrar solo el texto antes del primer espacio
                val nombreCorto = articulo.art_nombre.substringBefore(" -")
                filaPedNombreArt.text = nombreCorto

                // Buscar el recurso en drawable
                val resourceId = filaPedImg.context.resources.getIdentifier(
                    articulo.art_imagen, // Nombre de la imagen (sin extensión)
                    "drawable",
                    filaPedImg.context.packageName
                )

                // Asignar la imagen si existe, usar un recurso por defecto si no
                filaPedImg.setImageResource(
                    if (resourceId != 0) resourceId else R.drawable.elefanteparaguaceleste
                )
            } else {
                filaPedNombreArt.text = "Artículo no encontrado"
                filaPedImg.setImageResource(R.drawable.elefanteparaguaceleste) // Imagen por defecto
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fila_pedidos, parent, false)
        return MiViewHolder(view)
    }

    override fun onBindViewHolder(holder: MiViewHolder, position: Int) {
        val pedido = lstPedido[position]

        // Buscar el artículo correspondiente al `art_id` del pedido
        val articulo = lstArticulo.find { it.art_id == pedido.art_id }

        // Rellenar la fila
        holder.rellenarFila(context, pedido, articulo)

        holder.filaEliminar.setOnClickListener {
            eliminarItem?.invoke(pedido)
        }
    }

    override fun getItemCount(): Int {
        return lstPedido.size
    }
}
