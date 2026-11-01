package com.example.aprenderapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aprenderapp.R
import com.example.aprenderapp.repository.AuthRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etLoginEmail)
        etPassword = findViewById(R.id.etLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            validarFormulario()
        }
        
        findViewById<android.widget.TextView>(R.id.tvLoginRegister).setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validarFormulario() {
        val correo = etEmail.text?.toString()?.trim().orEmpty()
        val password = etPassword.text?.toString().orEmpty()

        if (correo.isBlank()) {
            mostrarMensaje(R.string.error_email_required)
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            mostrarMensaje(R.string.error_email_invalid)
            etEmail.requestFocus()
            return
        }

        if (password.isBlank()) {
            mostrarMensaje(R.string.error_password_required)
            etPassword.requestFocus()
            return
        }

        autenticarUsuario(correo, password)
    }

    private fun autenticarUsuario(correo: String, password: String) {
        btnLogin.isEnabled = false

        authRepository.iniciarSesion(correo, password) { usuario, error ->
            runOnUiThread {
                btnLogin.isEnabled = true

                if (usuario == null) {
                    val mensaje = when (error) {
                        "PERFIL_NO_ENCONTRADO" ->
                            R.string.login_profile_missing
                        "PERFIL_INVALIDO" ->
                            R.string.login_profile_invalid
                        else ->
                            R.string.login_error
                    }

                    mostrarMensaje(mensaje)
                    return@runOnUiThread
                }

                val destino = when (usuario.rol.uppercase()) {
                    RecursosActivity.ROL_ESTUDIANTE, 
                    RecursosActivity.ROL_DOCENTE -> HomeActivity::class.java
                    else -> {
                        mostrarMensaje(R.string.login_profile_invalid)
                        return@runOnUiThread
                    }
                }

                val intent = Intent(this, destino).apply {
                    putExtra(RecursosActivity.EXTRA_USUARIO_ROL, usuario.rol)
                }

                startActivity(intent)
                finish()
            }
        }
    }

    private fun mostrarMensaje(mensajeId: Int) {
        Toast.makeText(
            this,
            getString(mensajeId),
            Toast.LENGTH_LONG
        ).show()
    }
}