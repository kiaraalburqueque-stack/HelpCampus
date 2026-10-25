package com.tecsup.ayudacampus.viewmodel

import com.tecsup.ayudacampus.data.local.entity.Ticket
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Que tan cerca esta un ticket de vencer su tiempo de atencion (SLA). */
enum class NivelSla { EN_TIEMPO, POR_VENCER, VENCIDO, CUMPLIDO, INCUMPLIDO }

data class EstadoSla(
    val texto: String,
    val progreso: Float,     // 0f = recien creado, 1f = se acabo el tiempo
    val nivel: NivelSla
)

private const val HORA = 3_600_000L

/** Regla de negocio del SLA: Alta 4 h, Media 24 h, Baja 72 h. */
fun horasSla(prioridad: Prioridad): Int = when (prioridad) {
    Prioridad.ALTA -> 4
    Prioridad.MEDIA -> 24
    Prioridad.BAJA -> 72
}

/** Calcula el estado del SLA de un ticket en el momento 'ahora'. Funcion pura: facil de probar. */
fun calcularSla(t: Ticket, ahora: Long): EstadoSla {
    val plazo = horasSla(Prioridad.valueOf(t.prioridad)) * HORA
    val limite = t.fecha + plazo

    return when (EstadoTicket.valueOf(t.estado)) {
        EstadoTicket.CERRADO -> {
            val tardo = (t.fechaCierre ?: ahora) - t.fecha
            if (tardo <= plazo) {
                EstadoSla("Resuelto dentro del SLA (${duracionCorta(tardo)})", 1f, NivelSla.CUMPLIDO)
            } else {
                EstadoSla("Resuelto fuera del SLA (${duracionCorta(tardo)})", 1f, NivelSla.INCUMPLIDO)
            }
        }

        EstadoTicket.RESUELTO ->
            EstadoSla("Resuelto · esperando confirmación", 1f, NivelSla.CUMPLIDO)

        else -> {
            val restante = limite - ahora
            val progreso = ((ahora - t.fecha).toFloat() / plazo).coerceIn(0f, 1f)
            when {
                restante <= 0 -> EstadoSla("SLA vencido hace ${duracionCorta(-restante)}", 1f, NivelSla.VENCIDO)
                progreso >= 0.75f -> EstadoSla("¡Quedan ${duracionCorta(restante)}!", progreso, NivelSla.POR_VENCER)
                else -> EstadoSla("Quedan ${duracionCorta(restante)}", progreso, NivelSla.EN_TIEMPO)
            }
        }
    }
}

/** Un "reloj" que emite la hora actual cada minuto: hace que el SLA se actualice en vivo. */
fun relojPorMinuto(): Flow<Long> = flow {
    while (true) {
        emit(System.currentTimeMillis())
        delay(60_000)
    }
}
