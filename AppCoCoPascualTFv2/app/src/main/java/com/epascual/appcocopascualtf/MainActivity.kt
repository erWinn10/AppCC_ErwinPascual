package com.epascual.appcocopascualtf

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.epascual.appcocopascualtf.databinding.ActivityMainBinding
import com.epascual.appcocopascualtf.interfaz.AdaptadorColeccion
import com.epascual.appcocopascualtf.interfaz.AdaptadorMenu
import com.epascual.appcocopascualtf.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adaptador: AdaptadorColeccion
    private lateinit var adaptadorMenu: AdaptadorMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Recuperar el nombre y el correo del usuario desde SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "Usuario no encontrado")
        val userEmail = sharedPreferences.getString("userEmail", "Correo no encontrado")

        val userId = sharedPreferences.getInt("userId", 0)
        // Mostrar el nombre de usuario en el TextView
        binding.tvRol.text = "$userName \n($userId) ($userEmail)"


        asignar()
        cargarColecciones()
        cargarOpciones()

        // Configurar el botón de menú
        binding.btnMenu.setOnClickListener {
            toggleNavigationView()
        }
    }

    private fun asignar() {
        binding.rvColecciones.layoutManager = LinearLayoutManager(this)
        adaptador = AdaptadorColeccion(this)
        binding.rvColecciones.adapter = adaptador

        binding.rvOpciones.layoutManager = LinearLayoutManager(this)
        adaptadorMenu = AdaptadorMenu(this)
        binding.rvOpciones.adapter = adaptadorMenu
    }

    private fun cargarColecciones() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.getColeccion()
            runOnUiThread {
                if (call.isSuccessful) {
                    adaptador.setlistaColeccion(call.body()!!.lstColeccion)
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

    private fun toggleNavigationView() {
        if (binding.nvMenu.visibility == View.VISIBLE) {
            binding.nvMenu.visibility = View.GONE
        } else {
            binding.nvMenu.visibility = View.VISIBLE
        }
    }
}
