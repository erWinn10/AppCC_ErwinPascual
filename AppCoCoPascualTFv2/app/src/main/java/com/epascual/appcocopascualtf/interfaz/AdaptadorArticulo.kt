package com.epascual.appcocopascualtf.interfaz

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.epascual.appcocopascualtf.ArticuloActivity
import com.epascual.appcocopascualtf.R
import com.epascual.appcocopascualtf.WelcomeActivity
import com.epascual.appcocopascualtf.entidades.Articulo
import com.epascual.appcocopascualtf.entidades.Coleccion

class AdaptadorArticulo (private val context: Context) : RecyclerView.Adapter<AdaptadorArticulo.MiViewHolder>() {
    private var lstArticulo = ArrayList<Articulo>()

    fun setlistaArticulo(lista: ArrayList<Articulo>) {
        this.lstArticulo = lista
        notifyDataSetChanged() // Notificar cambios
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fila_articulo, parent, false)
        return MiViewHolder(view)
    }

    override fun onBindViewHolder(holder: MiViewHolder, position: Int) {
            val articuloItem = lstArticulo[position]
            holder.rellenarFila(articuloItem)

            // Asignar el click listener para el artículo
            holder.itemView.setOnClickListener {
                (context as ArticuloActivity).mostrarDetallesArticulo(articuloItem) // Llama al método del Activity
            }

    }


    override fun getItemCount(): Int {
        return lstArticulo.size
    }

    class MiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var filaArtNombreA = view.findViewById<TextView>(R.id.filaArtNombreA)
        private var filaArtImgA = view.findViewById<ImageView>(R.id.filaArtImgA)
        private var filaArtPrecioA = view.findViewById<TextView>(R.id.filaArtPrecioA)

        fun rellenarFila(articulo: Articulo) {
            filaArtNombreA.text = articulo.art_nombre
            filaArtPrecioA.text = articulo.art_precio.toString()

            val resourceId = filaArtImgA.context.resources.getIdentifier(
                articulo.art_imagen,
                "drawable",
                filaArtImgA.context.packageName
            )

            filaArtImgA.setImageResource(if (resourceId != 0) resourceId else R.drawable.elefanteparaguaceleste)

        }
    }
}