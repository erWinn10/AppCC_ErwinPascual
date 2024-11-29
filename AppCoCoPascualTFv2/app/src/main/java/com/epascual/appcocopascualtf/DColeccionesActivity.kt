package com.epascual.appcocopascualtf

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.epascual.appcocopascualtf.databinding.ActivityDcoleccionesBinding
import com.epascual.appcocopascualtf.entidades.Coleccion
import com.epascual.appcocopascualtf.entidades.Pedido
import com.epascual.appcocopascualtf.interfaz.AdaptadorColeccion
import com.epascual.appcocopascualtf.interfaz.AdaptadorDColeccion
import com.epascual.appcocopascualtf.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DColeccionesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDcoleccionesBinding
    private lateinit var getContent: ActivityResultLauncher<String>

    private lateinit var adaptador: AdaptadorDColeccion


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDcoleccionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val opciones = listOf("Agregar Coleccion", "Mostrar Colecciones")
        val adaptadorSpinner = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, opciones
        )
        adaptadorSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spEstadoCol.adapter = adaptadorSpinner

        // Configurar listener para cambios en el Spinner
        binding.spEstadoCol.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val opcionSeleccionada = binding.spEstadoCol.selectedItem.toString()
                actualizarUI(opcionSeleccionada)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada si no se selecciona nada
            }
        }

        asignar()
        cargarDColecciones()


        // Configurar el botón de menú
        binding.btnMenu.setOnClickListener {
            toggleNavigationView()
        }
    }

    private fun actualizarUI(opcion: String) {
        if (opcion == "Agregar Coleccion") {
            binding.textView4.visibility = View.VISIBLE
            binding.textView5.visibility = View.VISIBLE
            binding.etLNombreCol.visibility = View.VISIBLE
            binding.etLDescripCol.visibility = View.VISIBLE
            binding.btnDRegistrarCol.visibility = View.VISIBLE
            binding.btnSelectImagen.visibility = View.VISIBLE
            binding.ImgSeleccionada.visibility = View.VISIBLE
            binding.rvdColeccion.visibility = View.GONE

        } else if (opcion == "Mostrar Colecciones") {
            binding.textView4.visibility = View.GONE
            binding.textView5.visibility = View.GONE
            binding.etLNombreCol.visibility = View.GONE
            binding.etLDescripCol.visibility = View.GONE
            binding.btnDRegistrarCol.visibility = View.GONE
            binding.btnSelectImagen.visibility = View.GONE
            binding.ImgSeleccionada.visibility = View.GONE
            binding.rvdColeccion.visibility = View.VISIBLE
        }
    }

    private fun asignar() {

        binding.rvdColeccion.layoutManager = LinearLayoutManager(this)
        adaptador = AdaptadorDColeccion()
        binding.rvdColeccion.adapter = adaptador

        var validar = true

        binding.btnSelectImagen.setOnClickListener {
            getContent.launch("*/*") // Permitir seleccionar cualquier tipo de archivo
        }

        // Configurar el ActivityResultLauncher
        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.ImgSeleccionada.setImageURI(it) // Muestra la imagen seleccionada
                binding.ImgSeleccionada.tag = it // Asigna el URI al tag
            }
        }

        binding.btnDRegistrarCol.setOnClickListener {
            val nombre = binding.etLNombreCol.text.toString()
            val descripcion = binding.etLDescripCol.text.toString()
            val imagenUri = binding.ImgSeleccionada.tag as? Uri
            // Obtiene el nombre del archivo
            val nombreArchivo = imagenUri?.let { obtenerNombreArchivo(it) } ?: ""
            val coleccion = Coleccion(0, nombre, descripcion, nombreArchivo)

            if (nombre.isEmpty()) {
                binding.etLNombreCol.error = "Ingrese un nombre"
                validar = false
            }
            if (descripcion.isEmpty()) {
                binding.etLDescripCol.error = "Ingrese una descripción"
                validar = false
            }

            if (imagenUri == null) {
                mostrarMensaje("Por favor, selecciona una imagen.")
                validar = false
            }

            if (validar) {
                CoroutineScope(Dispatchers.IO).launch {
                    val call = RetrofitClient.webService.agregarColeccion(coleccion)
                    runOnUiThread {
                        if (call.isSuccessful) {
                            mostrarMensaje("Registro exitoso") // Mensaje de éxito
                            limpiarDatos()
                            cargarDColecciones()

                        } else {
                            mostrarMensaje("Error al registrar: ${call.message()}") // Mensaje de error
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

    private fun mostrarMensaje(mensaje: String) {
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Información")
        ventana.setMessage(mensaje)
        ventana.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        ventana.create().show()
    }

    private fun limpiarDatos() {
        binding.etLNombreCol.text.clear()
        binding.etLDescripCol.text.clear()
        binding.ImgSeleccionada.setImageResource(R.drawable.blanco) // Limpia la imagen
    }


    private fun eliminar(coleccion: Coleccion){
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Eliminar")
        ventana.setMessage("¿Desea eliminar la coleccion '${coleccion.col_nombre}' ?")
        ventana.setPositiveButton("Si", DialogInterface.OnClickListener { dialog, which ->
            CoroutineScope(Dispatchers.IO).launch {
                val call = RetrofitClient.webService.eliminarColeccion(coleccion.col_id)
                runOnUiThread {
                    if (call.isSuccessful) {
                        mostrarMensaje(call.body().toString())
                        cargarDColecciones()
                    }
                }
            }
        })
        ventana.setNegativeButton("No", null)
        ventana.show()
    }

    private fun obtenerNombreArchivo(uri: Uri): String {
        // Obtiene el último segmento de la URI
        return uri.lastPathSegment?.let { segment ->
            val nombreCompleto = segment.split("/").last()
            nombreCompleto.substringBeforeLast(".")
        } ?: ""
    }

    private fun toggleNavigationView() {
        if (binding.nvMenu.visibility == View.VISIBLE) {
            binding.nvMenu.visibility = View.GONE
        } else {
            binding.nvMenu.visibility = View.VISIBLE
        }
    }
    private fun cargarDColecciones(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.getColeccion()
            runOnUiThread {
                if (call.isSuccessful) {
                    adaptador.setlistaColeccion(call.body()!!.lstColeccion)
                }
            }
        }
    }
}
