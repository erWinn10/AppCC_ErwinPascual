package com.epascual.appcocopascualtf

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.epascual.appcocopascualtf.databinding.ActivityDarticuloBinding
import com.epascual.appcocopascualtf.entidades.Articulo
import com.epascual.appcocopascualtf.entidades.Coleccion
import com.epascual.appcocopascualtf.interfaz.AdaptadorDArticulo
import com.epascual.appcocopascualtf.interfaz.AdaptadorDColeccion
import com.epascual.appcocopascualtf.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DArticuloActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDarticuloBinding
    private lateinit var getContent: ActivityResultLauncher<String>

    private lateinit var adaptador: AdaptadorDArticulo
    private var colecciones: List<Coleccion> = emptyList() // Lista para almacenar las colecciones

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDarticuloBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val opciones = listOf("Agregar Articulo", "Mostrar Articulos")
        val adaptadorSpinner = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, opciones
        )
        adaptadorSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spEstadoArt.adapter = adaptadorSpinner

        // Configurar listener para cambios en el Spinner
        binding.spEstadoArt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val opcionSeleccionada = binding.spEstadoArt.selectedItem.toString()
                actualizarUI(opcionSeleccionada)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada si no se selecciona nada
            }
        }
        cargarDArticulos()

        cargarColecciones() // Cargar colecciones desde la API
        asignar()
        // Configurar el botón de menú
        binding.btnMenu.setOnClickListener {
            toggleNavigationView()
        }
    }

    private fun actualizarUI(opcion: String) {
        if (opcion == "Agregar Articulo") {
            binding.textView4.visibility = View.VISIBLE
            binding.textView5.visibility = View.VISIBLE
            binding.etLNombreArt.visibility = View.VISIBLE
            binding.etLPrecioArt.visibility = View.VISIBLE
            binding.spColeccionArt.visibility = View.VISIBLE
            binding.btnDRegistrarArt.visibility = View.VISIBLE
            binding.btnSelectImagen.visibility = View.VISIBLE
            binding.ImgSeleccionada.visibility = View.VISIBLE
            binding.rvDArticulos.visibility = View.GONE

        } else if (opcion == "Mostrar Articulos") {
            binding.textView4.visibility = View.GONE
            binding.textView5.visibility = View.GONE
            binding.etLNombreArt.visibility = View.GONE
            binding.etLPrecioArt.visibility = View.GONE
            binding.spColeccionArt.visibility = View.GONE
            binding.btnDRegistrarArt.visibility = View.GONE
            binding.btnSelectImagen.visibility = View.GONE
            binding.ImgSeleccionada.visibility = View.GONE
            binding.rvDArticulos.visibility = View.VISIBLE
        }
    }


    private fun asignar() {

        binding.rvDArticulos.layoutManager = LinearLayoutManager(this)
        adaptador = AdaptadorDArticulo()
        binding.rvDArticulos.adapter = adaptador

        var validar = true

        // Configurar el selector de imágenes
        binding.btnSelectImagen.setOnClickListener {
            getContent.launch("*/*") // Permitir seleccionar cualquier tipo de archivo
        }
        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.ImgSeleccionada.setImageURI(it) // Muestra la imagen seleccionada
                binding.ImgSeleccionada.tag = it // Asigna el URI al tag
            }
        }

        // Botón para registrar artículo
        binding.btnDRegistrarArt.setOnClickListener {
            val nombre = binding.etLNombreArt.text.toString()
            val precio = binding.etLPrecioArt.text.toString()
            val imagenUri = binding.ImgSeleccionada.tag as? Uri
            val fechaRegistro = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val nombreArchivo = imagenUri?.let { obtenerNombreArchivo(it) } ?: ""

            // Obtener el col_id de la colección seleccionada
            val colId = colecciones.getOrNull(binding.spColeccionArt.selectedItemPosition)?.col_id ?: 0
            val articulo = Articulo(0, nombre, precio.toDouble(), fechaRegistro, nombreArchivo, colId)
            // Validaciones
            if (nombre.isEmpty()) {
                binding.etLNombreArt.error = "Ingrese un nombre"
                validar = false
            }
            if (precio.isEmpty() || precio.toDoubleOrNull() == null) {
                binding.etLPrecioArt.error = "Ingrese un precio válido"
                validar = false
            }
            if (colId == 0) {
                mostrarMensaje("Seleccione una colección válida")
                validar = false
            }

            // Si todo es válido, enviamos el artículo al servidor
            if (validar) {

                CoroutineScope(Dispatchers.IO).launch {
                    val call = RetrofitClient.webService.agregarArticulo(articulo)
                    runOnUiThread {
                        if (call.isSuccessful) {
                            mostrarMensaje("Registro exitoso")
                            limpiarDatos()
                            cargarDArticulos()

                        } else {
                            mostrarMensaje("Error al registrar: ${call.message()}")
                        }
                    }
                }
            }
        }

        binding.cVColecciones.setOnClickListener {
            val intent = Intent(this, DColeccionesActivity::class.java)
            startActivity(intent)
        }
        binding.cVArticulos.setOnClickListener {
            val intent = Intent(this, DArticuloActivity::class.java)
            startActivity(intent)
        }
        binding.cVUsuarios.setOnClickListener {
            val intent = Intent(this, DUsuariosActivity::class.java)
            startActivity(intent)
        }
        binding.cVPedidos.setOnClickListener {
            val intent = Intent(this, DPedidosActivity::class.java)
            startActivity(intent)
        }
        binding.cVRegresar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        adaptador.setEliminarItem {
            eliminar(it)
        }
    }

    private fun eliminar(articulo: Articulo){
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Eliminar")
        ventana.setMessage("¿Desea eliminar el articulo '${articulo.art_nombre}' ?")
        ventana.setPositiveButton("Si", DialogInterface.OnClickListener { dialog, which ->
            CoroutineScope(Dispatchers.IO).launch {
                val call = RetrofitClient.webService.eliminarArticulo(articulo.art_id)
                runOnUiThread {
                    if (call.isSuccessful) {
                        mostrarMensaje(call.body().toString())
                        cargarDArticulos()
                    }
                }
            }
        })
        ventana.setNegativeButton("No", null)
        ventana.show()
    }


    private fun cargarColecciones() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.getColeccion()
                if (response.isSuccessful) {
                    colecciones = response.body()?.lstColeccion ?: emptyList()
                    val nombresColecciones = colecciones.map { it.col_nombre }
                    runOnUiThread {
                        actualizarSpinner(nombresColecciones)
                    }
                } else {
                    mostrarMensaje("Error al cargar colecciones: ${response.message()}")
                }
            } catch (e: Exception) {
                runOnUiThread {
                    mostrarMensaje("Error: ${e.message}")
                }
            }
        }
    }

    private fun actualizarSpinner(nombres: List<String>) {
        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            nombres
        )
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spColeccionArt.adapter = adaptador
    }

    private fun mostrarMensaje(mensaje: String) {
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Información")
        ventana.setMessage(mensaje)
        ventana.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        ventana.create().show()
    }

    private fun limpiarDatos() {
        binding.etLNombreArt.text.clear()
        binding.etLPrecioArt.text.clear()
        binding.ImgSeleccionada.setImageResource(R.drawable.blanco)
    }

    private fun obtenerNombreArchivo(uri: Uri): String {
        return uri.lastPathSegment?.split("/")?.last()?.substringBeforeLast(".") ?: ""
    }

    private fun toggleNavigationView() {
        if (binding.nvMenu.visibility == View.VISIBLE) {
            binding.nvMenu.visibility = View.GONE
        } else {
            binding.nvMenu.visibility = View.VISIBLE
        }
    }
    private fun cargarDArticulos(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.getArticulo()
            runOnUiThread {
                if (call.isSuccessful) {
                    adaptador.setlistaArticulo(call.body()!!.lstArticulo)
                }
            }
        }
    }
}
