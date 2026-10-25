package com.tecsup.ayudacampus.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Mensaje para listas vacias con una ilustracion dibujada con Canvas:
 * un circulo suave, puntos decorativos que "respiran" y el icono flotando.
 */
@Composable
fun EstadoVacio(
    mensaje: String,
    modifier: Modifier = Modifier,
    icono: ImageVector = Icons.Outlined.Inbox,
    detalle: String? = null
) {
    val transicion = rememberInfiniteTransition(label = "vacio")
    val flotar by transicion.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "flotar"
    )
    val brillo by transicion.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
        label = "brillo"
    )
    val fondo = MaterialTheme.colorScheme.primaryContainer
    val acento = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centro = Offset(size.width / 2f, size.height / 2f)
                drawCircle(color = fondo, radius = size.minDimension * 0.42f, center = centro)
                // puntitos alrededor (como confeti)
                val puntos = listOf(
                    Offset(0.12f, 0.25f) to 5f, Offset(0.88f, 0.20f) to 4f,
                    Offset(0.92f, 0.70f) to 6f, Offset(0.10f, 0.78f) to 4f, Offset(0.50f, 0.04f) to 3f
                )
                puntos.forEach { (p, r) ->
                    drawCircle(
                        color = acento.copy(alpha = brillo),
                        radius = r.dp.toPx(),
                        center = Offset(size.width * p.x, size.height * p.y)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .offset(y = flotar.dp)
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = acento, modifier = Modifier.size(34.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(mensaje, style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
        if (detalle != null) {
            Text(
                detalle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
