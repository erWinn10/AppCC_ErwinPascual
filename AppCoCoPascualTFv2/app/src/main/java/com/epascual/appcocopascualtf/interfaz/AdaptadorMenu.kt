package com.epascual.appcocopascualtf.interfaz

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.RecyclerView
import com.epascual.appcocopascualtf.ArticuloActivity
import com.epascual.appcocopascualtf.DArticuloActivity
import com.epascual.appcocopascualtf.DColeccionesActivity
import com.epascual.appcocopascualtf.R
import com.epascual.appcocopascualtf.WelcomeActivity
import com.epascual.appcocopascualtf.entidades.Coleccion

class AdaptadorMenu(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listaColeccion = ArrayList<Coleccion>()
    private val VIEW_TYPE_COLECCION = 0
    private val VIEW_TYPE_EXTRA = 1
    private var aux: Int = 0
    // Usamos el contexto para obtener SharedPreferences
    private val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
    private val userName = sharedPreferences.getString("userName", "Usuario no encontrado")
    private val userEmail = sharedPreferences.getString("userEmail", "Correo no encontrado")
    private val userId = sharedPreferences.getInt("userId", 0)
    val userRol = sharedPreferences.getInt("userRol", 0)


    fun setLista(lista: ArrayList<Coleccion>) {
        this.listaColeccion = lista
        notifyDataSetChanged() // Notificar cambios
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < listaColeccion.size) VIEW_TYPE_COLECCION else VIEW_TYPE_EXTRA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_COLECCION) {
            MiViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fila_opcion, parent, false))
        } else {
            MiViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fila_opcion, parent, false)) // Usar el mismo layout
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MiViewHolder) {
            if (position < listaColeccion.size) {
                val coleccionItem = listaColeccion[position]
                holder.rellenarFila(coleccionItem)

                holder.itemView.setOnClickListener {
                    val intent = Intent(context, ArticuloActivity::class.java)
                    intent.putExtra("COLECCION_ID", coleccionItem.col_id)
                    intent.putExtra("COLECCION_NOMBRE", coleccionItem.col_nombre)
                    context.startActivity(intent)
                }
            } else {
                // Para las opciones adicionales
                if (position == listaColeccion.size) {
                    holder.filaNombres.text = "Administración"
                    if(userRol==1 || userRol==2){
                        holder.itemView.setOnClickListener {
                            // Crear un Intent para abrir DColeccionesActivity
                            val intent = Intent(holder.itemView.context, DColeccionesActivity::class.java)
                            holder.itemView.context.startActivity(intent)
                        }
                    }
                    else {
                        holder.itemView.setOnClickListener {
                        // Mostrar un mensaje de que no está autorizado
                        Toast.makeText(holder.itemView.context, "No está autorizado para acceder a esta sección", Toast.LENGTH_SHORT).show()

                        }
                    }


                } else if (position == listaColeccion.size + 1) {
                    holder.filaNombres.text = "Cerrar Sesión"
                    holder.itemView.setOnClickListener {
                        val intent = Intent(holder.itemView.context, WelcomeActivity::class.java)
                        holder.itemView.context.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listaColeccion.size + 2 // +2 para las opciones extra
    }

    class MiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var filaNombres: TextView = view.findViewById(R.id.tvOpcionCol)

        fun rellenarFila(coleccion: Coleccion) {
            filaNombres.text = coleccion.col_nombre
        }
    }
}
