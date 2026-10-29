package com.example.aprendeapp.view

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aprendeapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        inicializarVistas()
        configurarEventos()
    }

    private fun inicializarVistas() {
        etEmail = findViewById(R.id.etLoginEmail)
        etPassword = findViewById(R.id.etLoginPassword)
    }

    private fun configurarEventos() {

        val btnLogin = findViewById<MaterialButton>(
            R.id.btnLogin
        )

        btnLogin.setOnClickListener {
            validarFormulario()
        }
    }

    private fun validarFormulario() {

        val email = etEmail.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val password = etPassword.text
            ?.toString()
            .orEmpty()

        if (email.isBlank()) {
            mostrarMensaje(R.string.error_email_required)
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mostrarMensaje(R.string.error_email_invalid)
            etEmail.requestFocus()
            return
        }

        if (password.isBlank()) {
            mostrarMensaje(R.string.error_password_required)
            etPassword.requestFocus()
            return
        }

        mostrarMensaje(R.string.login_pending)
    }

    private fun mostrarMensaje(mensajeId: Int) {
        Toast.makeText(
            this,
            getString(mensajeId),
            Toast.LENGTH_SHORT
        ).show()
    }
}