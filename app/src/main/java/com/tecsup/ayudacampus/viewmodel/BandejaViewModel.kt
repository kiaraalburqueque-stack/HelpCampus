package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.Prioridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/** Todos los criterios de busqueda juntos (Fase 2: busqueda avanzada). */
data class CriteriosBusqueda(
    val filtro: FiltroEstado = FiltroEstado.TODOS,
    val texto: String = "",
    val prioridades: Set<Prioridad> = emptySet(),   // vacio = todas
    val categoriaId: Int? = null                    // null = todas
) {
    /** Cuantos filtros avanzados hay activos (para el globito del boton). */
    val activos: Int
        get() = listOf(texto.isNotBlank(), prioridades.isNotEmpty(), categoriaId != null).count { it }

    fun acepta(t: TicketConDetalle): Boolean =
        filtro.acepta(t) &&
            (prioridades.isEmpty() || prioridades.any { it.name == t.ticket.prioridad }) &&
            (categoriaId == null || t.ticket.categoriaId == categoriaId) &&
            coincideTexto(t, texto)

    /** Busca en titulo, numero, nombre del cliente, ubicacion y descripcion. */
    private fun coincideTexto(t: TicketConDetalle, texto: String): Boolean {
        val q = texto.trim().lowercase()
        if (q.isEmpty()) return true
        return coincideBusqueda(t, q) ||
            (t.cliente?.nombre?.lowercase()?.contains(q) ?: false) ||
            t.ticket.ubicacion.lowercase().contains(q) ||
            t.ticket.descripcion.lowercase().contains(q)
    }
}

/** Bandeja de Soporte: ve TODOS los tickets, ya ordenados por prioridad desde el DAO. */
class BandejaViewModel(repo: AyudaRepository) : ViewModel() {

    private val _criterios = MutableStateFlow(CriteriosBusqueda())
    val criterios: StateFlow<CriteriosBusqueda> = _criterios.asStateFlow()

    /** Para los chips de categoria del filtro avanzado. */
    val categorias: StateFlow<List<Categoria>> = repo.observarCategorias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // filter() conserva el orden de la lista, asi que se mantiene ALTA -> MEDIA -> BAJA.
    val tickets: StateFlow<List<TicketConDetalle>> =
        combine(repo.observarTodosLosTickets(), _criterios) { lista, criterios ->
            lista.filter { criterios.acepta(it) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filtro: StateFlow<FiltroEstado> = _criterios.map { it.filtro }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FiltroEstado.TODOS)

    /** Fase 2: SLA de cada ticket de la bandeja (id -> estado), recalculado cada minuto. */
    val slas: StateFlow<Map<Int, EstadoSla>> =
        combine(tickets, relojPorMinuto()) { lista, ahora ->
            lista.associate { it.ticket.id to calcularSla(it.ticket, ahora) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun cambiarFiltro(f: FiltroEstado) = _criterios.update { it.copy(filtro = f) }

    fun cambiarTexto(texto: String) = _criterios.update { it.copy(texto = texto) }

    /** Las prioridades se pueden combinar: Alta + Media, por ejemplo. */
    fun alternarPrioridad(p: Prioridad) = _criterios.update {
        it.copy(prioridades = if (p in it.prioridades) it.prioridades - p else it.prioridades + p)
    }

    /** Tocar la categoria elegida otra vez la quita. */
    fun alternarCategoria(id: Int) = _criterios.update {
        it.copy(categoriaId = if (it.categoriaId == id) null else id)
    }

    fun limpiarFiltros() = _criterios.update { CriteriosBusqueda(filtro = it.filtro) }
}
