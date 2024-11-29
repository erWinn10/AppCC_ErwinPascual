package com.epascual.appcocopascualtf.servicio

import com.epascual.appcocopascualtf.entidades.Articulo
import com.epascual.appcocopascualtf.entidades.Coleccion
import com.epascual.appcocopascualtf.entidades.LoginRequest
import com.epascual.appcocopascualtf.entidades.Pedido
import com.epascual.appcocopascualtf.entidades.Usuario
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

object AppConstantes{
    const val BASE_URL = "http://192.168.18.179:1027"
}

interface WebService {
    @GET("/coleccion")
    suspend fun getColeccion():Response<ColeccionResponse>

    @GET("/articulo")
    suspend fun getArticulo():Response<ArticuloResponse>

    @GET("/usuarios")
    suspend fun getUsuario():Response<UsuarioResponse>

    @GET("/pedidos")
    suspend fun getPedido():Response<PedidoResponse>

    @GET("/roles")
    suspend fun getRol():Response<RolResponse>

    @POST("/usuarios/agregar")
    suspend fun agregarUsuario(@Body usuario: Usuario):Response<String>

    /*@GET("/usuarios/{correo}")
    suspend fun loginUsuario(@Path("correo") correo:String):Response<Usuario>*/

    @POST("/usuarios/login")
    suspend fun loginUsuario(@Body loginRequest: LoginRequest): Response<Usuario>

    @POST("/pedidos/agregar")
    suspend fun agregarPedido(@Body pedido: Pedido):Response<String>

    @POST("/articulo/agregar")
    suspend fun agregarArticulo(@Body articulo: Articulo):Response<String>

    @POST("/coleccion/agregar")
    suspend fun agregarColeccion(@Body coleccion: Coleccion):Response<String>

    @DELETE("/pedidos/eliminar/{id}")
    suspend fun eliminarPedido(@Path("id") id:Int):Response<String>

    @DELETE("/usuarios/eliminar/{id}")
    suspend fun eliminarUsuarios(@Path("id") id:Int):Response<String>

    @DELETE("/coleccion/eliminar/{id}")
    suspend fun eliminarColeccion(@Path("id") id:Int):Response<String>

    @DELETE("/articulo/eliminar/{id}")
    suspend fun eliminarArticulo(@Path("id") id:Int):Response<String>

    @PUT("/coleccion/modificar/{id}")
    suspend fun modificarColeccion(@Path("id") id: Int, @Body coleccion: Coleccion):Response<String>

}

object RetrofitClient {
    val webService: WebService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstantes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(WebService::class.java)
    }}