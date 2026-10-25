package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriasViewModel(private val repo: AyudaRepository) : ViewModel() {

    val categorias: StateFlow<List<Categoria>> = repo.observarCategorias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Cantidad de tickets por categoria: id -> cantidad (para la lista y la vista previa). */
    val conteos: StateFlow<Map<Int, Int>> = repo.observarCantidadPorCategoria()
        .map { lista -> lista.associate { it.categoriaId to it.cantidad } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // ---------- Formulario (crear / editar) ----------
    private val _form = MutableStateFlow(Categoria(nombre = "", icono = "pc"))
    val form: StateFlow<Categoria> = _form.asStateFlow()

    private val _errorNombre = MutableStateFlow<String?>(null)
    val errorNombre: StateFlow<String?> = _errorNombre.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun alternar(c: Categoria) {
        viewModelScope.launch { repo.cambiarActiva(c.id, !c.activa) }
    }

    /** id = 0 -> categoria nueva; otro id -> se carga para editar. */
    fun cargar(id: Int) {
        if (id == 0) {
            _form.value = Categoria(nombre = "", icono = "pc")
            return
        }
        viewModelScope.launch {
            repo.obtenerCategoria(id)?.let { _form.value = it }
        }
    }

    fun cambiarNombre(nombre: String) {
        _form.update { it.copy(nombre = nombre) }
        _errorNombre.value = null
    }

    fun cambiarIcono(icono: String) = _form.update { it.copy(icono = icono) }

    fun cambiarActiva(activa: Boolean) = _form.update { it.copy(activa = activa) }

    /** Guarda una categoria nueva o editada. El nombre es obligatorio. */
    fun guardar(categoria: Categoria, onListo: () -> Unit = {}) {
        val nombre = categoria.nombre.trim()
        if (nombre.isEmpty()) {
            _errorNombre.value = "El nombre es obligatorio"
            return
        }
        viewModelScope.launch {
            repo.guardarCategoria(categoria.copy(nombre = nombre))
            onListo()
        }
    }

    /** Regla: no se puede eliminar una categoria que ya tiene tickets. */
    fun eliminar(categoria: Categoria, onListo: () -> Unit = {}) {
        viewModelScope.launch {
            val cantidad = repo.contarTicketsDeCategoria(categoria.id)
            if (cantidad > 0) {
                _mensaje.value = "No se puede eliminar: tiene $cantidad ticket(s). Puedes desactivarla."
            } else {
                repo.eliminarCategoria(categoria)
                onListo()
            }
        }
    }
}
