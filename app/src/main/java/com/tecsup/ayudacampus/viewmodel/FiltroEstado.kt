package com.tecsup.ayudacampus.viewmodel

import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.model.EstadoTicket

/** Chips de filtro. 'Todos' (estado = null) incluye tambien los CERRADOS. */
enum class FiltroEstado(val etiqueta: String, val estado: EstadoTicket?) {
    TODOS("Todos", null),
    ABIERTOS("Abiertos", EstadoTicket.ABIERTO),
    EN_PROCESO("En proceso", EstadoTicket.EN_PROCESO),
    RESUELTOS("Resueltos", EstadoTicket.RESUELTO)
}

/** true si el ticket pasa el filtro de estado. */
fun FiltroEstado.acepta(t: TicketConDetalle): Boolean =
    estado == null || t.ticket.estado == estado.name

/** Busca por titulo o por numero ("12", "#0012" o "0012"). */
fun coincideBusqueda(t: TicketConDetalle, texto: String): Boolean {
    val q = texto.trim().removePrefix("#").lowercase()
    if (q.isEmpty()) return true
    val numero = t.ticket.id.toString().padStart(4, '0')
    return t.ticket.titulo.lowercase().contains(q) || numero.contains(q)
}
