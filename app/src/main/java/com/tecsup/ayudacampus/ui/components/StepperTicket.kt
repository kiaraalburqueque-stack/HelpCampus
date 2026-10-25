package com.tecsup.ayudacampus.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.ui.theme.ColorResuelto

/** Indicador de 4 pasos: Abierto -> En proceso -> Resuelto -> Cerrado. */
@Composable
fun StepperTicket(estado: EstadoTicket, modifier: Modifier = Modifier) {
    val pasos = EstadoTicket.entries
    val actual = estado.ordinal
    Row(modifier = modifier.fillMaxWidth()) {
        pasos.forEachIndexed { i, paso ->
            val hecho = i < actual || (i == actual && estado == EstadoTicket.CERRADO)
            val esActual = i == actual && !hecho
            // Cuando Soporte cambia el estado, el paso se "enciende" con una transicion de color.
            val colorLinea by animateColorAsState(
                if (i < actual) ColorResuelto else MaterialTheme.colorScheme.outline,
                tween(600),
                label = "linea"
            )
            Column(
                modifier = Modifier.width(64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val circulo = when {
                    hecho -> Modifier.background(ColorResuelto, CircleShape)
                    esActual -> Modifier
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(6.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                    else -> Modifier.border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                }
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .then(circulo),
                    contentAlignment = Alignment.Center
                ) {
                    if (hecho) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    paso.etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = if (i <= actual) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (i < pasos.lastIndex) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 12.dp)
                        .height(3.dp)
                        .background(colorLinea)
                )
            }
        }
    }
}
