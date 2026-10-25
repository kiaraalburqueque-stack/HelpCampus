package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.ui.components.EstadoVacio
import com.tecsup.ayudacampus.ui.components.FiltrosEstado
import com.tecsup.ayudacampus.ui.components.IndicadorSla
import com.tecsup.ayudacampus.ui.components.TicketCard
import com.tecsup.ayudacampus.viewmodel.MisTicketsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MisTicketsScreen(
    vm: MisTicketsViewModel,
    nombre: String,
    onAbrir: (Int) -> Unit,
    onNuevo: () -> Unit
) {
    val tickets by vm.tickets.collectAsState()
    val slas by vm.slas.collectAsState()
    val resumen by vm.resumen.collectAsState()
    val filtro by vm.filtro.collectAsState()
    val busqueda by vm.busqueda.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(
                titulo = "Mis tickets",
                subtitulo = "Hola, ${nombre.substringBefore(" ")} · ${resumen.abiertos} abiertos",
                acciones = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones")
                    }
                }
            )
            AyudaTextField(
                valor = busqueda,
                onCambio = vm::cambiarBusqueda,
                etiqueta = "Buscar por título o número...",
                icono = Icons.Outlined.Search,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(8.dp))
            FiltrosEstado(seleccionado = filtro, onCambio = vm::cambiarFiltro)
            Spacer(Modifier.height(8.dp))

            if (tickets.isEmpty()) {
                EstadoVacio(
                    mensaje = "No tienes tickets aquí",
                    detalle = "Prueba con otro filtro o crea un ticket nuevo."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(tickets, key = { it.ticket.id }) { t ->
                        val sla = slas[t.ticket.id]
                        TicketCard(
                            modifier = Modifier.animateItem(),
                            titulo = t.ticket.titulo,
                            numero = t.ticket.id,
                            detalle = t.ticket.ubicacion.ifBlank { t.categoria?.nombre ?: "" },
                            icono = t.categoria?.icono ?: "tag",
                            estado = EstadoTicket.valueOf(t.ticket.estado),
                            prioridad = Prioridad.valueOf(t.ticket.prioridad),
                            fecha = t.ticket.fecha,
                            pie = if (sla != null) { { IndicadorSla(sla) } } else null,
                        onClick = { onAbrir(t.ticket.id) }
                        )
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = onNuevo,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Nuevo ticket")
        }
    }
}
