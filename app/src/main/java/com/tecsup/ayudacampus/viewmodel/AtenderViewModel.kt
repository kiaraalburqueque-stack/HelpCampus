package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Soporte atiende un ticket. El estado y la prioridad elegidos se guardan
 * en memoria y solo se escriben en Room al tocar 'Guardar cambios'.
 */
class AtenderViewModel(
    private val repo: AyudaRepository,
    private val ticketId: Int,
    private val usuario: Usuario
) : ViewModel() {

    val ticket: StateFlow<TicketConDetalle?> = repo.observarTicket(ticketId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _estadoElegido = MutableStateFlow<EstadoTicket?>(null)
    private val _prioridadElegida = MutableStateFlow<Prioridad?>(null)

    /** Lo elegido en pantalla o, si aun no se eligio nada, lo que dice la base. */
    val estadoElegido: StateFlow<EstadoTicket?> = combine(ticket, _estadoElegido) { t, elegido ->
        elegido ?: t?.let { EstadoTicket.valueOf(it.ticket.estado) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val prioridadElegida: StateFlow<Prioridad?> = combine(ticket, _prioridadElegida) { t, elegida ->
        elegida ?: t?.let { Prioridad.valueOf(it.ticket.prioridad) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private fun estaCerrado(): Boolean = ticket.value?.ticket?.estado == EstadoTicket.CERRADO.name

    /** Regla: Soporte solo puede elegir ABIERTO, EN_PROCESO o RESUELTO. Nunca CERRADO. */
    fun cambiarEstado(estado: EstadoTicket) {
        when {
            estaCerrado() -> _mensaje.value = "Un ticket cerrado ya no se puede modificar"
            estado == EstadoTicket.CERRADO -> _mensaje.value = "Solo el estudiante puede cerrar el ticket"
            else -> {
                _estadoElegido.value = estado
                _mensaje.value = null
            }
        }
    }

    fun cambiarPrioridad(prioridad: Prioridad) {
        if (estaCerrado()) {
            _mensaje.value = "Un ticket cerrado ya no se puede modificar"
            return
        }
        _prioridadElegida.value = prioridad
    }

    /** Responde al estudiante (no se permite texto vacio ni ticket cerrado). */
    fun responder(texto: String) {
        if (texto.isBlank() || ticket.value == null || estaCerrado()) return
        viewModelScope.launch { repo.agregarComentario(ticketId, usuario, texto.trim()) }
    }

    /** Escribe en Room solo lo que realmente cambio. */
    fun guardar(onGuardado: () -> Unit) {
        val actual = ticket.value ?: return
        if (estaCerrado()) return
        val nuevoEstado = _estadoElegido.value
        val nuevaPrioridad = _prioridadElegida.value
        viewModelScope.launch {
            if (nuevoEstado != null && nuevoEstado.name != actual.ticket.estado) {
                repo.cambiarEstado(ticketId, nuevoEstado)
            }
            if (nuevaPrioridad != null && nuevaPrioridad.name != actual.ticket.prioridad) {
                repo.cambiarPrioridad(ticketId, nuevaPrioridad)
            }
            _estadoElegido.value = null
            _prioridadElegida.value = null
            onGuardado()
        }
    }
}
