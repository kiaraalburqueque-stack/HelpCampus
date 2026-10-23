package com.tecsup.ayudacampus.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.ayudacampus.AyudaApp
import com.tecsup.ayudacampus.model.Rol
import com.tecsup.ayudacampus.ui.cliente.LoginScreen
import com.tecsup.ayudacampus.ui.cliente.SplashScreen
import com.tecsup.ayudacampus.ui.soporte.CategoriasScreen
import com.tecsup.ayudacampus.ui.theme.AyudaTheme
import com.tecsup.ayudacampus.viewmodel.AyudaViewModelFactory
import com.tecsup.ayudacampus.viewmodel.CategoriasViewModel
import com.tecsup.ayudacampus.viewmodel.SesionViewModel

/** Un item de la barra inferior. 'tambien' marca el item como activo en rutas hijas. */
private data class ItemBarra(
    val ruta: String,
    val etiqueta: String,
    val icono: ImageVector,
    val tambien: Set<String> = emptySet()
)

private val ITEMS_CLIENTE = listOf(
    ItemBarra(Rutas.MIS_TICKETS, "Tickets", Icons.Outlined.Menu, setOf(Rutas.DETALLE)),
    ItemBarra(Rutas.NUEVO_TICKET, "Nuevo", Icons.Filled.Add),
    ItemBarra(Rutas.PERFIL, "Perfil", Icons.Outlined.Person)
)

private val ITEMS_SOPORTE = listOf(
    ItemBarra(Rutas.PANEL, "Panel", Icons.Outlined.BarChart),
    ItemBarra(Rutas.BANDEJA, "Tickets", Icons.Outlined.Menu, setOf(Rutas.ATENDER)),
    ItemBarra(Rutas.CATEGORIAS, "Categorías", Icons.Outlined.LocalOffer),
    ItemBarra(Rutas.AJUSTES, "Ajustes", Icons.Outlined.Settings)
)

private val RUTAS_BARRA_CLIENTE = ITEMS_CLIENTE.flatMap { it.tambien + it.ruta }.toSet()
private val RUTAS_BARRA_SOPORTE = ITEMS_SOPORTE.flatMap { it.tambien + it.ruta }.toSet()

@Composable
fun NavAyuda(app: AyudaApp) {
    val repo = app.repository
    val prefs = app.preferencias
    val factory = remember { AyudaViewModelFactory(repo, prefs) }

    // La sesion vive a nivel de Activity: la comparten todas las pantallas.
    val sesionVm: SesionViewModel = viewModel(factory = factory)
    val usuario by sesionVm.usuarioActual.collectAsState()
    val oscuro by sesionVm.modoOscuro.collectAsState()
    val inicio by sesionVm.inicio.collectAsState()

    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route ?: ""

    // El tema depende del perfil: cian para el estudiante, violeta para soporte.
    val rol = usuario?.let { Rol.valueOf(it.rol) } ?: Rol.CLIENTE

    /** Va al grafo del perfil y limpia toda la pila (no se puede volver atras al login). */
    fun irAlGrafo(r: Rol) {
        val destino = if (r == Rol.CLIENTE) Rutas.GRAFO_CLIENTE else Rutas.GRAFO_SOPORTE
        nav.navigate(destino) {
            popUpTo(nav.graph.id) { inclusive = true }
        }
    }

    AyudaTheme(rol = rol, oscuro = oscuro) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                when (rutaActual) {
                    in RUTAS_BARRA_CLIENTE -> BarraInferior(ITEMS_CLIENTE, rutaActual) {
                        irATab(nav, it, Rutas.MIS_TICKETS)
                    }

                    in RUTAS_BARRA_SOPORTE -> BarraInferior(ITEMS_SOPORTE, rutaActual) {
                        irATab(nav, it, Rutas.PANEL)
                    }

                    else -> Unit // Splash, Login y formularios no llevan barra
                }
            }
        ) { padding ->
            NavHost(
                navController = nav,
                startDestination = Rutas.SPLASH,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                composable(Rutas.SPLASH) {
                    SplashScreen(
                        inicio = inicio,
                        onComenzar = {
                            nav.navigate(Rutas.LOGIN) { popUpTo(Rutas.SPLASH) { inclusive = true } }
                        },
                        onSesionActiva = { irAlGrafo(it) }
                    )
                }

                composable(Rutas.LOGIN) {
                    LoginScreen(vm = sesionVm, onIngreso = { irAlGrafo(it) })
                }

                // ================= Grafo del estudiante =================
                navigation(startDestination = Rutas.MIS_TICKETS, route = Rutas.GRAFO_CLIENTE) {
                    composable(Rutas.MIS_TICKETS) {
                        Pendiente("Mis tickets (Paso 7)")
                    }
                    composable(Rutas.NUEVO_TICKET) {
                        Pendiente("Nuevo ticket (Paso 8)")
                    }
                    composable(
                        Rutas.DETALLE,
                        arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
                    ) {
                        Pendiente("Detalle (Paso 9)")
                    }
                    composable(Rutas.PERFIL) {
                        Pendiente("Perfil (Paso 12)")
                    }
                }

                // ================= Grafo de soporte =================
                navigation(startDestination = Rutas.PANEL, route = Rutas.GRAFO_SOPORTE) {
                    composable(Rutas.PANEL) {
                        Pendiente("Panel (Paso 10)")
                    }
                    composable(Rutas.BANDEJA) {
                        Pendiente("Bandeja (Paso 10)")
                    }
                    composable(
                        Rutas.ATENDER,
                        arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
                    ) {
                        Pendiente("Atender ticket (Paso 11)")
                    }
                    composable(Rutas.CATEGORIAS) {
                        val vm: CategoriasViewModel = viewModel(factory = factory)
                        CategoriasScreen(vm = vm, onEditar = { /* Paso 11 */ })
                    }
                    composable(
                        Rutas.FORM_CATEGORIA,
                        arguments = listOf(navArgument("id") {
                            type = NavType.IntType
                            defaultValue = 0
                        })
                    ) {
                        Pendiente("Formulario de categoría (Paso 11)")
                    }
                    composable(Rutas.AJUSTES) {
                        Pendiente("Ajustes (Paso 12)")
                    }
                }
            }
        }
    }
}

/**
 * Navega a una pestana de la barra inferior sin apilar pantallas repetidas.
 * Si la pestana es la de inicio del grafo, vuelve a ella.
 */
private fun irATab(nav: NavHostController, ruta: String, inicioGrafo: String) {
    if (ruta == inicioGrafo) {
        nav.popBackStack(inicioGrafo, inclusive = false)
    } else {
        nav.navigate(ruta) {
            popUpTo(inicioGrafo)
            launchSingleTop = true
        }
    }
}

@Composable
private fun BarraInferior(items: List<ItemBarra>, rutaActual: String, onIr: (String) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        items.forEach { item ->
            val activo = rutaActual == item.ruta || rutaActual in item.tambien
            NavigationBarItem(
                selected = activo,
                onClick = { if (rutaActual != item.ruta) onIr(item.ruta) },
                icon = { Icon(item.icono, contentDescription = item.etiqueta) },
                label = { Text(item.etiqueta) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

/** Marcador temporal para pantallas que se construyen en pasos siguientes. */
@Composable
private fun Pendiente(texto: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(texto, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
