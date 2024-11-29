package com.epascual.appcocopascualtf

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.epascual.appcocopascualtf.databinding.ActivityDpedidosBinding
import com.epascual.appcocopascualtf.entidades.Pedido
import com.epascual.appcocopascualtf.interfaz.AdaptadorPedido
import com.epascual.appcocopascualtf.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DPedidosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDpedidosBinding
    private var adaptador: AdaptadorPedido = AdaptadorPedido()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDpedidosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        asignar()
        cargarPedidos()
        // Configurar el botón de menú
        binding.btnMenu.setOnClickListener {
            toggleNavigationView()
        }
    }
    private fun asignar(){
        binding.rvPedidos.layoutManager = LinearLayoutManager(this)
        adaptador.setContext(this)




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

    private fun eliminar(pedido: Pedido){
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Eliminar")
        ventana.setMessage("¿Desea eliminar el pedido de id: ${pedido.ped_id} ?")
        ventana.setPositiveButton("Si", DialogInterface.OnClickListener { dialog, which ->
            CoroutineScope(Dispatchers.IO).launch {
                val call = RetrofitClient.webService.eliminarPedido(pedido.ped_id)
                runOnUiThread {
                    if (call.isSuccessful) {
                        mostrarMensaje(call.body().toString())
                        cargarPedidos()
                    }
                }
            }
        })
        ventana.setNegativeButton("No", null)
        ventana.show()
    }

    private fun mostrarMensaje(mensaje: String) {
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Información")
        ventana.setMessage(mensaje)
        ventana.setPositiveButton("Aceptar") { dialog, which -> dialog.dismiss() } // Solo cerrar el diálogo
        ventana.create().show()
    }

    private fun cargarPedidos(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.getPedido()
            val call2 = RetrofitClient.webService.getArticulo()

            runOnUiThread {
                if (call.isSuccessful && call2.isSuccessful) {
                    adaptador.setListaPedido(call.body()!!.lstPedidos)
                    adaptador.setListaArticulos(call2.body()!!.lstArticulo)

                    binding.rvPedidos.adapter = adaptador
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