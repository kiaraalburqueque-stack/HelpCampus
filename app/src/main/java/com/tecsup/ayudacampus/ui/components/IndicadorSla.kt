package com.tecsup.ayudacampus.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.ui.theme.ColorAbierto
import com.tecsup.ayudacampus.ui.theme.ColorAlta
import com.tecsup.ayudacampus.ui.theme.ColorResuelto
import com.tecsup.ayudacampus.viewmodel.EstadoSla
import com.tecsup.ayudacampus.viewmodel.NivelSla

fun colorSla(nivel: NivelSla): Color = when (nivel) {
    NivelSla.EN_TIEMPO -> ColorResuelto
    NivelSla.POR_VENCER -> ColorAbierto
    NivelSla.VENCIDO, NivelSla.INCUMPLIDO -> ColorAlta
    NivelSla.CUMPLIDO -> ColorResuelto
}

/**
 * Pie de la TicketCard con el SLA: icono, texto y una barra que se llena
 * con el tiempo y cambia de verde a ambar a rojo (con animacion).
 */
@Composable
fun IndicadorSla(sla: EstadoSla, modifier: Modifier = Modifier) {
    val color by animateColorAsState(colorSla(sla.nivel), tween(600), label = "colorSla")
    val progreso by animateFloatAsState(sla.progreso, tween(800), label = "progresoSla")
    val icono = when (sla.nivel) {
        NivelSla.CUMPLIDO -> Icons.Outlined.CheckCircle
        NivelSla.VENCIDO, NivelSla.INCUMPLIDO -> Icons.Outlined.Warning
        else -> Icons.Outlined.Timer
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(sla.texto, style = MaterialTheme.typography.labelSmall, color = color)
        }
        if (sla.nivel == NivelSla.EN_TIEMPO || sla.nivel == NivelSla.POR_VENCER) {
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progreso },
                color = color,
                trackColor = color.copy(alpha = 0.15f),
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
        }
    }
}
