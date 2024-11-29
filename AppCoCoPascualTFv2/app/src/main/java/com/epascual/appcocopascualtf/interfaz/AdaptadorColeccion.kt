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

class AdaptadorColeccion(private val context: Context) : RecyclerView.Adapter<AdaptadorColeccion.MiViewHolder>() {

    private var lstColeccion = ArrayList<Coleccion>()

    fun setlistaColeccion(lista: ArrayList<Coleccion>) {
        this.lstColeccion = lista
        notifyDataSetChanged() // Notificar cambios
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filacoleccion, parent, false)
        return MiViewHolder(view)
    }

    override fun onBindViewHolder(holder: MiViewHolder, position: Int) {
        val coleccionItem = lstColeccion[position]
        holder.rellenarFila(coleccionItem)

        // Asignar el click listener para la colección
        holder.itemView.setOnClickListener {
            // Pasar el ID de la colección al iniciar ArticuloActivity
            val intent = Intent(context, ArticuloActivity::class.java)
            intent.putExtra("COLECCION_ID", coleccionItem.col_id) // Asegúrate de que col_id sea el ID correcto
            intent.putExtra("COLECCION_NOMBRE", coleccionItem.col_nombre) // Nombre de la colección
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return lstColeccion.size
    }

    class MiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var filaColNombreC = view.findViewById<TextView>(R.id.filaColNombreC)
        private var filaColImgC = view.findViewById<ImageView>(R.id.filaColImgC)

        fun rellenarFila(coleccion: Coleccion) {
            filaColNombreC.text = coleccion.col_nombre

            val resourceId = filaColImgC.context.resources.getIdentifier(
                coleccion.col_imagenref,
                "drawable",
                filaColImgC.context.packageName
            )

            filaColImgC.setImageResource(if (resourceId != 0) resourceId else R.drawable.elefanteparaguaceleste)
        }
    }
}

