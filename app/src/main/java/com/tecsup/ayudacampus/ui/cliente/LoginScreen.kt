package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.model.Rol
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.ui.theme.AyudaTheme
import com.tecsup.ayudacampus.ui.theme.ColorResuelto
import com.tecsup.ayudacampus.viewmodel.SesionViewModel

/**
 * Un solo Composable para ambos perfiles. Al cambiar la pestana cambia el tema
 * (cian o violeta), por eso el boton 'Ingresar' toma el color del rol elegido.
 */
@Composable
fun LoginScreen(
    vm: SesionViewModel,
    onIngreso: (Rol) -> Unit,
    onRegistrarse: () -> Unit = {},
    correoRegistrado: String? = null,
    onCorreoUsado: () -> Unit = {}
) {
    var rol by rememberSaveable { mutableStateOf(Rol.CLIENTE) }
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var cuentaCreada by rememberSaveable { mutableStateOf(false) }

    val usuario by vm.usuarioActual.collectAsState()
    val error by vm.error.collectAsState()
    val cargando by vm.cargando.collectAsState()
    val oscuro by vm.modoOscuro.collectAsState()

    // Si venimos del registro: pestana Estudiante, correo ya escrito y aviso de cuenta creada.
    LaunchedEffect(correoRegistrado) {
        if (correoRegistrado != null) {
            rol = Rol.CLIENTE
            correo = correoRegistrado
            clave = ""
            cuentaCreada = true
            vm.limpiarError()
            onCorreoUsado()
        }
    }

    // Cuando el ViewModel confirma el usuario, navegamos a su pantalla principal.
    LaunchedEffect(usuario) {
        usuario?.let { onIngreso(Rol.valueOf(it.rol)) }
    }

    AyudaTheme(rol = rol, oscuro = oscuro) {
        Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Spacer(Modifier.height(24.dp))
                Text("Bienvenido", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Ingresa para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(24.dp))

                if (cuentaCreada) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ColorResuelto.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = ColorResuelto)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Cuenta creada. Ahora inicia sesión con tu correo y contraseña.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }

                SelectorRol(
                    seleccionado = rol,
                    onCambio = {
                        rol = it
                        cuentaCreada = false
                        vm.limpiarError()
                    }
                )
                Spacer(Modifier.height(20.dp))

                AyudaTextField(
                    valor = correo,
                    onCambio = { correo = it; vm.limpiarError() },
                    etiqueta = "Correo",
                    icono = Icons.Outlined.Email,
                    teclado = KeyboardType.Email
                )
                Spacer(Modifier.height(12.dp))
                AyudaTextField(
                    valor = clave,
                    onCambio = { clave = it; vm.limpiarError() },
                    etiqueta = "Contraseña",
                    icono = Icons.Outlined.Lock,
                    esClave = true
                )
                Text(
                    "¿Olvidaste tu contraseña?",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                )

                if (error != null) {
                    Text(
                        error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                AyudaButton(
                    texto = if (cargando) "Ingresando..." else "Ingresar",
                    onClick = { vm.login(correo, clave, rol) },
                    enabled = !cargando
                )
                Spacer(Modifier.height(20.dp))

                if (rol == Rol.CLIENTE) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("¿No tienes cuenta? ", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Regístrate",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable(onClick = onRegistrarse)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            "Modo práctica",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Puedes cambiar de perfil desde Ajustes para probar ambos roles.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/** Pestanas Estudiante / Soporte TI. */
@Composable
private fun SelectorRol(seleccionado: Rol, onCambio: (Rol) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        Rol.entries.forEach { opcion ->
            val activo = opcion == seleccionado
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onCambio(opcion) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    opcion.etiqueta,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (activo) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
