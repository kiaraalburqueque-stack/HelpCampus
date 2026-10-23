package com.tecsup.ayudacampus.ui.components.graficos

import androidx.compose.ui.graphics.Color

/** Un dato para los graficos del laboratorio de Canvas: etiqueta, valor y color. */
data class DatoGrafico(
    val etiqueta: String,
    val valor: Float,
    val color: Color
)
