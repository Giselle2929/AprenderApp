package com.example.aprenderapp.view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aprenderapp.R
import com.example.aprenderapp.controller.RecursoController
import com.example.aprenderapp.data.RecursoRepository
import com.example.aprenderapp.model.Recurso

import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText

class FormularioRecursoActivity : AppCompatActivity() {

    private lateinit var controlador: RecursoController
    private lateinit var edtTitulo: TextInputEditText
    private lateinit var edtDescripcion: TextInputEditText
    private lateinit var edtTipo: TextInputEditText
    private lateinit var edtEnlace: TextInputEditText
    private lateinit var edtImagen: TextInputEditText
    private lateinit var progressFormulario: android.widget.ProgressBar
    private lateinit var btnGuardar: android.widget.Button
    private var modoEdicion = false
    private var recursoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_recurso)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarFormulario)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        edtTitulo = findViewById(R.id.edtTitulo)
        edtDescripcion = findViewById(R.id.edtDescripcion)
        edtTipo = findViewById(R.id.edtTipo)
        edtEnlace = findViewById(R.id.edtEnlace)
        edtImagen = findViewById(R.id.edtImagen)
        progressFormulario = findViewById(R.id.progressFormulario)
        btnGuardar = findViewById(R.id.btnGuardarRecurso)

        modoEdicion = intent.getBooleanExtra(RecursosActivity.EDIT_MODE, false)
        if (modoEdicion) {
            recursoId = intent.getStringExtra(RecursosActivity.RECURSO_ID)
            edtTitulo.setText(intent.getStringExtra(RecursosActivity.RECURSO_TITULO))
            edtDescripcion.setText(intent.getStringExtra(RecursosActivity.RECURSO_DESC))
            edtTipo.setText(intent.getStringExtra(RecursosActivity.RECURSO_TIPO))
            edtEnlace.setText(intent.getStringExtra(RecursosActivity.RECURSO_LINK))
            edtImagen.setText(intent.getStringExtra(RecursosActivity.RECURSO_IMAGEN))
            btnGuardar.text = getString(R.string.guardar_recurso)
            toolbar.title = getString(R.string.opcion_editar)
        }

        controlador = RecursoController(
            RecursoRepository(),
            object : RecursoController.Listener {
                override fun onRecursosCargados(recursos: List<Recurso>) {}
                override fun onErrorApi() {}
            }
        )

        btnGuardar.setOnClickListener {
            guardarRecurso()
        }
    }

    private fun guardarRecurso() {
        val titulo = edtTitulo.text.toString().trim()
        val descripcion = edtDescripcion.text.toString().trim()
        val tipo = edtTipo.text.toString().trim()
        val enlace = edtEnlace.text.toString().trim()
        val imagen = edtImagen.text.toString().trim()

        if (titulo.isEmpty() || descripcion.isEmpty() ||
            tipo.isEmpty() || enlace.isEmpty()
        ) {
            Toast.makeText(this, getString(R.string.error_campos), Toast.LENGTH_SHORT).show()
            return
        }

        // Mostrar Loader y deshabilitar botón
        progressFormulario.visibility = android.view.View.VISIBLE
        btnGuardar.isEnabled = false

        val recurso = Recurso(
            id = recursoId ?: "",
            title = titulo,
            description = descripcion,
            type = tipo,
            link = enlace,
            image = imagen,
            rating = intent.getFloatExtra(RecursosActivity.RECURSO_RATING, 0.0f),
            isFavorite = intent.getBooleanExtra(RecursosActivity.RECURSO_FAV, false)
        )

        val operacionListener = object : RecursoController.OperacionListener {
            override fun onExito() {
                runOnUiThread {
                    progressFormulario.visibility = android.view.View.GONE
                    Toast.makeText(this@FormularioRecursoActivity, getString(R.string.recurso_guardado), Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
            }

            override fun onError() {
                runOnUiThread {
                    progressFormulario.visibility = android.view.View.GONE
                    btnGuardar.isEnabled = true
                    Toast.makeText(this@FormularioRecursoActivity, getString(R.string.error_guardar), Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (modoEdicion && recursoId != null) {
            controlador.actualizarRecurso(recursoId!!, recurso, operacionListener)
        } else {
            controlador.crearRecurso(recurso, operacionListener)
        }
    }
}