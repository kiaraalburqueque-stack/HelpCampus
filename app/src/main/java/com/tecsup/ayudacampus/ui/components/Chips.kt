package com.tecsup.ayudacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.ui.theme.colorEstado
import com.tecsup.ayudacampus.ui.theme.colorPrioridad

/** Etiqueta pequena con fondo suave del mismo color que el texto. */
@Composable
fun EtiquetaColor(texto: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        color = color,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .background(color.copy(alpha = 0.14f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
fun EstadoChip(estado: EstadoTicket, modifier: Modifier = Modifier) {
    EtiquetaColor(estado.etiqueta, colorEstado(estado), modifier)
}

@Composable
fun PrioridadChip(prioridad: Prioridad, modifier: Modifier = Modifier) {
    EtiquetaColor(prioridad.etiqueta, colorPrioridad(prioridad), modifier)
}
