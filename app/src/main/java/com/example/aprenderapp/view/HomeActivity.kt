package com.example.aprenderapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aprenderapp.MainActivity
import com.example.aprenderapp.R
import com.example.aprenderapp.model.Usuario
import com.example.aprenderapp.repository.AuthRepository
import com.google.android.material.card.MaterialCardView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        authRepository = AuthRepository()

        setupUI()
        loadUserData()
    }

    private fun setupUI() {
        findViewById<MaterialCardView>(R.id.cardRecursos).setOnClickListener {
            val intent = Intent(this, RecursosActivity::class.java)
            intent.putExtra(RecursosActivity.EXTRA_USUARIO_ROL, authRepository.obtenerRolActual())
            startActivity(intent)
        }

        // Configurar Quick Actions
        val actionFavs = findViewById<androidx.cardview.widget.CardView>(R.id.actionFavs)
        actionFavs.findViewById<android.widget.TextView>(R.id.tvActionTitle).text = getString(R.string.menu_favorites)
        actionFavs.findViewById<android.widget.ImageView>(R.id.ivActionIcon).setImageResource(R.drawable.ic_heart_filled)
        actionFavs.setOnClickListener {
            val intent = Intent(this, RecursosActivity::class.java)
            intent.putExtra(RecursosActivity.EXTRA_SOLO_FAVORITOS, true)
            intent.putExtra(RecursosActivity.EXTRA_USUARIO_ROL, authRepository.obtenerRolActual())
            startActivity(intent)
        }

        val actionProfile = findViewById<androidx.cardview.widget.CardView>(R.id.actionProfile)
        actionProfile.findViewById<android.widget.TextView>(R.id.tvActionTitle).text = getString(R.string.menu_profile)
        actionProfile.findViewById<android.widget.ImageView>(R.id.ivActionIcon).setImageResource(R.drawable.ic_logo_vector)
        actionProfile.setOnClickListener {
            mostrarDialogoPerfil()
        }

        findViewById<TextView>(R.id.btnLogout).setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private var perfilUsuario: Usuario? = null

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            authRepository.obtenerPerfilUsuario(user.uid) { profile, _ ->
                if (profile != null) {
                    perfilUsuario = profile
                    authRepository.setCachedRol(profile.rol)
                    val tvWelcome = findViewById<TextView>(R.id.tvDashboardWelcome)
                    val tvRole = findViewById<TextView>(R.id.tvDashboardRoleTitle)
                    val tvSubtitle = findViewById<TextView>(R.id.tvDashboardSubtitle)
                    
                    tvWelcome.text = getString(R.string.dashboard_welcome, profile.nombre)
                    
                    val esDocente = profile.rol.uppercase() == RecursosActivity.ROL_DOCENTE
                    
                    // Ocultar sección de favoritos para Docentes
                    val actionFavs = findViewById<androidx.cardview.widget.CardView>(R.id.actionFavs)
                    actionFavs.visibility = if (esDocente) android.view.View.GONE else android.view.View.VISIBLE

                    tvRole.text = if (esDocente) {
                        getString(R.string.dashboard_role_teacher)
                    } else {
                        getString(R.string.dashboard_role_student)
                    }

                    tvSubtitle.text = if (esDocente) {
                        getString(R.string.home_repo_subtitle_teacher)
                    } else {
                        getString(R.string.home_repo_subtitle_student)
                    }
                }
            }
        }
    }

    private fun mostrarDialogoPerfil() {
        val profile = perfilUsuario ?: return
        val bottomSheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_perfil, null)

        view.findViewById<TextView>(R.id.tvPerfilNombre).text = profile.nombre
        view.findViewById<TextView>(R.id.tvPerfilEmail).text = getString(R.string.correo_usuario_label, profile.correo)
        view.findViewById<TextView>(R.id.tvPerfilRol).text = getString(R.string.rol_usuario_label, profile.rol.uppercase())

        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCerrarPerfil).setOnClickListener {
            bottomSheet.dismiss()
        }

        bottomSheet.setContentView(view)
        bottomSheet.show()
    }
}
