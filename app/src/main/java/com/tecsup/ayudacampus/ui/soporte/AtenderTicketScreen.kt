package com.tecsup.ayudacampus.ui.soporte

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.ui.cliente.CabeceraTicket
import com.tecsup.ayudacampus.ui.cliente.SelectorPrioridad
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.BarraComentario
import com.tecsup.ayudacampus.ui.components.BurbujaComentario
import com.tecsup.ayudacampus.ui.components.horaComentario
import com.tecsup.ayudacampus.ui.components.numeroTicket
import com.tecsup.ayudacampus.ui.theme.colorEstado
import com.tecsup.ayudacampus.viewmodel.AtenderViewModel

@Composable
fun AtenderTicketScreen(
    vm: AtenderViewModel,
    usuarioId: Int,
    onBack: () -> Unit
) {
    val detalle by vm.ticket.collectAsState()
    val estadoElegido by vm.estadoElegido.collectAsState()
    val prioridadElegida by vm.prioridadElegida.collectAsState()
    val mensaje by vm.mensaje.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            titulo = detalle?.let { "Atender ticket ${numeroTicket(it.ticket.id)}" } ?: "Atender ticket",
            onBack = onBack
        )
        val d = detalle
        if (d == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Column
        }
        val cerrado = d.ticket.estado == EstadoTicket.CERRADO.name

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { CabeceraTicket(d, mostrarCliente = true) }

            item { Text("Estado", style = MaterialTheme.typography.titleSmall) }
            item {
                SelectorEstado(
                    seleccionado = estadoElegido,
                    ticketCerrado = cerrado,
                    onCambio = vm::cambiarEstado
                )
            }

            item { Text("Prioridad", style = MaterialTheme.typography.titleSmall) }
            item {
                prioridadElegida?.let {
                    SelectorPrioridad(seleccionada = it, onCambio = vm::cambiarPrioridad, habilitado = !cerrado)
                }
            }

            mensaje?.let { texto ->
                item { Text(texto, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }

            item { Text("Conversación", style = MaterialTheme.typography.titleSmall) }
            items(d.comentarios.sortedBy { it.fecha }, key = { it.id }) { c ->
                val propio = c.autorId == usuarioId
                BurbujaComentario(
                    autor = if (propio) "Tú" else c.autorNombre,
                    hora = horaComentario(c.fecha),
                    texto = c.texto,
                    esPropio = propio
                )
            }
            if (cerrado) {
                item {
                    Text(
                        "Ticket cerrado por el estudiante con ${d.ticket.calificacion ?: 0} de 5 estrellas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (!cerrado) {
            BarraComentario(placeholder = "Responder al cliente...", onEnviar = vm::responder)
            AyudaButton(
                texto = "Guardar cambios",
                onClick = { vm.guardar(onGuardado = onBack) },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
            )
        }
    }
}

/** 4 opciones de estado. CERRADO aparece deshabilitada: Soporte no puede cerrar tickets. */
@Composable
private fun SelectorEstado(
    seleccionado: EstadoTicket?,
    ticketCerrado: Boolean,
    onCambio: (EstadoTicket) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
        EstadoTicket.entries.forEach { estado ->
            val activo = estado == seleccionado
            val habilitado = !ticketCerrado && estado != EstadoTicket.CERRADO
            val color = colorEstado(estado)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .alpha(if (habilitado || activo) 1f else 0.4f)
                    .background(if (activo) color else MaterialTheme.colorScheme.surface, RoundedCornerShape(50))
                    .border(
                        BorderStroke(1.dp, if (activo) color else MaterialTheme.colorScheme.outline),
                        RoundedCornerShape(50)
                    )
                    .clickable(enabled = habilitado) { onCambio(estado) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    estado.etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (activo) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
