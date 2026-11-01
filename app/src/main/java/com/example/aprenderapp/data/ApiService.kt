package com.example.aprenderapp.data

import com.example.aprenderapp.model.Recurso
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("recursos")
    fun obtenerRecursos(): Call<List<Recurso>>

    @POST("recursos")
    fun crearRecurso(
        @Body recurso: Recurso
    ): Call<Recurso>

    @PUT("recursos/{id}")
    fun actualizarRecurso(
        @Path("id") id: String,
        @Body recurso: Recurso
    ): Call<Recurso>

    @DELETE("recursos/{id}")
    fun eliminarRecurso(
        @Path("id") id: String
    ): Call<Void>
}