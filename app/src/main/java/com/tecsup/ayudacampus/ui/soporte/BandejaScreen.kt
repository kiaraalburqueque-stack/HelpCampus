package com.tecsup.ayudacampus.ui.soporte

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.EstadoVacio
import com.tecsup.ayudacampus.ui.components.FiltrosEstado
import com.tecsup.ayudacampus.ui.components.IndicadorSla
import com.tecsup.ayudacampus.ui.components.TicketCard
import com.tecsup.ayudacampus.ui.components.nombreCorto
import com.tecsup.ayudacampus.viewmodel.BandejaViewModel

@Composable
fun BandejaScreen(vm: BandejaViewModel, onAtender: (Int) -> Unit) {
    val tickets by vm.tickets.collectAsState()
    val slas by vm.slas.collectAsState()
    val filtro by vm.filtro.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(titulo = "Tickets", subtitulo = "${tickets.size} tickets · prioridad Alta primero")
        FiltrosEstado(seleccionado = filtro, onCambio = vm::cambiarFiltro)
        Spacer(Modifier.height(8.dp))

        if (tickets.isEmpty()) {
            EstadoVacio(mensaje = "No hay tickets con este filtro")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tickets, key = { it.ticket.id }) { t ->
                    val sla = slas[t.ticket.id]
                    TicketCard(
                        titulo = t.ticket.titulo,
                        numero = t.ticket.id,
                        detalle = nombreCorto(t.cliente?.nombre ?: "Estudiante"),
                        icono = t.categoria?.icono ?: "tag",
                        estado = EstadoTicket.valueOf(t.ticket.estado),
                        prioridad = Prioridad.valueOf(t.ticket.prioridad),
                        fecha = t.ticket.fecha,
                        pie = if (sla != null) { { IndicadorSla(sla) } } else null,
                        onClick = { onAtender(t.ticket.id) }
                    )
                }
            }
        }
    }
}
