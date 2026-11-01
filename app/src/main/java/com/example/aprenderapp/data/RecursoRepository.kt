package com.example.aprenderapp.data

import com.example.aprenderapp.model.Recurso
import retrofit2.Call

class RecursoRepository {

    fun obtenerRecursos(): Call<List<Recurso>> {
        return RetrofitClient.api.obtenerRecursos()
    }

    fun crearRecurso(recurso: Recurso): Call<Recurso> {
        return RetrofitClient.api.crearRecurso(recurso)
    }

    fun actualizarRecurso(id: String, recurso: Recurso): Call<Recurso> {
        return RetrofitClient.api.actualizarRecurso(id, recurso)
    }

    fun eliminarRecurso(id: String): Call<Void> {
        return RetrofitClient.api.eliminarRecurso(id)
    }
}