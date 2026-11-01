package com.example.aprenderapp.repository

import android.util.Log
import com.example.aprenderapp.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "FIREBASE_REPO"

    fun registrarUsuario(
        nombre: String,
        correo: String,
        password: String,
        rol: String,
        alCompletar: (Boolean, String?) -> Unit
    ) {
        Log.d(TAG, "Intentando registrar usuario en Auth: $correo")
        
        auth.createUserWithEmailAndPassword(correo, password)
            .addOnCompleteListener { tarea ->
                if (!tarea.isSuccessful) {
                    val error = tarea.exception?.message ?: "Error desconocido"
                    Log.e(TAG, "ERROR EN AUTH: $error")
                    alCompletar(false, error)
                    return@addOnCompleteListener
                }

                val user = auth.currentUser
                if (user == null) {
                    Log.e(TAG, "Usuario creado pero no se pudo recuperar de la sesión")
                    alCompletar(false, "Error de sesión")
                    return@addOnCompleteListener
                }

                val uid = user.uid
                val rolNormal = rol.trim().uppercase()

                // USAMOS HASHMAP: Evita errores de serialización con las reglas de Firestore
                val datosUsuario = hashMapOf(
                    "uid" to uid,
                    "nombre" to nombre,
                    "correo" to correo,
                    "rol" to rolNormal
                )

                Log.d(TAG, "AUTH OK. Intentando guardar en Firestore: usuarios/$uid")

                db.collection("usuarios")
                    .document(uid)
                    .set(datosUsuario)
                    .addOnSuccessListener {
                        Log.d(TAG, "¡ÉXITO! Usuario guardado en Firestore")
                        alCompletar(true, null)
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "ERROR FIRESTORE: ${e.javaClass.simpleName} - ${e.message}")
                        
                        // Si falla Firestore, borramos de Auth para no dejar cuentas huérfanas
                        user.delete().addOnCompleteListener {
                            Log.w(TAG, "Rollback: Usuario eliminado de Auth por fallo en Firestore")
                            alCompletar(false, "Error en Base de Datos: ${e.localizedMessage}")
                        }
                    }
            }
    }

    fun iniciarSesion(
        correo: String,
        password: String,
        alCompletar: (Usuario?, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(correo, password)
            .addOnCompleteListener { tarea ->
                if (!tarea.isSuccessful) {
                    Log.e(TAG, "Error Login: ${tarea.exception?.message}")
                    alCompletar(null, tarea.exception?.localizedMessage)
                    return@addOnCompleteListener
                }

                val uid = auth.currentUser?.uid ?: return@addOnCompleteListener alCompletar(null, "UID_NULL")
                
                db.collection("usuarios")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        if (!doc.exists()) {
                            Log.e(TAG, "Perfil no encontrado en Firestore para UID: $uid")
                            auth.signOut()
                            alCompletar(null, "PERFIL_NO_ENCONTRADO")
                            return@addOnSuccessListener
                        }

                        val usuario = doc.toObject(Usuario::class.java)
                        if (usuario != null) {
                            usuario.rol = usuario.rol.uppercase()
                        }
                        alCompletar(usuario, null)
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error leyendo Firestore: ${e.message}")
                        auth.signOut()
                        alCompletar(null, e.localizedMessage)
                    }
            }
    }

    fun cerrarSesion() {
        auth.signOut()
    }

    fun getUsuarioActual() = auth.currentUser

    fun obtenerPerfilUsuario(uid: String, alCompletar: (Usuario?, String?) -> Unit) {
        db.collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val usuario = doc.toObject(Usuario::class.java)
                usuario?.let { it.rol = it.rol.uppercase() }
                alCompletar(usuario, null)
            }
            .addOnFailureListener { e ->
                alCompletar(null, e.localizedMessage)
            }
    }

    private var cachedRol: String = "ESTUDIANTE"

    fun setCachedRol(rol: String) {
        cachedRol = rol.uppercase()
    }

    fun obtenerRolActual(): String = cachedRol
}
