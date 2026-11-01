package com.example.aprenderapp.model

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class Usuario(
    var uid: String = "",
    var nombre: String = "",
    var correo: String = "",
    var rol: String = "ESTUDIANTE"
)