package com.example.aprenderapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aprenderapp.data.RetrofitClient
import com.example.aprenderapp.model.Recurso
import com.example.aprenderapp.view.LoginActivity
import com.example.aprenderapp.view.RegistroActivity
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        startActivity(Intent(this, destino))
    }


}