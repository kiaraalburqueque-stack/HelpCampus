package com.tecsup.ayudacampus.ui.soporte

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.Contador
import com.tecsup.ayudacampus.ui.components.graficos.GraficoBarras
import com.tecsup.ayudacampus.ui.components.graficos.GraficoDona
import com.tecsup.ayudacampus.ui.theme.ColorAbierto
import com.tecsup.ayudacampus.ui.theme.ColorAlta
import com.tecsup.ayudacampus.ui.theme.ColorEnProceso
import com.tecsup.ayudacampus.ui.theme.ColorResuelto
import com.tecsup.ayudacampus.viewmodel.PanelViewModel

@Composable
fun PanelScreen(vm: PanelViewModel, onVerAltas: () -> Unit) {
    val abiertos by vm.abiertos.collectAsState()
    val enProceso by vm.enProceso.collectAsState()
    val resueltos by vm.resueltos.collectAsState()
    val porEstado by vm.porEstado.collectAsState()
    val porCategoria by vm.porCategoria.collectAsState()
    val altas by vm.altasSinAtender.collectAsState()
    val total = porEstado.sumOf { it.valor.toInt() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AppTopBar(
            titulo = "Panel de soporte",
            subtitulo = "Mesa de ayuda TI · Hoy",
            acciones = {
                IconButton(onClick = onVerAltas) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Alertas")
                }
            }
        )
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Contador(abiertos, "Abiertos", ColorAbierto, Modifier.weight(1f))
                Contador(enProceso, "En proceso", ColorEnProceso, Modifier.weight(1f))
                Contador(resueltos, "Resueltos", ColorResuelto, Modifier.weight(1f))
            }

            Seccion("Tickets por estado") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GraficoDona(
                        datos = porEstado,
                        modifier = Modifier.size(130.dp),
                        textoCentro = total.toString(),
                        subtextoCentro = "tickets"
                    )
                    Spacer(Modifier.width(20.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        porEstado.forEach { dato ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .size(10.dp)
                                        .background(dato.color, CircleShape)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    dato.etiqueta,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(dato.valor.toInt().toString(), style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }

            Seccion("Tickets por categoría") {
                GraficoBarras(datos = porCategoria)
            }

            if (altas > 0) {
                AlertaAltas(cantidad = altas, onVer = onVerAltas)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Seccion(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Text(titulo, style = MaterialTheme.typography.titleSmall)
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), content = contenido)
    }
}

@Composable
private fun AlertaAltas(cantidad: Int, onVer: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorAlta.copy(alpha = 0.10f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.Warning, contentDescription = null, tint = ColorAlta)
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                if (cantidad == 1) "1 ticket de prioridad Alta" else "$cantidad tickets de prioridad Alta",
                style = MaterialTheme.typography.titleSmall,
                color = ColorAlta
            )
            Text(
                "sin atender hace más de 1 hora",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Button(
            onClick = onVer,
            colors = ButtonDefaults.buttonColors(containerColor = ColorAlta, contentColor = Color.White)
        ) {
            Text("Ver")
        }
    }
}
