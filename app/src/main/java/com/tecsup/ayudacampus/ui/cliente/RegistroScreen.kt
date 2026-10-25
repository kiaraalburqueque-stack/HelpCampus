package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.viewmodel.RegistroViewModel

/** Crear cuenta de estudiante. La pantalla solo muestra el estado y avisa los eventos. */
@Composable
fun RegistroScreen(
    vm: RegistroViewModel,
    onBack: () -> Unit,
    onRegistrado: (Usuario) -> Unit
) {
    val form by vm.form.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(titulo = "Crear cuenta", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Únete a AyudaCampus", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Crea tu cuenta y luego inicia sesión para reportar incidencias y seguir tus tickets.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))

            AyudaTextField(
                valor = form.nombre,
                onCambio = vm::cambiarNombre,
                etiqueta = "Nombre completo",
                icono = Icons.Outlined.Person,
                error = form.errorNombre
            )
            AyudaTextField(
                valor = form.correo,
                onCambio = vm::cambiarCorreo,
                etiqueta = "Correo",
                icono = Icons.Outlined.Email,
                error = form.errorCorreo,
                teclado = KeyboardType.Email
            )
            AyudaTextField(
                valor = form.clave,
                onCambio = vm::cambiarClave,
                etiqueta = "Contraseña",
                icono = Icons.Outlined.Lock,
                error = form.errorClave,
                esClave = true
            )
            AyudaTextField(
                valor = form.confirmar,
                onCambio = vm::cambiarConfirmar,
                etiqueta = "Confirmar contraseña",
                icono = Icons.Outlined.Lock,
                error = form.errorConfirmar,
                esClave = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Las cuentas nuevas son de Estudiante. Las de Soporte TI las crea la mesa de ayuda.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(Modifier.height(4.dp))
            AyudaButton(
                texto = if (form.enviando) "Creando cuenta..." else "Crear cuenta",
                onClick = { vm.registrar(onRegistrado) },
                enabled = !form.enviando
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("¿Ya tienes cuenta? ", style = MaterialTheme.typography.bodySmall)
                Text(
                    "Inicia sesión",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = onBack)
                )
            }
        }
    }
}
