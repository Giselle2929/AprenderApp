package com.example.aprenderapp.data.local

import androidx.room.*
import com.example.aprenderapp.model.Recurso
import kotlinx.coroutines.flow.Flow

@Dao
interface RecursoDao {
    @Query("SELECT * FROM recursos_favoritos")
    fun getFavoritos(): Flow<List<Recurso>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarFavorito(recurso: Recurso)

    @Delete
    suspend fun eliminarFavorito(recurso: Recurso)

    @Query("SELECT EXISTS(SELECT * FROM recursos_favoritos WHERE id = :id)")
    suspend fun esFavorito(id: String): Boolean
}