package com.example.aprenderapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aprenderapp.R
import com.example.aprenderapp.model.Recurso
import com.example.aprenderapp.view.RecursosActivity

class RecursoAdapter(
    private var recursos: List<Recurso>,
    private val usuarioRol: String,
    private val alPresionarFavorito: (Recurso) -> Unit,
    private val alCambiarRating: (Recurso, Float) -> Unit,
    private val alSeleccionar: (Recurso) -> Unit
) : RecyclerView.Adapter<RecursoAdapter.RecursoViewHolder>() {

    class RecursoViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val imagen: ImageView = itemView.findViewById(R.id.imgRecurso)
        val titulo: TextView = itemView.findViewById(R.id.txtTitulo)
        val descripcion: TextView = itemView.findViewById(R.id.txtDescripcion)
        val tipo: TextView = itemView.findViewById(R.id.txtTipo)
        val enlace: TextView = itemView.findViewById(R.id.txtEnlace)
        val btnFavorito: ImageButton = itemView.findViewById(R.id.btnFavorito)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingRecurso)
        val btnEditar: com.google.android.material.button.MaterialButton = itemView.findViewById(R.id.btnEditarRecurso)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecursoViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recurso, parent, false)

        return RecursoViewHolder(vista)
    }

    override fun onBindViewHolder(holder: RecursoViewHolder, position: Int) {
        val recurso = recursos[position]

        val context = holder.itemView.context
        
        // Uso de strings.xml para evitar texto quemado
        holder.titulo.text = if (recurso.title.isNullOrBlank()) context.getString(R.string.recurso_sin_titulo) else recurso.title
        holder.descripcion.text = if (recurso.description.isNullOrBlank()) context.getString(R.string.recurso_sin_descripcion) else recurso.description
        holder.tipo.text = if (recurso.type.isNullOrBlank()) context.getString(R.string.material_generico) else recurso.type.uppercase()
        holder.enlace.text = context.getString(R.string.action_view_resource)

        val esEstudiante = usuarioRol.equals(RecursosActivity.ROL_ESTUDIANTE, ignoreCase = true)
        val esDocente = usuarioRol.equals(RecursosActivity.ROL_DOCENTE, ignoreCase = true)

        // Favoritos: Solo para Estudiantes
        if (esEstudiante) {
            holder.btnFavorito.visibility = View.VISIBLE
            val iconFav = if (recurso.isFavorite) {
                R.drawable.ic_heart_filled
            } else {
                R.drawable.ic_heart_border
            }
            holder.btnFavorito.setImageResource(iconFav)

            holder.btnFavorito.setOnClickListener {
                recurso.isFavorite = !recurso.isFavorite
                val newIcon = if (recurso.isFavorite) {
                    R.drawable.ic_heart_filled
                } else {
                    R.drawable.ic_heart_border
                }
                holder.btnFavorito.setImageResource(newIcon)
                alPresionarFavorito(recurso)
            }
        } else {
            holder.btnFavorito.visibility = View.GONE
        }

        // Calificación: El Estudiante puede calificar, el Docente solo ve el promedio
        holder.ratingBar.setOnRatingBarChangeListener(null)
        holder.ratingBar.rating = recurso.rating
        holder.ratingBar.setIsIndicator(!esEstudiante)
        
        holder.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser && esEstudiante) {
                recurso.rating = rating
                alCambiarRating(recurso, rating)
            }
        }

        // Ver contenido (Navegador)
        holder.enlace.setOnClickListener {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(recurso.link))
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, R.string.error_link_invalid, android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        // Botón Editar: Solo para Docentes
        if (esDocente) {
            holder.btnEditar.visibility = View.VISIBLE
            holder.btnEditar.setOnClickListener {
                alSeleccionar(recurso)
            }
        } else {
            holder.btnEditar.visibility = View.GONE
        }

        // Imagen con Glide
        com.bumptech.glide.Glide.with(context)
            .load(recurso.image)
            .placeholder(R.drawable.ic_placeholder_resource)
            .error(R.drawable.ic_placeholder_resource)
            .into(holder.imagen)

        // Clic en el item completo (para Docente muestra menú, para Estudiante info básica)
        holder.itemView.setOnClickListener {
            alSeleccionar(recurso)
        }
    }

    override fun getItemCount(): Int = recursos.size

    fun actualizarLista(nuevaLista: List<Recurso>) {
        recursos = nuevaLista
        notifyDataSetChanged()
    }

    fun obtenerLista(): List<Recurso> {
        return recursos
    }
}