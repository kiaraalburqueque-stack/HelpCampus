package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** Bandeja de Soporte: ve TODOS los tickets, ya ordenados por prioridad desde el DAO. */
class BandejaViewModel(repo: AyudaRepository) : ViewModel() {

    private val _filtro = MutableStateFlow(FiltroEstado.TODOS)
    val filtro: StateFlow<FiltroEstado> = _filtro.asStateFlow()

    // filter() conserva el orden de la lista, asi que se mantiene ALTA -> MEDIA -> BAJA.
    val tickets: StateFlow<List<TicketConDetalle>> =
        combine(repo.observarTodosLosTickets(), _filtro) { lista, filtro ->
            lista.filter { filtro.acepta(it) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Fase 2: SLA de cada ticket de la bandeja (id -> estado), recalculado cada minuto. */
    val slas: StateFlow<Map<Int, EstadoSla>> =
        combine(tickets, relojPorMinuto()) { lista, ahora ->
            lista.associate { it.ticket.id to calcularSla(it.ticket, ahora) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun cambiarFiltro(f: FiltroEstado) {
        _filtro.value = f
    }
}
