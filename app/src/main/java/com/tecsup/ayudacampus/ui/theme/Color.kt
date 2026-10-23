package com.tecsup.ayudacampus.ui.theme

import androidx.compose.ui.graphics.Color
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad

// Estudiante (cian)
val CianPrimario = Color(0xFF0891B2)
val CianContenedor = Color(0xFFCFFAFE)
val CianFondo = Color(0xFFF7FDFE)

// Soporte TI (violeta)
val VioletaPrimario = Color(0xFF7C3AED)
val VioletaContenedor = Color(0xFFEDE4FF)
val VioletaFondo = Color(0xFFFAF8FF)
val VioletaSuave = Color(0xFFC4B5FD)

// Estados del ticket
val ColorAbierto = Color(0xFFD97706)
val ColorEnProceso = Color(0xFF2563EB)
val ColorResuelto = Color(0xFF16A34A)
val ColorCerrado = Color(0xFF6B7280)

// Prioridades
val ColorAlta = Color(0xFFDC2626)
val ColorMedia = Color(0xFFB45309)
val ColorBaja = Color(0xFF6B7280)

// Otros
val Ambar = Color(0xFFF59E0B)
val TextoOscuro = Color(0xFF111827)
val TextoSecundario = Color(0xFF6B7280)

fun colorEstado(estado: EstadoTicket): Color = when (estado) {
    EstadoTicket.ABIERTO -> ColorAbierto
    EstadoTicket.EN_PROCESO -> ColorEnProceso
    EstadoTicket.RESUELTO -> ColorResuelto
    EstadoTicket.CERRADO -> ColorCerrado
}

fun colorPrioridad(prioridad: Prioridad): Color = when (prioridad) {
    Prioridad.ALTA -> ColorAlta
    Prioridad.MEDIA -> ColorMedia
    Prioridad.BAJA -> ColorBaja
}
