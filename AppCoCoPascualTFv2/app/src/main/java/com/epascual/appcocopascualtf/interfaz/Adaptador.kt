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

class Adaptador(private val context: Context) : RecyclerView.Adapter<Adaptador.MiViewHolder>() {

    private var lstColeccion = ArrayList<Coleccion>()
    private var lstArticulo = ArrayList<Articulo>()

    fun setlistaColeccion(lista: ArrayList<Coleccion>) {
        this.lstColeccion = lista
        notifyDataSetChanged() // Notificar cambios
    }

    fun setlistaArticulo(lista: ArrayList<Articulo>) {
        this.lstArticulo = lista
        notifyDataSetChanged() // Notificar cambios
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < lstColeccion.size) {
            0 // Tipo para colecciones
        } else {
            1 // Tipo para artículos
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiViewHolder {
        val layoutId = if (viewType == 0) R.layout.filacoleccion else R.layout.fila_articulo
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MiViewHolder(view)
    }

    override fun onBindViewHolder(holder: MiViewHolder, position: Int) {
        if (position < lstColeccion.size) {
            val coleccionItem = lstColeccion[position]
            holder.rellenarFila(coleccionItem)

            // Asignar el click listener para la colección
            holder.itemView.setOnClickListener {
                if (position == 0) { // Solo si es el primer elemento
                    val intent = Intent(context, ArticuloActivity::class.java)
                    context.startActivity(intent)
                }
            }
        } else {
            val articuloItem = lstArticulo[position - lstColeccion.size]
            holder.rellenarFila(articuloItem)
        }
    }

    override fun getItemCount(): Int {
        return lstColeccion.size + lstArticulo.size
    }

    class MiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var filaColNombreC = view.findViewById<TextView>(R.id.filaColNombreC)
        private var filaColImgC = view.findViewById<ImageView>(R.id.filaColImgC)

        private var filaArtNombreA = view.findViewById<TextView>(R.id.filaArtNombreA)
        private var filaArtImgA = view.findViewById<ImageView>(R.id.filaArtImgA)
        private var filaArtPrecioA = view.findViewById<TextView>(R.id.filaArtPrecioA)

        fun rellenarFila(coleccion: Coleccion) {
            filaColNombreC.text = coleccion.col_nombre

            val resourceId = filaColImgC.context.resources.getIdentifier(
                coleccion.col_imagenref,
                "drawable",
                filaColImgC.context.packageName
            )

            filaColImgC.setImageResource(if (resourceId != 0) resourceId else R.drawable.elefanteparaguaceleste)
        }

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

