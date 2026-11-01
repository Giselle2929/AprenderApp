package com.example.aprenderapp.view

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aprenderapp.MainActivity
import com.example.aprenderapp.R
import com.example.aprenderapp.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logo = findViewById<ImageView>(R.id.ivSplashLogo)
        val title = findViewById<TextView>(R.id.tvSplashTitle)
        val subtitle = findViewById<TextView>(R.id.tvSplashSubtitle)
        val progress = findViewById<ProgressBar>(R.id.pbSplash)
        
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        title.startAnimation(fadeIn)
        subtitle.startAnimation(fadeIn)

        val progressAnimator = ValueAnimator.ofInt(0, 100)
        progressAnimator.duration = 2800
        progressAnimator.interpolator = AccelerateDecelerateInterpolator()
        progressAnimator.addUpdateListener { animator ->
            progress.progress = animator.animatedValue as Int
        }
        
        val pulse = AnimationUtils.loadAnimation(this, R.anim.pulse_infinite)
        logo.startAnimation(pulse)

        progressAnimator.start()

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = FirebaseAuth.getInstance().currentUser
            
            if (currentUser != null) {
                val authRepository = AuthRepository()
                authRepository.obtenerPerfilUsuario(currentUser.uid) { usuario, _ ->
                    if (usuario != null) {
                        val intent = Intent(this, HomeActivity::class.java).apply {
                            putExtra(RecursosActivity.EXTRA_USUARIO_ROL, usuario.rol)
                        }
                        startActivity(intent)
                    } else {
                        // Si falla Firestore pero hay sesión de Auth, enviamos al Welcome
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    finish()
                }
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                @Suppress("DEPRECATION")
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }, 3000)
    }
}