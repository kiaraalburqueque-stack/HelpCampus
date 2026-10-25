package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.EstadoTicket
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Detalle del ticket para el estudiante. Como 'ticket' es un Flow de Room,
 * si Soporte cambia el estado o responde, esta pantalla se actualiza sola.
 */
class TicketDetalleViewModel(
    private val repo: AyudaRepository,
    private val ticketId: Int,
    private val usuario: Usuario
) : ViewModel() {

    val ticket: StateFlow<TicketConDetalle?> = repo.observarTicket(ticketId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private fun estadoActual(): EstadoTicket? =
        ticket.value?.let { EstadoTicket.valueOf(it.ticket.estado) }

    /** Regla: no se comenta con texto vacio ni en un ticket CERRADO. */
    fun comentar(texto: String) {
        val estado = estadoActual() ?: return
        if (texto.isBlank() || estado == EstadoTicket.CERRADO) return
        viewModelScope.launch { repo.agregarComentario(ticketId, usuario, texto.trim()) }
    }

    /** Regla: solo se cierra si esta RESUELTO y con una calificacion de 1 a 5. */
    fun cerrar(calificacion: Int, comentarioFinal: String = "") {
        if (estadoActual() != EstadoTicket.RESUELTO) return
        if (calificacion !in 1..5) return
        viewModelScope.launch {
            if (comentarioFinal.isNotBlank()) {
                repo.agregarComentario(ticketId, usuario, comentarioFinal.trim())
            }
            repo.cerrarConCalificacion(ticketId, calificacion)
        }
    }

    /** Regla: solo se reabre si esta RESUELTO (vuelve a ABIERTO y se borra la calificacion). */
    fun reabrir() {
        if (estadoActual() != EstadoTicket.RESUELTO) return
        viewModelScope.launch { repo.reabrir(ticketId) }
    }
}
