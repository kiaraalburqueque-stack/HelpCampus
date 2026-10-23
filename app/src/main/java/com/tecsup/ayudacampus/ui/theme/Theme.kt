package com.tecsup.ayudacampus.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tecsup.ayudacampus.model.Rol

private val ClienteClaro = lightColorScheme(
    primary = CianPrimario,
    onPrimary = Color.White,
    primaryContainer = CianContenedor,
    onPrimaryContainer = Color(0xFF164E63),
    secondaryContainer = VioletaContenedor,
    onSecondaryContainer = Color(0xFF4C1D95),
    background = CianFondo,
    onBackground = TextoOscuro,
    surface = Color.White,
    onSurface = TextoOscuro,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextoSecundario,
    outline = Color(0xFFE2E8F0),
    error = ColorAlta
)

private val SoporteClaro = lightColorScheme(
    primary = VioletaPrimario,
    onPrimary = Color.White,
    primaryContainer = VioletaContenedor,
    onPrimaryContainer = Color(0xFF4C1D95),
    secondaryContainer = CianContenedor,
    onSecondaryContainer = Color(0xFF164E63),
    background = VioletaFondo,
    onBackground = TextoOscuro,
    surface = Color.White,
    onSurface = TextoOscuro,
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = TextoSecundario,
    outline = Color(0xFFE5E7EB),
    error = ColorAlta
)

private val ClienteOscuro = darkColorScheme(
    primary = Color(0xFF22D3EE),
    onPrimary = Color(0xFF083344),
    primaryContainer = Color(0xFF155E75),
    onPrimaryContainer = CianContenedor,
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = VioletaContenedor,
    background = Color(0xFF0B1220),
    onBackground = Color(0xFFE5E7EB),
    surface = Color(0xFF162032),
    onSurface = Color(0xFFE5E7EB),
    surfaceVariant = Color(0xFF1F2A3D),
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = Color(0xFF334155),
    error = Color(0xFFF87171)
)

private val SoporteOscuro = darkColorScheme(
    primary = Color(0xFFA78BFA),
    onPrimary = Color(0xFF2E1065),
    primaryContainer = Color(0xFF5B21B6),
    onPrimaryContainer = VioletaContenedor,
    secondaryContainer = Color(0xFF155E75),
    onSecondaryContainer = CianContenedor,
    background = Color(0xFF110D1F),
    onBackground = Color(0xFFE5E7EB),
    surface = Color(0xFF1C1730),
    onSurface = Color(0xFFE5E7EB),
    surfaceVariant = Color(0xFF2A2342),
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = Color(0xFF3F3A56),
    error = Color(0xFFF87171)
)

/** Devuelve la paleta segun el perfil (cian = estudiante, violeta = soporte) y el modo oscuro. */
fun esquemaPara(rol: Rol, oscuro: Boolean): ColorScheme = when (rol) {
    Rol.CLIENTE -> if (oscuro) ClienteOscuro else ClienteClaro
    Rol.ADMIN -> if (oscuro) SoporteOscuro else SoporteClaro
}

/**
 * Tema de la app. Las pantallas NUNCA usan colores fijos para el primario:
 * usan MaterialTheme.colorScheme.primary y este tema decide cian o violeta.
 */
@Composable
fun AyudaTheme(
    rol: Rol = Rol.CLIENTE,
    oscuro: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = esquemaPara(rol, oscuro),
        typography = AyudaTypography,
        content = content
    )
}
