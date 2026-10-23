package com.tecsup.ayudacampus.model

/** Ciclo de vida de un ticket. En la tabla se guarda como texto (EstadoTicket.name). */
enum class EstadoTicket(val etiqueta: String) {
    ABIERTO("Abierto"),
    EN_PROCESO("En proceso"),
    RESUELTO("Resuelto"),
    CERRADO("Cerrado")
}
