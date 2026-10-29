package com.example.aprenderapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aprenderapp.view.LoginActivity
import com.example.aprenderapp.view.RegistroActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        configurarBotones()
    }

    private fun configurarBotones() {

        val btnLogin = findViewById<MaterialButton>(
            R.id.btnWelcomeLogin
        )

        val btnRegister = findViewById<MaterialButton>(
            R.id.btnWelcomeRegister
        )

        btnLogin.setOnClickListener {
            abrirPantalla(LoginActivity::class.java)
        }

        btnRegister.setOnClickListener {
            abrirPantalla(RegistroActivity::class.java)
        }
    }

    private fun abrirPantalla(
        destino: Class<out AppCompatActivity>
    ) {
        val intent = Intent(this, destino)
        startActivity(intent)
    }
}