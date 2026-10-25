package com.tecsup.ayudacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Tarjeta de color con un numero grande y su etiqueta (panel y perfil). */
@Composable
fun Contador(valor: Int, etiqueta: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(color.copy(alpha = 0.13f), RoundedCornerShape(14.dp))
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            valor.toString(),
            color = color,
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 26.sp)
        )
        Text(etiqueta, color = color, style = MaterialTheme.typography.labelMedium)
    }
}
