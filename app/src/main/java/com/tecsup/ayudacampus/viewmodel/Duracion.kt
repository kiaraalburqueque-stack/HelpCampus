package com.tecsup.ayudacampus.viewmodel

/** Convierte millis a un texto corto: "45 min", "3 h 20 min", "1 d 4 h". */
fun duracionCorta(millis: Long): String {
    val minutos = (millis / 60_000).coerceAtLeast(0)
    val dias = minutos / (24 * 60)
    val horas = (minutos % (24 * 60)) / 60
    val mins = minutos % 60
    return when {
        dias > 0 -> if (horas > 0) "$dias d $horas h" else "$dias d"
        horas > 0 -> if (mins > 0) "$horas h $mins min" else "$horas h"
        else -> "$mins min"
    }
}
