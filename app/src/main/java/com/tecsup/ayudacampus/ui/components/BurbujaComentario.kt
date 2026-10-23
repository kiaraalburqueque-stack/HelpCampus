package com.tecsup.ayudacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Un mensaje de la conversacion.
 * Mensajes propios: a la derecha con el color primario.
 * Mensajes del otro perfil: a la izquierda con el color del otro perfil (secondaryContainer).
 */
@Composable
fun BurbujaComentario(
    autor: String,
    hora: String,
    texto: String,
    esPropio: Boolean,
    modifier: Modifier = Modifier
) {
    val fondo = if (esPropio) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
    val colorTexto = if (esPropio) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
    val forma = RoundedCornerShape(
        topStart = 14.dp,
        topEnd = 14.dp,
        bottomStart = if (esPropio) 14.dp else 4.dp,
        bottomEnd = if (esPropio) 4.dp else 14.dp
    )
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (esPropio) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(fondo, forma)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                "$autor · $hora",
                style = MaterialTheme.typography.labelSmall,
                color = colorTexto.copy(alpha = 0.75f)
            )
            Spacer(Modifier.height(2.dp))
            Text(texto, style = MaterialTheme.typography.bodyMedium, color = colorTexto)
        }
    }
}
