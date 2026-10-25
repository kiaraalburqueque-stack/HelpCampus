package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.EstadoTicket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Contadores para el saludo y para el perfil del estudiante. */
data class ResumenTickets(
    val abiertos: Int = 0,     // aun sin resolver: ABIERTO + EN_PROCESO
    val resueltos: Int = 0,    // RESUELTO + CERRADO
    val total: Int = 0
)

class MisTicketsViewModel(
    repo: AyudaRepository,
    clienteId: Int
) : ViewModel() {

    // Regla de visibilidad: el estudiante solo ve SUS tickets.
    private val misTickets = repo.observarTicketsCliente(clienteId)

    private val _filtro = MutableStateFlow(FiltroEstado.TODOS)
    val filtro: StateFlow<FiltroEstado> = _filtro.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    /** Combina los 3 flujos: si cambia la lista, el chip o el texto, se recalcula. */
    val tickets: StateFlow<List<TicketConDetalle>> =
        combine(misTickets, _filtro, _busqueda) { lista, filtro, texto ->
            lista.filter { filtro.acepta(it) && coincideBusqueda(it, texto) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Fase 2: SLA de cada ticket visible (id -> estado), recalculado cada minuto. */
    val slas: StateFlow<Map<Int, EstadoSla>> =
        combine(tickets, relojPorMinuto()) { lista, ahora ->
            lista.associate { it.ticket.id to calcularSla(it.ticket, ahora) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val resumen: StateFlow<ResumenTickets> = misTickets.map { lista ->
        ResumenTickets(
            abiertos = lista.count {
                it.ticket.estado == EstadoTicket.ABIERTO.name || it.ticket.estado == EstadoTicket.EN_PROCESO.name
            },
            resueltos = lista.count {
                it.ticket.estado == EstadoTicket.RESUELTO.name || it.ticket.estado == EstadoTicket.CERRADO.name
            },
            total = lista.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResumenTickets())

    fun cambiarFiltro(f: FiltroEstado) {
        _filtro.value = f
    }

    fun cambiarBusqueda(texto: String) {
        _busqueda.value = texto
    }
}
