package com.example.aprenderapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "recursos_favoritos")
data class Recurso(
    @PrimaryKey 
    val id: String = "",
    
    @SerializedName("title")
    val title: String = "",
    
    @SerializedName("description")
    val description: String = "",
    
    @SerializedName("type")
    val type: String = "",
    
    @SerializedName("link")
    val link: String = "",
    
    @SerializedName("image")
    val image: String = "",

    @SerializedName("rating")
    var rating: Float = 0.0f,

    var isFavorite: Boolean = false
)