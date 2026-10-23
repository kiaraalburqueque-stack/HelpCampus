package com.tecsup.ayudacampus.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Grafico de dona dibujado con Canvas: cada dato es un arco proporcional a su valor. */
@Composable
fun GraficoDona(
    datos: List<DatoGrafico>,
    modifier: Modifier = Modifier,
    grosor: Dp = 16.dp,
    textoCentro: String? = null,
    subtextoCentro: String? = null
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val trazo = grosor.toPx()
            val diametro = size.minDimension - trazo
            val esquina = Offset((size.width - diametro) / 2f, (size.height - diametro) / 2f)
            val tamano = Size(diametro, diametro)
            val total = datos.sumOf { it.valor.toDouble() }.toFloat()
            if (total <= 0f) {
                drawArc(
                    color = Color(0xFFE5E7EB),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = esquina,
                    size = tamano,
                    style = Stroke(width = trazo)
                )
            } else {
                var inicio = -90f
                datos.forEach { dato ->
                    val barrido = dato.valor / total * 360f
                    if (barrido > 0f) {
                        drawArc(
                            color = dato.color,
                            startAngle = inicio,
                            sweepAngle = barrido,
                            useCenter = false,
                            topLeft = esquina,
                            size = tamano,
                            style = Stroke(width = trazo)
                        )
                    }
                    inicio += barrido
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (textoCentro != null) {
                Text(textoCentro, style = MaterialTheme.typography.titleLarge)
            }
            if (subtextoCentro != null) {
                Text(
                    subtextoCentro,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
