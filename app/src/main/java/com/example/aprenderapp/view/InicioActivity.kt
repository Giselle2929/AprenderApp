package com.example.aprenderapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aprenderapp.MainActivity
import com.example.aprenderapp.R
import com.example.aprenderapp.repository.AuthRepository
import com.google.android.material.button.MaterialButton

class InicioActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Obtener datos del Intent
        val nombre = intent.getStringExtra("usuario_nombre").orEmpty()
        val rol = intent.getStringExtra("usuario_rol").orEmpty()

        // Inicializar vistas
        val tvRoleTitle = findViewById<TextView>(R.id.tvDashboardRoleTitle)
        val tvWelcome = findViewById<TextView>(R.id.tvDashboardWelcome)
        val tvSubtitle = findViewById<TextView>(R.id.tvDashboardSubtitle)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        // Configurar UI según el rol
        val esDocente = rol.equals("DOCENTE", ignoreCase = true)
        
        tvRoleTitle.text = if (esDocente) {
            getString(R.string.dashboard_teacher_title)
        } else {
            getString(R.string.dashboard_student_title)
        }

        tvSubtitle.text = if (esDocente) {
            getString(R.string.dashboard_subtitle)
        } else {
            getString(R.string.dashboard_explore_desc)
        }

        tvWelcome.text = getString(R.string.dashboard_welcome, nombre)

        // Configurar Acciones Rápidas (Favoritos y Perfil)
        val viewFavs = findViewById<android.view.View>(R.id.actionFavs)
        viewFavs.findViewById<android.widget.ImageView>(R.id.ivActionIcon).setImageResource(android.R.drawable.btn_star_big_on)
        viewFavs.findViewById<android.widget.TextView>(R.id.tvActionTitle).text = getString(R.string.menu_favorites)

        val viewProfile = findViewById<android.view.View>(R.id.actionProfile)
        viewProfile.findViewById<android.widget.ImageView>(R.id.ivActionIcon).setImageResource(android.R.drawable.ic_menu_myplaces)
        viewProfile.findViewById<android.widget.TextView>(R.id.tvActionTitle).text = getString(R.string.menu_profile)

        // Navegar a Recursos
        findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardRecursos).setOnClickListener {
            val intent = Intent(this, RecursosActivity::class.java).apply {
                putExtra("usuario_rol", rol.uppercase().trim())
            }
            startActivity(intent)
        }

        // Click en Favoritos
        viewFavs.setOnClickListener {
            val intent = Intent(this, RecursosActivity::class.java).apply {
                putExtra("usuario_rol", rol.uppercase().trim())
                putExtra("SOLO_FAVORITOS", true)
            }
            startActivity(intent)
        }

        // Click en Perfil
        viewProfile.setOnClickListener {
            mostrarDialogoPerfil(nombre, rol, authRepository.getUsuarioActual()?.email ?: "No disponible")
        }

        // Configurar botón de cerrar sesión
        btnLogout.setOnClickListener {
            authRepository.cerrarSesion()
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    private fun mostrarDialogoPerfil(nombre: String, rol: String, email: String) {
        val bottomSheet = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_perfil, null)

        view.findViewById<TextView>(R.id.tvPerfilNombre).text = nombre
        view.findViewById<TextView>(R.id.tvPerfilEmail).text = getString(R.string.correo_usuario_label, email)
        view.findViewById<TextView>(R.id.tvPerfilRol).text = getString(R.string.rol_usuario_label, rol.uppercase())

        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCerrarPerfil).setOnClickListener {
            bottomSheet.dismiss()
        }

        bottomSheet.setContentView(view)
        bottomSheet.show()
    }
}