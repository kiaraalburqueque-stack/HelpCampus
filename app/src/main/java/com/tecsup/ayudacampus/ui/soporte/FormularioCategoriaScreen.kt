package com.tecsup.ayudacampus.ui.soporte

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.ui.components.EstiloBoton
import com.tecsup.ayudacampus.ui.components.ICONOS_CATEGORIA
import com.tecsup.ayudacampus.ui.components.iconoCategoria
import com.tecsup.ayudacampus.viewmodel.CategoriasViewModel

/** Sirve para crear (id = 0) y para editar (id de la categoria). */
@Composable
fun FormularioCategoriaScreen(
    vm: CategoriasViewModel,
    id: Int,
    onListo: () -> Unit
) {
    LaunchedEffect(id) { vm.cargar(id) }

    val form by vm.form.collectAsState()
    val errorNombre by vm.errorNombre.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val conteos by vm.conteos.collectAsState()
    val esNueva = id == 0

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(titulo = if (esNueva) "Nueva categoría" else "Editar categoría", onBack = onListo)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AyudaTextField(
                valor = form.nombre,
                onCambio = vm::cambiarNombre,
                etiqueta = "Nombre de la categoría",
                error = errorNombre
            )

            Text("Ícono", style = MaterialTheme.typography.titleSmall)
            ICONOS_CATEGORIA.chunked(3).forEach { fila ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    fila.forEach { icono ->
                        val elegido = icono == form.icono
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .background(
                                    if (elegido) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    BorderStroke(
                                        if (elegido) 2.dp else 1.dp,
                                        if (elegido) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline
                                    ),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { vm.cambiarIcono(icono) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                iconoCategoria(icono),
                                contentDescription = icono,
                                tint = if (elegido) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Categoría activa", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "Los estudiantes podrán elegirla",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = form.activa, onCheckedChange = vm::cambiarActiva)
                }
            }

            Text("Vista previa", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(iconoCategoria(form.icono), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(form.nombre.ifBlank { "Nombre de la categoría" }, style = MaterialTheme.typography.titleSmall)
                    Text(
                        "${conteos[form.id] ?: 0} tickets",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            mensaje?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            AyudaButton(texto = "Guardar categoría", onClick = { vm.guardar(form, onListo) })
            AyudaButton(texto = "Cancelar", onClick = onListo, estilo = EstiloBoton.CONTORNO)
            if (!esNueva) {
                AyudaButton(
                    texto = "Eliminar categoría",
                    onClick = { vm.eliminar(form, onListo) },
                    estilo = EstiloBoton.PELIGRO
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
