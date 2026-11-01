package com.example.aprenderapp.controller

import com.example.aprenderapp.data.RecursoRepository
import com.example.aprenderapp.model.Recurso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecursoController(
    private val repository: RecursoRepository,
    private val listener: Listener
) {

    interface Listener {
        fun onRecursosCargados(recursos: List<Recurso>)
        fun onErrorApi()
    }

    interface OperacionListener {
        fun onExito()
        fun onError()
    }

    fun cargarRecursos() {
        repository.obtenerRecursos().enqueue(
            object : Callback<List<Recurso>> {
                override fun onResponse(
                    call: Call<List<Recurso>>,
                    response: Response<List<Recurso>>
                ) {
                    if (response.isSuccessful) {
                        listener.onRecursosCargados(
                            response.body() ?: emptyList()
                        )
                    } else {
                        android.util.Log.e("RecursoController", "Error en respuesta: ${response.code()} - ${response.errorBody()?.string()}")
                        listener.onErrorApi()
                    }
                }

                override fun onFailure(call: Call<List<Recurso>>, t: Throwable) {
                    android.util.Log.e("RecursoController", "Fallo de red: ${t.message}", t)
                    listener.onErrorApi()
                }
            }
        )
    }

    fun crearRecurso(recurso: Recurso, operacion: OperacionListener) {
        repository.crearRecurso(recurso).enqueue(
            object : Callback<Recurso> {
                override fun onResponse(
                    call: Call<Recurso>,
                    response: Response<Recurso>
                ) {
                    if (response.isSuccessful) operacion.onExito()
                    else operacion.onError()
                }

                override fun onFailure(call: Call<Recurso>, t: Throwable) {
                    operacion.onError()
                }
            }
        )
    }

    fun actualizarRecurso(
        id: String,
        recurso: Recurso,
        operacion: OperacionListener
    ) {
        repository.actualizarRecurso(id, recurso).enqueue(
            object : Callback<Recurso> {
                override fun onResponse(
                    call: Call<Recurso>,
                    response: Response<Recurso>
                ) {
                    if (response.isSuccessful) operacion.onExito()
                    else operacion.onError()
                }

                override fun onFailure(call: Call<Recurso>, t: Throwable) {
                    operacion.onError()
                }
            }
        )
    }

    fun eliminarRecurso(id: String, operacion: OperacionListener) {
        repository.eliminarRecurso(id).enqueue(
            object : Callback<Void> {
                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful) operacion.onExito()
                    else operacion.onError()
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    operacion.onError()
                }
            }
        )
    }
}