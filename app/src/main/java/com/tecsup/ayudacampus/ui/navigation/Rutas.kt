package com.tecsup.ayudacampus.ui.navigation

/** Todas las rutas de la app en un solo lugar. */
object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTRO = "registro"

    // Grafo del estudiante
    const val GRAFO_CLIENTE = "grafo_cliente"
    const val MIS_TICKETS = "mis_tickets"
    const val NUEVO_TICKET = "nuevo_ticket"
    const val DETALLE = "detalle/{ticketId}"
    const val PERFIL = "perfil"

    // Grafo de soporte
    const val GRAFO_SOPORTE = "grafo_soporte"
    const val PANEL = "panel"
    const val BANDEJA = "bandeja"
    const val ATENDER = "atender/{ticketId}"
    const val CATEGORIAS = "categorias"
    const val FORM_CATEGORIA = "form_categoria?id={id}"
    const val AJUSTES = "ajustes"

    fun detalle(ticketId: Int) = "detalle/$ticketId"
    fun atender(ticketId: Int) = "atender/$ticketId"
    fun formCategoria(id: Int = 0) = "form_categoria?id=$id"
}
