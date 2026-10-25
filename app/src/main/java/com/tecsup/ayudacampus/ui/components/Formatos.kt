package com.tecsup.ayudacampus.ui.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** 12 -> "#0012" */
fun numeroTicket(id: Int): String = "#%04d".format(id)

/** Convierte una fecha en millis a "Hace 2 h", "Ayer", "Hace 3 d"... */
fun tiempoRelativo(millis: Long, ahora: Long = System.currentTimeMillis()): String {
    val minutos = (ahora - millis) / 60_000
    return when {
        minutos < 1 -> "Ahora"
        minutos < 60 -> "Hace $minutos min"
        minutos < 24 * 60 -> "Hace ${minutos / 60} h"
        minutos < 48 * 60 -> "Ayer"
        else -> "Hace ${minutos / (24 * 60)} d"
    }
}

/** Hora corta "10:20" si es de hoy; si no, el tiempo relativo. */
fun horaComentario(millis: Long, ahora: Long = System.currentTimeMillis()): String =
    if (ahora - millis < 24 * 3_600_000L) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(millis))
    } else {
        tiempoRelativo(millis, ahora).lowercase()
    }

/** "Kiara Alburqueque" -> "Kiara A." */
fun nombreCorto(nombre: String): String {
    val partes = nombre.trim().split(" ").filter { it.isNotBlank() }
    return when {
        partes.isEmpty() -> ""
        partes.size == 1 -> partes[0]
        else -> "${partes[0]} ${partes[1].first()}."
    }
}
