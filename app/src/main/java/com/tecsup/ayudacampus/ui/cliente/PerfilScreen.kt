package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.model.Rol
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.BotonCambiarPerfil
import com.tecsup.ayudacampus.ui.components.Contador
import com.tecsup.ayudacampus.ui.components.EstiloBoton
import com.tecsup.ayudacampus.ui.components.NotaDataStore
import com.tecsup.ayudacampus.ui.components.OpcionSwitch
import com.tecsup.ayudacampus.ui.components.TarjetaPreferencias
import com.tecsup.ayudacampus.ui.components.TarjetaUsuario
import com.tecsup.ayudacampus.ui.theme.ColorAbierto
import com.tecsup.ayudacampus.ui.theme.ColorResuelto
import com.tecsup.ayudacampus.viewmodel.MisTicketsViewModel
import com.tecsup.ayudacampus.viewmodel.SesionViewModel

@Composable
fun PerfilScreen(
    sesionVm: SesionViewModel,
    ticketsVm: MisTicketsViewModel,
    onCambiarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val usuario by sesionVm.usuarioActual.collectAsState()
    val oscuro by sesionVm.modoOscuro.collectAsState()
    val notificaciones by sesionVm.notificaciones.collectAsState()
    val resumen by ticketsVm.resumen.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AppTopBar(titulo = "Perfil")
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TarjetaUsuario(
                nombre = usuario?.nombre ?: "",
                correo = usuario?.correo ?: "",
                etiqueta = Rol.CLIENTE.etiqueta
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Contador(resumen.abiertos, "Abiertos", ColorAbierto, Modifier.weight(1f))
                Contador(resumen.resueltos, "Resueltos", ColorResuelto, Modifier.weight(1f))
                Contador(resumen.total, "Total", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            }
            BotonCambiarPerfil(destino = Rol.ADMIN.etiqueta, onClick = onCambiarPerfil)

            Text("Preferencias", style = MaterialTheme.typography.titleSmall)
            TarjetaPreferencias(
                listOf(
                    OpcionSwitch("Modo oscuro", "Cambia el tema de la app", oscuro, sesionVm::guardarModoOscuro),
                    OpcionSwitch(
                        "Notificaciones", "Avisar cambios de estado", notificaciones,
                        sesionVm::guardarNotificaciones
                    )
                )
            )
            NotaDataStore()
            AyudaButton(texto = "Cerrar sesión", onClick = onCerrarSesion, estilo = EstiloBoton.PELIGRO)
            Spacer(Modifier.height(16.dp))
        }
    }
}
