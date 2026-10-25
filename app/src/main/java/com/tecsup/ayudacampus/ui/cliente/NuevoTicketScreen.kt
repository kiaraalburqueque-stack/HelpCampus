package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.ui.components.EstadoVacio
import com.tecsup.ayudacampus.ui.components.iconoCategoria
import com.tecsup.ayudacampus.ui.theme.colorPrioridad
import com.tecsup.ayudacampus.viewmodel.NuevoTicketViewModel
import com.tecsup.ayudacampus.viewmodel.Sugerencia
import com.tecsup.ayudacampus.viewmodel.horasSla

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NuevoTicketScreen(
    vm: NuevoTicketViewModel,
    onBack: () -> Unit,
    onEnviado: () -> Unit
) {
    val form by vm.form.collectAsState()
    val categorias by vm.categorias.collectAsState()
    val recibirTickets by vm.recibirTickets.collectAsState()
    val sugerencia by vm.sugerencia.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(titulo = "Nuevo ticket", onBack = onBack)

        if (!recibirTickets) {
            EstadoVacio(
                mensaje = "Mesa de ayuda cerrada",
                detalle = "Por ahora Soporte TI no está recibiendo tickets. Intenta más tarde.",
                icono = Icons.Outlined.Lock
            )
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Etiqueta("Categoría")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categorias.forEach { c ->
                    FilterChip(
                        selected = form.categoriaId == c.id,
                        onClick = { vm.elegirCategoria(c.id) },
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
            form.errorCategoria?.let { MensajeError(it) }

            Spacer(Modifier.height(12.dp))
            AyudaTextField(
                valor = form.titulo,
                onCambio = vm::cambiarTitulo,
                etiqueta = "Título",
                error = form.errorTitulo
            )
            Spacer(Modifier.height(8.dp))
            AyudaTextField(
                valor = form.ubicacion,
                onCambio = vm::cambiarUbicacion,
                etiqueta = "Ubicación",
                icono = Icons.Outlined.Place,
                placeholder = "Ej. Lab 3 · Pabellón B"
            )

            Etiqueta("Prioridad")
            SelectorPrioridad(seleccionada = form.prioridad, onCambio = vm::cambiarPrioridad)

            Spacer(Modifier.height(12.dp))
            AyudaTextField(
                valor = form.descripcion,
                onCambio = vm::cambiarDescripcion,
                etiqueta = "Descripción",
                lineasMin = 4,
                error = form.errorDescripcion
            )

            // Fase 2: el asistente sugiere categoria y prioridad mientras el estudiante escribe.
            AnimatedVisibility(
                visible = sugerencia != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                sugerencia?.let { TarjetaSugerencia(it, onAplicar = vm::aplicarSugerencia) }
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Tiempo de respuesta estimado: ${horasRespuesta(form.prioridad)} horas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(Modifier.height(16.dp))
            AyudaButton(
                texto = if (form.enviando) "Enviando..." else "Enviar ticket",
                onClick = { vm.enviar(onEnviado) },
                enabled = !form.enviando
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun horasRespuesta(p: Prioridad): Int = horasSla(p)

/** Tarjeta del asistente: que sugiere, por que (palabras detectadas) y boton Aplicar. */
@Composable
private fun TarjetaSugerencia(s: Sugerencia, onAplicar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "Sugerencia del asistente",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            listOfNotNull(s.categoria?.nombre, s.prioridad?.let { "Prioridad ${it.etiqueta}" }).joinToString(" · "),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        if (s.pistas.isNotEmpty()) {
            Text(
                "Detecté: " + s.pistas.joinToString(", ") { "«$it»" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
        }
        TextButton(onClick = onAplicar, modifier = Modifier.align(Alignment.End)) {
            Text("Aplicar", color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

/** Tres botones Baja / Media / Alta; el elegido se pinta con el color de la prioridad. */
@Composable
fun SelectorPrioridad(
    seleccionada: Prioridad,
    onCambio: (Prioridad) -> Unit,
    habilitado: Boolean = true
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Prioridad.entries.forEach { p ->
            val activa = p == seleccionada
            val color = colorPrioridad(p)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .background(
                        if (activa) color else MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(10.dp)
                    )
                    .border(
                        BorderStroke(1.dp, if (activa) color else MaterialTheme.colorScheme.outline),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable(enabled = habilitado) { onCambio(p) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    p.etiqueta,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (activa) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun Etiqueta(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
    )
}

@Composable
private fun MensajeError(texto: String) {
    Text(
        texto,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
    )
}
