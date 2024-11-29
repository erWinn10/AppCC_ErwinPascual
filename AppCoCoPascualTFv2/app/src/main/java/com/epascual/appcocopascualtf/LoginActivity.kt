package com.epascual.appcocopascualtf

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginLeft
import com.epascual.appcocopascualtf.databinding.ActivityLoginBinding
import com.epascual.appcocopascualtf.entidades.LoginRequest
import com.epascual.appcocopascualtf.entidades.Usuario
import com.epascual.appcocopascualtf.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        asignar()
    }

    private fun asignar() {

        binding.imLLogo.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }

        binding.btnSegundon.setOnClickListener {
            segundon()
        }

        binding.btnRLogin.setOnClickListener {
            if(binding.btnPrincipal.text == "Iniciar Sesión"){
                iniciarSesion()
            }else{
                registrar()
            }
        }
    }

    private fun iniciarSesion() {
        val correo = binding.etLCorreo.text.toString()
        val contrasena = binding.etLContrasena.text.toString()
        var validar = true

        // Validar campos vacíos
        if (correo.isEmpty()) {
            binding.etLCorreo.error = "Ingrese su correo"
            validar = false
        }

        if (contrasena.isEmpty()) {
            binding.etLContrasena.error = "Ingrese su contraseña"
            validar = false
        }

        if (validar) {
            CoroutineScope(Dispatchers.IO).launch {
                val loginRequest = LoginRequest(correo, contrasena)
                val call = RetrofitClient.webService.loginUsuario(loginRequest)
                runOnUiThread {
                    if (call.isSuccessful) {
                        CoroutineScope(Dispatchers.IO).launch {
                            // Obtener la lista de usuarios
                            val usuariosResponse = RetrofitClient.webService.getUsuario()
                            runOnUiThread {
                                if (usuariosResponse.isSuccessful) {
                                    val listaUsuarios = usuariosResponse.body()?.lstUsuarios
                                    val usuario = listaUsuarios?.find { it.usu_correo == correo } // Busca el usuario por correo
                                    if (usuario != null) {
                                        guardarUsuarioEnSharedPreferences(usuario) // Guarda los datos en SharedPreferences
                                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        mostrarError("Usuario no encontrado en la lista")
                                    }
                                } else {
                                    mostrarError("Error al obtener la lista de usuarios")
                                }
                            }
                        }
                    }else{
                        // Mostrar un cuadro de diálogo si las credenciales son incorrectas
                        mostrarError("Correo o contraseña incorrectos")
                    }
                }
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Error")
        ventana.setMessage(mensaje)
        ventana.setPositiveButton("Aceptar") { dialog, which -> dialog.dismiss() }
        ventana.create().show()
    }


    private fun registrar() {
        val nombre = binding.etLNombre.text.toString()
        val correo = binding.etLCorreo.text.toString()
        val contrasena = binding.etLContrasena.text.toString()
        var validar = true
        val usuario = Usuario(0, nombre, correo, contrasena, 3)

        // Validar campos vacíos
        if (nombre.isEmpty()) {
            binding.etLNombre.error = "Ingrese su nombre"
            validar = false
        }
        if (correo.isEmpty()) {
            binding.etLCorreo.error = "Ingrese su correo"
            validar = false
        }
        if (contrasena.isEmpty()) {
            binding.etLContrasena.error = "Ingrese su contraseña"
            validar = false
        }

        if (validar) {
            CoroutineScope(Dispatchers.IO).launch {
                val call = RetrofitClient.webService.agregarUsuario(usuario)
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

    private fun mostrarMensaje(mensaje: String) {
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Información")
        ventana.setMessage(mensaje)
        ventana.setPositiveButton("Aceptar") { dialog, which -> dialog.dismiss() } // Solo cerrar el diálogo
        ventana.create().show()
    }


    private fun mostrarMensaje2(mensaje: String){
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Información")
        ventana.setMessage(mensaje)
        ventana.setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })
        ventana.create().show()
    }

    private fun segundon(){
        if (binding.btnPrincipal.text == "Iniciar Sesión") {
            // Mostrar el TextView y el EditText
            binding.tvLNombre.visibility = View.VISIBLE
            binding.etLNombre.visibility = View.VISIBLE

            // Ocultar el TextView de Bienvenido
            binding.tvLBienvenido.visibility = View.INVISIBLE

            binding.etLNombre.requestFocus()
            val params = binding.btnPrincipal.layoutParams as ViewGroup.MarginLayoutParams
            params.marginStart = 420 // Cambia el margen izquierdo
            params.marginEnd = 40 // Cambia el margen derecho
            binding.btnPrincipal.layoutParams = params // Aplica los nuevos parámetros
            binding.btnPrincipal.text = "Registrarse"
            binding.btnRLogin.text = "Registrarse"
            binding.btnSegundon.setPadding(0, 0, 380, 0) // Quita el padding (izquierdo, superior, derecho, inferior)
            binding.btnSegundon.text = "Iniciar Sesión"
        }
        else{
            // Mostrar el TextView y el EditText
            binding.tvLNombre.visibility = View.INVISIBLE
            binding.etLNombre.visibility = View.INVISIBLE

            // Ocultar el TextView de Bienvenido
            binding.tvLBienvenido.visibility = View.VISIBLE

            binding.etLCorreo.requestFocus()
            val params = binding.btnPrincipal.layoutParams as ViewGroup.MarginLayoutParams
            params.marginStart = 40 // Cambia el margen izquierdo
            params.marginEnd = 420 // Cambia el margen derecho
            binding.btnPrincipal.layoutParams = params // Aplica los nuevos parámetros
            binding.btnPrincipal.text = "Iniciar Sesión"
            binding.btnRLogin.text = "Iniciar Sesión"
            binding.btnSegundon.setPadding(380, 0, 0, 0)
            binding.btnSegundon.text = "Registrarse"
        }
    }
    private fun limpiarDatos() {
        binding.etLNombre.text.clear()
        binding.etLCorreo.text.clear()
        binding.etLContrasena.text.clear()
    }

    private fun guardarUsuarioEnSharedPreferences(usuario: Usuario) {
        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userEmail", usuario.usu_correo)
        editor.putString("userName", usuario.usu_nombre)
        editor.putInt("userId", usuario.usu_id)
        editor.putInt("userRol", usuario.rol_id)

        editor.apply()
    }
}