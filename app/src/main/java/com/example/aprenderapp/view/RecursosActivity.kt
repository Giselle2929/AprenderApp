package com.example.aprenderapp.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aprenderapp.R
import com.example.aprenderapp.controller.RecursoController
import com.example.aprenderapp.data.RecursoRepository
import com.example.aprenderapp.data.local.AppDatabase
import com.example.aprenderapp.model.Recurso
import com.example.aprenderapp.view.adapter.RecursoAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RecursosActivity : AppCompatActivity(), RecursoController.Listener {

    private lateinit var adaptador: RecursoAdapter
    private lateinit var indicadorCarga: ProgressBar
    private lateinit var txtEstado: TextView
    private lateinit var controlador: RecursoController
    private lateinit var btnNuevo: FloatingActionButton
    private lateinit var etSearch: TextInputEditText
    private var listaOriginal: List<Recurso> = emptyList()
    private var usuarioRol: String = ROL_ESTUDIANTE

    private var soloFavoritos: Boolean = false
    private var criterioOrden: String = ORDEN_TITULO

    companion object {
        const val EXTRA_USUARIO_ROL = "usuario_rol"
        const val EXTRA_SOLO_FAVORITOS = "SOLO_FAVORITOS"
        
        const val ROL_DOCENTE = "DOCENTE"
        const val ROL_ESTUDIANTE = "ESTUDIANTE"
        
        const val ORDEN_TITULO = "TITULO"
        const val ORDEN_TIPO = "TIPO"
        const val ORDEN_RATING = "RATING"
        
        const val EDIT_MODE = "EDIT_MODE"
        const val RECURSO_ID = "RECURSO_ID"
        const val RECURSO_TITULO = "RECURSO_TITULO"
        const val RECURSO_DESC = "RECURSO_DESC"
        const val RECURSO_TIPO = "RECURSO_TIPO"
        const val RECURSO_LINK = "RECURSO_LINK"
        const val RECURSO_IMAGEN = "RECURSO_IMAGEN"
        const val RECURSO_RATING = "RECURSO_RATING"
        const val RECURSO_FAV = "RECURSO_FAV"
    }

    private val formularioLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            cargarRecursos()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recursos)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarRecursos)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        usuarioRol = intent.getStringExtra(EXTRA_USUARIO_ROL) ?: ROL_ESTUDIANTE
        soloFavoritos = intent.getBooleanExtra(EXTRA_SOLO_FAVORITOS, false)

        val recyclerRecursos = findViewById<RecyclerView>(R.id.recyclerRecursos)
        indicadorCarga = findViewById(R.id.progressRecursos)
        txtEstado = findViewById(R.id.txtEstadoRecursos)
        btnNuevo = findViewById(R.id.btnNuevoRecurso)
        etSearch = findViewById(R.id.etSearchRecursos)

        // Control de roles: solo docentes agregan
        btnNuevo.visibility = if (usuarioRol.equals(ROL_DOCENTE, ignoreCase = true)) View.VISIBLE else View.GONE

        val db = AppDatabase.getDatabase(this)
        val dao = db.recursoDao()

        adaptador = RecursoAdapter(
            emptyList(),
            usuarioRol,
            alPresionarFavorito = { recurso ->
                lifecycleScope.launch {
                    try {
                        if (recurso.isFavorite) {
                            dao.insertarFavorito(recurso)
                        } else {
                            dao.eliminarFavorito(recurso)
                        }

                        // Sincronizar con la lista original
                        listaOriginal.find { it.id == recurso.id }?.isFavorite = recurso.isFavorite

                        if (!recurso.isFavorite && soloFavoritos) {
                            runOnUiThread {
                                val nuevaLista = adaptador.obtenerLista().filter { it.id != recurso.id }
                                adaptador.actualizarLista(nuevaLista)
                                actualizarEstadoVacio(nuevaLista.isEmpty())
                            }
                        }
                    } catch (_: Exception) {
                        recurso.isFavorite = !recurso.isFavorite
                        // Sincronizar reversión
                        listaOriginal.find { it.id == recurso.id }?.isFavorite = recurso.isFavorite
                        adaptador.notifyDataSetChanged()
                        Toast.makeText(this@RecursosActivity, R.string.error_favorito, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            alCambiarRating = { recurso, nuevoRating ->
                actualizarRating(recurso, nuevoRating)
            },
            alSeleccionar = { recurso ->
                if (usuarioRol.equals(ROL_DOCENTE, ignoreCase = true)) {
                    mostrarOpcionesDocente(recurso)
                } else {
                    mostrarDetalleRecurso(recurso)
                }
            }
        )

        recyclerRecursos.layoutManager = LinearLayoutManager(this)
        recyclerRecursos.adapter = adaptador

        btnNuevo.setOnClickListener {
            val intent = Intent(this, FormularioRecursoActivity::class.java)
            formularioLauncher.launch(intent)
        }

        configurarBusqueda()

        controlador = RecursoController(RecursoRepository(), this)

        cargarRecursos()
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_recursos, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_title -> {
                ordenarLista(ORDEN_TITULO)
                true
            }
            R.id.action_sort_type -> {
                ordenarLista(ORDEN_TIPO)
                true
            }
            R.id.action_sort_rating -> {
                ordenarLista(ORDEN_RATING)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun ordenarLista(criterio: String) {
        criterioOrden = criterio
        val listaActual = adaptador.obtenerLista().toMutableList()
        aplicarOrden(listaActual)
        adaptador.actualizarLista(listaActual)
        
        val nombreCriterio = when (criterio) {
            ORDEN_TITULO -> getString(R.string.orden_titulo)
            ORDEN_TIPO -> getString(R.string.orden_tipo)
            ORDEN_RATING -> getString(R.string.orden_valoracion)
            else -> criterio
        }
        Toast.makeText(this, getString(R.string.msg_ordenado_por, nombreCriterio), Toast.LENGTH_SHORT).show()
    }

    private fun aplicarOrden(lista: MutableList<Recurso>) {
        when (criterioOrden) {
            ORDEN_TITULO -> lista.sortBy { it.title.lowercase() }
            ORDEN_TIPO -> lista.sortBy { it.type.lowercase() }
            ORDEN_RATING -> lista.sortByDescending { it.rating }
        }
    }

    private fun configurarBusqueda() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filtrar(texto: String?) {
        val filtrados = if (texto.isNullOrBlank()) {
            listaOriginal
        } else {
            listaOriginal.filter {
                it.title.contains(texto, ignoreCase = true) ||
                it.type.contains(texto, ignoreCase = true) ||
                it.id.equals(texto, ignoreCase = true)
            }
        }
        
        val listaOrdenada = filtrados.toMutableList()
        aplicarOrden(listaOrdenada)
        adaptador.actualizarLista(listaOrdenada)
        
        actualizarEstadoVacio(listaOrdenada.isEmpty())
    }

    private fun actualizarEstadoVacio(isEmpty: Boolean) {
        if (isEmpty) {
            txtEstado.text = if (soloFavoritos) {
                getString(R.string.no_favoritos)
            } else {
                getString(R.string.lista_vacia)
            }
            txtEstado.visibility = View.VISIBLE
        } else {
            txtEstado.visibility = View.GONE
        }
    }

    private fun actualizarRating(recurso: Recurso, nuevoRating: Float) {
        val recursoActualizado = recurso.copy(rating = nuevoRating)
        controlador.actualizarRecurso(recurso.id, recursoActualizado, object : RecursoController.OperacionListener {
            override fun onExito() {
                runOnUiThread {
                    recurso.rating = nuevoRating
                    // Sincronizar con la lista original
                    listaOriginal.find { it.id == recurso.id }?.rating = nuevoRating
                    Toast.makeText(this@RecursosActivity, R.string.rating_actualizado, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError() {
                runOnUiThread {
                    adaptador.notifyDataSetChanged()
                    Toast.makeText(this@RecursosActivity, R.string.error_api, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun mostrarDetalleRecurso(recurso: Recurso) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_detalle_recurso, null)
        
        val img: ImageView = dialogView.findViewById(R.id.imgDetalle)
        val txtTipo: TextView = dialogView.findViewById(R.id.txtDetalleTipo)
        val txtTitulo: TextView = dialogView.findViewById(R.id.txtDetalleTitulo)
        val rating: RatingBar = dialogView.findViewById(R.id.ratingDetalle)
        val txtDesc: TextView = dialogView.findViewById(R.id.txtDetalleDesc)

        txtTitulo.text = if (recurso.title.isNullOrBlank()) getString(R.string.recurso_sin_titulo) else recurso.title
        txtDesc.text = if (recurso.description.isNullOrBlank()) getString(R.string.recurso_sin_descripcion) else recurso.description
        txtTipo.text = if (recurso.type.isNullOrBlank()) getString(R.string.material_generico) else recurso.type.uppercase()
        rating.rating = recurso.rating

        Glide.with(this)
            .load(recurso.image)
            .placeholder(R.drawable.ic_placeholder_resource)
            .error(R.drawable.ic_placeholder_resource)
            .into(img)

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton(R.string.action_view_resource) { _, _ ->
                try {
                    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(recurso.link))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, R.string.error_link_invalid, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.action_close, null)
            .show()
    }

    private fun mostrarOpcionesDocente(recurso: Recurso) {
        val opciones = arrayOf(getString(R.string.opcion_editar), getString(R.string.opcion_eliminar))
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(recurso.title)
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> abrirEditarRecurso(recurso)
                    1 -> confirmarEliminacion(recurso)
                }
            }
            .show()
    }

    private fun abrirEditarRecurso(recurso: Recurso) {
        val intent = Intent(this, FormularioRecursoActivity::class.java).apply {
            putExtra(EDIT_MODE, true)
            putExtra(RECURSO_ID, recurso.id)
            putExtra(RECURSO_TITULO, recurso.title)
            putExtra(RECURSO_DESC, recurso.description)
            putExtra(RECURSO_TIPO, recurso.type)
            putExtra(RECURSO_LINK, recurso.link)
            putExtra(RECURSO_IMAGEN, recurso.image)
            putExtra(RECURSO_RATING, recurso.rating)
            putExtra(RECURSO_FAV, recurso.isFavorite)
        }
        formularioLauncher.launch(intent)
    }

    private fun confirmarEliminacion(recurso: Recurso) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(R.string.eliminar)
            .setMessage(R.string.confirmar_eliminacion)
            .setPositiveButton(R.string.eliminar) { _, _ ->
                eliminarRecurso(recurso.id)
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }

    private fun eliminarRecurso(id: String) {
        indicadorCarga.visibility = View.VISIBLE
        controlador.eliminarRecurso(id, object : RecursoController.OperacionListener {
            override fun onExito() {
                runOnUiThread {
                    Toast.makeText(this@RecursosActivity, R.string.recurso_eliminado, Toast.LENGTH_SHORT).show()
                    cargarRecursos()
                }
            }

            override fun onError() {
                runOnUiThread {
                    indicadorCarga.visibility = View.GONE
                    Toast.makeText(this@RecursosActivity, R.string.error_api, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun cargarRecursos() {
        indicadorCarga.visibility = View.VISIBLE
        txtEstado.visibility = View.GONE
        controlador.cargarRecursos()
    }

    override fun onRecursosCargados(recursos: List<Recurso>) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@RecursosActivity)
            val dao = db.recursoDao()
            
            // Mapear favoritos de forma eficiente
            var listaProcesada = recursos.map { recurso ->
                recurso.copy(isFavorite = dao.esFavorito(recurso.id))
            }

            // Aplicar filtro de favoritos si se solicitó
            if (soloFavoritos) {
                listaProcesada = listaProcesada.filter { it.isFavorite }
            }

            runOnUiThread {
                indicadorCarga.visibility = View.GONE
                listaOriginal = listaProcesada
                
                val listaParaMostrar = listaProcesada.toMutableList()
                aplicarOrden(listaParaMostrar)
                adaptador.actualizarLista(listaParaMostrar)

                actualizarEstadoVacio(listaParaMostrar.isEmpty())
            }
        }
    }

    override fun onErrorApi() {
        indicadorCarga.visibility = View.GONE
        txtEstado.text = getString(R.string.error_cargar_recursos)
        txtEstado.visibility = View.VISIBLE
    }
}
