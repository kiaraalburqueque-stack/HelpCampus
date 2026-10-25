package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.ui.components.BarraComentario
import com.tecsup.ayudacampus.ui.components.BurbujaComentario
import com.tecsup.ayudacampus.ui.components.EstadoChip
import com.tecsup.ayudacampus.ui.components.Estrellas
import com.tecsup.ayudacampus.ui.components.EstiloBoton
import com.tecsup.ayudacampus.ui.components.PrioridadChip
import com.tecsup.ayudacampus.ui.components.StepperTicket
import com.tecsup.ayudacampus.ui.components.horaComentario
import com.tecsup.ayudacampus.ui.components.iconoCategoria
import com.tecsup.ayudacampus.ui.components.numeroTicket
import com.tecsup.ayudacampus.ui.components.tiempoRelativo
import com.tecsup.ayudacampus.ui.theme.ColorResuelto
import com.tecsup.ayudacampus.viewmodel.TicketDetalleViewModel

@Composable
fun DetalleTicketScreen(
    vm: TicketDetalleViewModel,
    usuarioId: Int,
    onBack: () -> Unit
) {
    val detalle by vm.ticket.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            titulo = detalle?.let { "Ticket ${numeroTicket(it.ticket.id)}" } ?: "Ticket",
            onBack = onBack
        )

        val d = detalle
        if (d == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        val estado = EstadoTicket.valueOf(d.ticket.estado)
        val comentarios = d.comentarios.sortedBy { it.fecha }
        val lista = rememberLazyListState()

        // Cuando llega un comentario nuevo (mio o de Soporte), bajamos al final.
        LaunchedEffect(comentarios.size) {
            if (comentarios.isNotEmpty()) lista.animateScrollToItem((lista.layoutInfo.totalItemsCount - 1).coerceAtLeast(0))
        }

        LazyColumn(
            state = lista,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { CabeceraTicket(d) }
            item { StepperTicket(estado) }

            if (estado == EstadoTicket.RESUELTO) {
                item { AvisoResuelto() }
            }

            item { Text("Conversación", style = MaterialTheme.typography.titleSmall) }
            if (comentarios.isEmpty()) {
                item {
                    Text(
                        "Aún no hay mensajes. Escribe a Soporte TI si necesitas algo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(comentarios, key = { it.id }) { c ->
                val propio = c.autorId == usuarioId
                BurbujaComentario(
                    autor = if (propio) "Tú" else c.autorNombre,
                    hora = horaComentario(c.fecha),
                    texto = c.texto,
                    esPropio = propio
                )
            }

            when (estado) {
                EstadoTicket.RESUELTO -> item {
                    TarjetaCalificar(
                        ticketId = d.ticket.id,
                        onConfirmar = { estrellas, final -> vm.cerrar(estrellas, final) },
                        onReabrir = vm::reabrir
                    )
                }

                EstadoTicket.CERRADO -> item { CalificacionRecibida(d.ticket.calificacion ?: 0) }
                else -> Unit
            }
        }

        // El campo de comentarios desaparece cuando el ticket esta CERRADO.
        // En RESUELTO se usa el "comentario final" de la tarjeta de calificacion.
        if (estado == EstadoTicket.ABIERTO || estado == EstadoTicket.EN_PROCESO) {
            BarraComentario(placeholder = "Escribe un comentario...", onEnviar = vm::comentar)
        }
    }
}

@Composable
fun CabeceraTicket(d: TicketConDetalle, mostrarCliente: Boolean = false) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(d.ticket.titulo, style = MaterialTheme.typography.titleMedium)
            if (mostrarCliente) {
                Text(
                    "${d.cliente?.nombre ?: "Estudiante"} · ${d.ticket.ubicacion} · ${d.categoria?.nombre ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                EstadoChip(EstadoTicket.valueOf(d.ticket.estado))
                Spacer(Modifier.width(6.dp))
                PrioridadChip(Prioridad.valueOf(d.ticket.prioridad))
            }
            Spacer(Modifier.height(8.dp))
            if (mostrarCliente) {
                Text(d.ticket.descripcion, style = MaterialTheme.typography.bodyMedium)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        iconoCategoria(d.categoria?.icono ?: "tag"),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        listOfNotNull(
                            d.categoria?.nombre,
                            d.ticket.ubicacion.ifBlank { null },
                            tiempoRelativo(d.ticket.fecha)
                        ).joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AvisoResuelto() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorResuelto.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = ColorResuelto)
        Spacer(Modifier.width(10.dp))
        Text(
            "Soporte marcó este ticket como resuelto. ¿El problema se solucionó?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TarjetaCalificar(
    ticketId: Int,
    onConfirmar: (Int, String) -> Unit,
    onReabrir: () -> Unit
) {
    var estrellas by rememberSaveable(ticketId) { mutableIntStateOf(0) }
    var comentarioFinal by rememberSaveable(ticketId) { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Califica la atención", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                Estrellas(valor = estrellas, onCambio = { estrellas = it })
                Text(
                    if (estrellas == 0) "Toca una estrella" else "$estrellas de 5 estrellas",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        AyudaTextField(
            valor = comentarioFinal,
            onCambio = { comentarioFinal = it },
            etiqueta = "Comentario final (opcional)"
        )
        // La pantalla solo habilita o deshabilita; la regla real se valida en el ViewModel.
        AyudaButton(
            texto = "Confirmar y cerrar",
            onClick = { onConfirmar(estrellas, comentarioFinal) },
            enabled = estrellas > 0
        )
        AyudaButton(texto = "Reabrir ticket", onClick = onReabrir, estilo = EstiloBoton.PELIGRO)
    }
}

@Composable
private fun CalificacionRecibida(calificacion: Int) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Ticket cerrado", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(6.dp))
            Estrellas(valor = calificacion, tamano = 28.dp)
            Text(
                "Calificaste la atención con $calificacion de 5",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
