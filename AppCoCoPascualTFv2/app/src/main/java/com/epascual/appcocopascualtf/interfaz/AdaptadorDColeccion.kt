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
import com.epascual.appcocopascualtf.entidades.Coleccion
import com.epascual.appcocopascualtf.entidades.Pedido

class AdaptadorDColeccion:RecyclerView.Adapter<AdaptadorDColeccion.MiViewHolder>() {

    private var lstColeccion = ArrayList<Coleccion>()
    private lateinit var context: Context
    private var editarItem: ((Coleccion) -> Unit)? = null

    fun setEditarItem(editarItem: (Coleccion) -> Unit) {
        this.editarItem = editarItem
    }


    fun setContext(context: Context) {
        this.context = context
    }

    fun setlistaColeccion(lista: ArrayList<Coleccion>) {
        this.lstColeccion = lista
        notifyDataSetChanged() // Notificar cambios
    }

    private var eliminarItem: ((Coleccion) -> Unit)? = null

    fun setEliminarItem(eliminarItem: (Coleccion) -> Unit) {
        this.eliminarItem = eliminarItem
    }

    class MiViewHolder(view:View):RecyclerView.ViewHolder(view) {
        private var filaDColId = view.findViewById<TextView>(R.id.filaDColId)
        private var filaDColNombre = view.findViewById<TextView>(R.id.filaDColNombre)
        private var filaDColImg = view.findViewById<ImageView>(R.id.filaDColImg)

        var filaEliminar = view.findViewById<ImageView>(R.id.filaDColBtnEliminar)
        var filaEditar = view.findViewById<ImageView>(R.id.filaDColBtnEdit)

        fun rellenarFila(coleccion: Coleccion) {
            filaDColId.text = coleccion.col_id.toString()
            filaDColNombre.text = coleccion.col_nombre

            val resourceId = filaDColImg.context.resources.getIdentifier(
                coleccion.col_imagenref,
                "drawable",
                filaDColImg.context.packageName
            )

            filaDColImg.setImageResource(if (resourceId != 0) resourceId else R.drawable.elefanteparaguaceleste)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MiViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.fila_pcoleccion, parent, false)
    )

    override fun onBindViewHolder(holder: AdaptadorDColeccion.MiViewHolder, position: Int) {
        val dcoleccionItem = lstColeccion[position]
        holder.rellenarFila(dcoleccionItem)

        holder.filaEliminar.setOnClickListener {
            eliminarItem?.invoke(dcoleccionItem)
        }
        holder.filaEditar.setOnClickListener {
            val intent = Intent(context, dcoleccionItem::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return lstColeccion.size
    }
}