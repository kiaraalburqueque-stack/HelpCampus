package com.tecsup.ayudacampus.ui.soporte

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.ui.components.EstadoVacio
import com.tecsup.ayudacampus.ui.components.FiltrosEstado
import com.tecsup.ayudacampus.ui.components.IndicadorSla
import com.tecsup.ayudacampus.ui.components.TicketCard
import com.tecsup.ayudacampus.ui.components.iconoCategoria
import com.tecsup.ayudacampus.ui.components.nombreCorto
import com.tecsup.ayudacampus.ui.theme.colorPrioridad
import com.tecsup.ayudacampus.viewmodel.BandejaViewModel
import com.tecsup.ayudacampus.viewmodel.CriteriosBusqueda

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandejaScreen(vm: BandejaViewModel, onAtender: (Int) -> Unit) {
    val tickets by vm.tickets.collectAsState()
    val slas by vm.slas.collectAsState()
    val filtro by vm.filtro.collectAsState()
    val criterios by vm.criterios.collectAsState()
    val categorias by vm.categorias.collectAsState()
    var mostrarAvanzado by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            titulo = "Tickets",
            subtitulo = "${tickets.size} resultados · prioridad Alta primero",
            acciones = {
                IconButton(onClick = { mostrarAvanzado = !mostrarAvanzado }) {
                    BadgedBox(
                        badge = {
                            if (criterios.activos > 0) Badge { Text(criterios.activos.toString()) }
                        }
                    ) {
                        Icon(Icons.Outlined.Tune, contentDescription = "Búsqueda avanzada")
                    }
                }
            }
        )

        AyudaTextField(
            valor = criterios.texto,
            onCambio = vm::cambiarTexto,
            etiqueta = "Buscar título, número, cliente o lugar",
            icono = Icons.Outlined.Search,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        AnimatedVisibility(
            visible = mostrarAvanzado,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            PanelFiltrosAvanzados(
                criterios = criterios,
                categorias = categorias,
                onPrioridad = vm::alternarPrioridad,
                onCategoria = vm::alternarCategoria,
                onLimpiar = vm::limpiarFiltros
            )
        }

        Spacer(Modifier.height(8.dp))
        FiltrosEstado(seleccionado = filtro, onCambio = vm::cambiarFiltro)
        Spacer(Modifier.height(8.dp))

        if (tickets.isEmpty()) {
            EstadoVacio(
                mensaje = "Ningún ticket coincide",
                detalle = if (criterios.activos > 0) "Prueba quitando algún filtro." else null
            )
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

/** Panel desplegable: prioridades (se pueden combinar) y una categoria. */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun PanelFiltrosAvanzados(
    criterios: CriteriosBusqueda,
    categorias: List<Categoria>,
    onPrioridad: (Prioridad) -> Unit,
    onCategoria: (Int) -> Unit,
    onLimpiar: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Prioridad", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Prioridad.entries.reversed().forEach { p ->
                    FilterChip(
                        selected = p in criterios.prioridades,
                        onClick = { onPrioridad(p) },
                        label = { Text(p.etiqueta) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colorPrioridad(p),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text("Categoría", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categorias.forEach { c ->
                    FilterChip(
                        selected = criterios.categoriaId == c.id,
                        onClick = { onCategoria(c.id) },
                        label = { Text(c.nombre) },
                        leadingIcon = {
                            Icon(iconoCategoria(c.icono), contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
            if (criterios.activos > 0) {
                TextButton(onClick = onLimpiar, modifier = Modifier.align(Alignment.End)) {
                    Text("Limpiar filtros")
                }
            }
        }
    }
}
