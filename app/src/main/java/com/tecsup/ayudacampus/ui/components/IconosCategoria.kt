package com.tecsup.ayudacampus.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.ui.graphics.vector.ImageVector

/** Iconos permitidos para una categoria (se guardan como texto en la tabla). */
val ICONOS_CATEGORIA = listOf("pc", "code", "wifi", "key", "printer", "tag")

/** Traduce el texto guardado en la tabla al icono de Material. */
fun iconoCategoria(icono: String): ImageVector = when (icono) {
    "pc" -> Icons.Outlined.Computer
    "code" -> Icons.Outlined.Code
    "wifi" -> Icons.Outlined.Wifi
    "key" -> Icons.Outlined.VpnKey
    "printer" -> Icons.Outlined.Print
    else -> Icons.Outlined.LocalOffer
}
