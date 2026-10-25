package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.data.prefs.PreferenciasStore
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.Prioridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Todo el estado del formulario, incluidos los errores de cada campo. */
data class FormularioTicket(
    val categoriaId: Int? = null,
    val titulo: String = "",
    val ubicacion: String = "",
    val prioridad: Prioridad = Prioridad.MEDIA,      // prioridad por defecto
    val descripcion: String = "",
    val errorCategoria: String? = null,
    val errorTitulo: String? = null,
    val errorDescripcion: String? = null,
    val enviando: Boolean = false
)

class NuevoTicketViewModel(
    private val repo: AyudaRepository,
    prefs: PreferenciasStore,
    private val clienteId: Int
) : ViewModel() {

    /** Solo las categorias activas: las inactivas no aparecen en el formulario. */
    val categorias: StateFlow<List<Categoria>> = repo.observarCategoriasActivas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Si Soporte apago "Recibir tickets", la mesa de ayuda esta cerrada. */
    val recibirTickets: StateFlow<Boolean> = prefs.recibirTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private val _form = MutableStateFlow(FormularioTicket())
    val form: StateFlow<FormularioTicket> = _form.asStateFlow()

    // Al escribir en un campo se borra su error.
    fun elegirCategoria(id: Int) = _form.update { it.copy(categoriaId = id, errorCategoria = null) }
    fun cambiarTitulo(t: String) = _form.update { it.copy(titulo = t, errorTitulo = null) }
    fun cambiarUbicacion(u: String) = _form.update { it.copy(ubicacion = u) }
    fun cambiarPrioridad(p: Prioridad) = _form.update { it.copy(prioridad = p) }
    fun cambiarDescripcion(d: String) = _form.update { it.copy(descripcion = d, errorDescripcion = null) }

    /** Valida las reglas de negocio y, si todo esta bien, crea el ticket. */
    fun enviar(onEnviado: () -> Unit) {
        val f = _form.value
        if (f.enviando || !recibirTickets.value) return

        val categoriaValida = f.categoriaId != null && categorias.value.any { it.id == f.categoriaId }
        val errorCategoria = if (!categoriaValida) "Elige una categoría" else null
        val errorTitulo = if (f.titulo.trim().length < 5) "El título debe tener al menos 5 caracteres" else null
        val errorDescripcion =
            if (f.descripcion.trim().length < 10) "La descripción debe tener al menos 10 caracteres" else null

        if (errorCategoria != null || errorTitulo != null || errorDescripcion != null) {
            _form.value = f.copy(
                errorCategoria = errorCategoria,
                errorTitulo = errorTitulo,
                errorDescripcion = errorDescripcion
            )
            return
        }

        _form.value = f.copy(enviando = true)
        viewModelScope.launch {
            repo.crearTicket(
                clienteId = clienteId,
                categoriaId = f.categoriaId!!,
                titulo = f.titulo.trim(),
                descripcion = f.descripcion.trim(),
                ubicacion = f.ubicacion.trim(),
                prioridad = f.prioridad
            )
            _form.value = FormularioTicket()   // limpia el formulario
            onEnviado()
        }
    }
}
