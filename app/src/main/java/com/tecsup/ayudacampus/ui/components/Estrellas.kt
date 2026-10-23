package com.tecsup.ayudacampus.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.ui.theme.Ambar

/** Calificacion de 1 a 5 estrellas. Si onCambio es null, solo se muestra (no se puede tocar). */
@Composable
fun Estrellas(
    valor: Int,
    modifier: Modifier = Modifier,
    onCambio: ((Int) -> Unit)? = null,
    tamano: Dp = 36.dp
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            val llena = i <= valor
            Icon(
                imageVector = if (llena) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = "$i estrellas",
                tint = if (llena) Ambar else MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .size(tamano)
                    .then(if (onCambio != null) Modifier.clickable { onCambio(i) } else Modifier)
            )
        }
    }
}
