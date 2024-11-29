package com.epascual.appcocopascualtf

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.epascual.appcocopascualtf.databinding.ActivityDusuariosBinding
import com.epascual.appcocopascualtf.entidades.Articulo
import com.epascual.appcocopascualtf.entidades.Coleccion
import com.epascual.appcocopascualtf.entidades.Rol
import com.epascual.appcocopascualtf.entidades.Usuario
import com.epascual.appcocopascualtf.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DUsuariosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDusuariosBinding
    private lateinit var getContent: ActivityResultLauncher<String>
    private var roles: List<Rol> = emptyList() // Lista para almacenar las colecciones

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDusuariosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        cargarRoles()
        asignar()
        // Configurar el botón de menú
        binding.btnMenu.setOnClickListener {
            toggleNavigationView()
        }
    }

    private fun asignar() {
        var validar = true


        // Botón para registrar artículo
        binding.btnDRegistrarUsu.setOnClickListener {
            val nombre = binding.etLNombreUsu.text.toString()
            val correo = binding.etLCorreoUsu.text.toString()
            val contrasena = binding.etLContrasenaUsu.text.toString()

            val rolID = roles.getOrNull(binding.spRolUsu.selectedItemPosition)?.rol_id ?: 0
            val usuario = Usuario(0, nombre, correo, contrasena, rolID)
            // Validaciones
            if (nombre.isEmpty()) {
                binding.etLNombreUsu.error = "Ingrese un nombre"
                validar = false
            }
            if (correo.isEmpty()) {
                binding.etLCorreoUsu.error = "Ingrese un correo"
                validar = false
            }
            if (rolID == 0) {
                mostrarMensaje("Seleccione un rol valido")
                validar = false
            }

            // Si todo es válido, enviamos el artículo al servidor
            if (validar) {

                CoroutineScope(Dispatchers.IO).launch {
                    val call = RetrofitClient.webService.agregarUsuario(usuario)
                    runOnUiThread {
                        if (call.isSuccessful) {
                            mostrarMensaje("Registro exitoso")
                            limpiarDatos()
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
    }

    private fun cargarRoles() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService.getRol()
                if (response.isSuccessful) {
                    roles = response.body()?.lstRoles ?: emptyList()
                    val nombreRoles = roles.map { it.rol_authority}
                    runOnUiThread {
                        actualizarSpinner(nombreRoles)
                    }
                } else {
                    mostrarMensaje("Error al cargar los roles: ${response.message()}")
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
        binding.spRolUsu.adapter = adaptador
    }

    private fun mostrarMensaje(mensaje: String) {
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Información")
        ventana.setMessage(mensaje)
        ventana.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        ventana.create().show()
    }

    private fun limpiarDatos() {
        binding.etLNombreUsu.text.clear()
        binding.etLCorreoUsu.text.clear()
        binding.etLContrasenaUsu.text.clear()
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

}