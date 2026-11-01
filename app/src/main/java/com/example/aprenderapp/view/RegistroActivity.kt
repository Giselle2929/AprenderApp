package com.example.aprenderapp.view

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aprenderapp.R
import com.example.aprenderapp.repository.AuthRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegistroActivity : AppCompatActivity() {

    private lateinit var etNombre: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmarPassword: TextInputEditText
    private lateinit var actvRol: android.widget.AutoCompleteTextView
    private lateinit var btnRegistrar: MaterialButton

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        inicializarVistas()
        configurarDropdownRol()

        btnRegistrar.setOnClickListener {
            validarFormulario()
        }

        findViewById<android.widget.TextView>(R.id.tvRegisterLogin).setOnClickListener {
            finish()
        }
    }

    private fun inicializarVistas() {
        etNombre = findViewById(R.id.etRegisterName)
        etEmail = findViewById(R.id.etRegisterEmail)
        etPassword = findViewById(R.id.etRegisterPassword)
        etConfirmarPassword = findViewById(R.id.etRegisterConfirmPassword)
        actvRol = findViewById(R.id.actvRegisterRole)
        btnRegistrar = findViewById(R.id.btnRegister)
    }

    private fun configurarDropdownRol() {
        val roles = resources.getStringArray(R.array.user_roles)
        val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        actvRol.setAdapter(adapter)
    }

    private fun validarFormulario() {
        val nombre = etNombre.text?.toString()?.trim().orEmpty()
        val correo = etEmail.text?.toString()?.trim().orEmpty()
        val password = etPassword.text?.toString().orEmpty()
        val confirmar = etConfirmarPassword.text?.toString().orEmpty()
        val rol = actvRol.text?.toString().orEmpty()

        if (nombre.isBlank()) {
            mostrarMensaje(R.string.error_name_required)
            etNombre.requestFocus()
            return
        }

        if (correo.isBlank()) {
            mostrarMensaje(R.string.error_email_required)
            etEmail.requestFocus()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            mostrarMensaje(R.string.error_email_invalid)
            etEmail.requestFocus()
            return
        }

        if (!cumpleRequisitosPassword(password)) {
            mostrarMensaje(R.string.error_password_requirements)
            etPassword.requestFocus()
            return
        }

        if (password != confirmar) {
            mostrarMensaje(R.string.error_password_mismatch)
            etConfirmarPassword.requestFocus()
            return
        }

        if (rol.isBlank()) {
            mostrarMensaje(R.string.error_role_required)
            actvRol.requestFocus()
            return
        }

        registrarEnFirebase(nombre, correo, password, rol)
    }

    private fun cumpleRequisitosPassword(password: String): Boolean {
        val pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])(?=\\S+$).{12,}$"
        return password.matches(Regex(pattern))
    }

    private fun registrarEnFirebase(
        nombre: String,
        correo: String,
        password: String,
        rol: String
    ) {
        btnRegistrar.isEnabled = false

        // Mapeo robusto usando constantes
        val rolNormalizado = when {
            rol.contains(getString(R.string.rol_docente), ignoreCase = true) -> RecursosActivity.ROL_DOCENTE
            else -> RecursosActivity.ROL_ESTUDIANTE
        }

        android.util.Log.d("RegistroActivity", "Rol original: $rol -> Normalizado: $rolNormalizado")

        authRepository.registrarUsuario(
            nombre = nombre,
            correo = correo,
            password = password,
            rol = rolNormalizado
        ) { exito, error ->

            btnRegistrar.isEnabled = true

            if (exito) {
                Toast.makeText(
                    this,
                    getString(R.string.register_success),
                    Toast.LENGTH_LONG
                ).show()

                finish()
            } else {
                val mensajeError = when {
                    error?.contains("FirebaseAuthUserCollisionException") == true || 
                    error?.contains("already in use") == true -> 
                        getString(R.string.error_email_already_exists)
                    else -> getString(R.string.register_error)
                }

                Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show()

                if (!error.isNullOrBlank()) {
                    android.util.Log.e(
                        "RegistroActivity",
                        "Error al registrar usuario: $error"
                    )
                }
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