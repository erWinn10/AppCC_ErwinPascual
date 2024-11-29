package com.epascual.appcocopascualtf

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.epascual.appcocopascualtf.databinding.ActivityArticuloBinding
import com.epascual.appcocopascualtf.entidades.Articulo
import com.epascual.appcocopascualtf.entidades.Pedido
import com.epascual.appcocopascualtf.entidades.Usuario
import com.epascual.appcocopascualtf.interfaz.AdaptadorArticulo
import com.epascual.appcocopascualtf.interfaz.AdaptadorMenu
import com.epascual.appcocopascualtf.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ArticuloActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticuloBinding
    private lateinit var adaptador: AdaptadorArticulo
    private var coleccionId: Int = 0 // Variable para almacenar el ID de la colección
    private var coleccionNombre: String = ""// Variable para almacenar el ID de la colección
    private lateinit var adaptadorMenu: AdaptadorMenu


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityArticuloBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Recuperar el nombre y el correo del usuario desde SharedPreferences



        // Obtener el ID de la colección desde el Intent
        coleccionId = intent.getIntExtra("COLECCION_ID", 0)
        coleccionNombre = intent.getStringExtra("COLECCION_NOMBRE") ?: ""
        asignar()
        cargarArticulos()

        cargarOpciones()
        // Configurar el botón de menú
        binding.btnMenu.setOnClickListener {
            toggleNavigationView()
        }
    }

    fun mostrarDetallesArticulo(articulo: Articulo) { // Cambia a public o simplemente quita 'private'
        // Referencias a los elementos del CardView
        val dialogArtImg = binding.ivArtiCULOImagenArt
        val dialogArtNombre = binding.tvArtiCULONombreArt
        val dialogArtPrecio = binding.tvArtiCULOPrecioArt

        // Configurar los datos del artículo
        dialogArtNombre.text = articulo.art_nombre
        dialogArtPrecio.text = "${articulo.art_precio} $"

        // Cambiar la imagen según el ID del artículo
        val resourceId = dialogArtImg.context.resources.getIdentifier(
            articulo.art_imagen,
            "drawable",
            dialogArtImg.context.packageName
        )
        dialogArtImg.setImageResource(if (resourceId != 0) resourceId else R.drawable.elefanteparaguaceleste)

        // Hacer visible el CardView
        binding.cvArtiCULO.visibility = View.VISIBLE // Asegúrate de que el ID sea correcto
        binding.btnArtiCULORegistrarArt.setOnClickListener {
            registrarPedido(articulo)
        }
    }

    private fun registrarPedido(articulo: Articulo) {
        val etCantidad = binding.etArtiCULOCantidadArt.text.toString()
        var validar = true
        val fechaRegistro = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", 0)
        val pedido = Pedido(0, etCantidad.toInt(), fechaRegistro, articulo.art_id, userId)
        if (etCantidad.isEmpty()) {
            binding.etArtiCULOCantidadArt.error = "Ingrese la cantidad"
            validar = false
        }
        if (validar) {
            CoroutineScope(Dispatchers.IO).launch {
                val call = RetrofitClient.webService.agregarPedido(pedido)
                runOnUiThread {
                    if (call.isSuccessful) {
                        mostrarMensaje("Registro exitoso") // Mensaje de éxito
                        limpiarDatos()
                    } else {
                        mostrarMensaje("Error al registrar: ${call.message()}") // Mensaje de error
                    }
                }
            }
        }
    }

    private fun asignar() {
        // Usar GridLayoutManager para mostrar en 2 columnas
        binding.rvArticulos.layoutManager = GridLayoutManager(this, 2)
        adaptador = AdaptadorArticulo(this) // Inicializa el adaptador con el contexto
        binding.rvArticulos.adapter = adaptador // Asegúrate de establecer el adaptador aquí
        binding.tvArtNombreCol.text = "$coleccionNombre"
        // Configurar el botón de salir
        binding.ibArtiCULOSalir.setOnClickListener {
            binding.cvArtiCULO.visibility = View.INVISIBLE // Ocultar el CardView
        }
        binding.ivArtLogo.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.rvOpciones.layoutManager = LinearLayoutManager(this)
        adaptadorMenu = AdaptadorMenu(this)
        binding.rvOpciones.adapter = adaptadorMenu
    }

    private fun cargarArticulos() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.getArticulo()
            runOnUiThread {
                if (call.isSuccessful) {
                    val articulos = call.body()
                    if (articulos != null) {
                        // Filtrar los artículos que coincidan con el ID de la colección
                        val articulosFiltrados = articulos.lstArticulo.filter { it.col_id == coleccionId }
                        adaptador.setlistaArticulo(ArrayList(articulosFiltrados))
                    }
                } else {
                    // Manejar el error, por ejemplo, mostrar un mensaje
                    Toast.makeText(this@ArticuloActivity, "Error al cargar los artículos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun cargarOpciones() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.getColeccion()
            runOnUiThread {
                if (call.isSuccessful) {
                    adaptadorMenu.setLista(call.body()!!.lstColeccion)
                    binding.rvOpciones.adapter = adaptadorMenu
                }
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Información")
        ventana.setMessage(mensaje)
        ventana.setPositiveButton("Aceptar") { dialog, which -> dialog.dismiss() } // Solo cerrar el diálogo
        ventana.create().show()
    }

    private fun limpiarDatos() {
        binding.etArtiCULOCantidadArt.text.clear()
    }

    private fun toggleNavigationView() {
        if (binding.nvMenu.visibility == View.VISIBLE) {
            binding.nvMenu.visibility = View.GONE
        } else {
            binding.nvMenu.visibility = View.VISIBLE
        }
    }
}

