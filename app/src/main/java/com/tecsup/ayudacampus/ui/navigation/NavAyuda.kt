package com.tecsup.ayudacampus.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import com.tecsup.ayudacampus.ui.cliente.DetalleTicketScreen
import com.tecsup.ayudacampus.ui.cliente.LoginScreen
import com.tecsup.ayudacampus.ui.cliente.MisTicketsScreen
import com.tecsup.ayudacampus.ui.cliente.NuevoTicketScreen
import com.tecsup.ayudacampus.ui.cliente.PerfilScreen
import com.tecsup.ayudacampus.ui.cliente.RegistroScreen
import com.tecsup.ayudacampus.ui.cliente.SplashScreen
import com.tecsup.ayudacampus.ui.soporte.AjustesScreen
import com.tecsup.ayudacampus.ui.soporte.AtenderTicketScreen
import com.tecsup.ayudacampus.ui.soporte.BandejaScreen
import com.tecsup.ayudacampus.ui.soporte.CategoriasScreen
import com.tecsup.ayudacampus.ui.soporte.FormularioCategoriaScreen
import com.tecsup.ayudacampus.ui.soporte.PanelScreen
import com.tecsup.ayudacampus.ui.theme.AyudaTheme
import com.tecsup.ayudacampus.viewmodel.AtenderViewModel
import com.tecsup.ayudacampus.viewmodel.AyudaViewModelFactory
import com.tecsup.ayudacampus.viewmodel.BandejaViewModel
import com.tecsup.ayudacampus.viewmodel.CategoriasViewModel
import com.tecsup.ayudacampus.viewmodel.MisTicketsViewModel
import com.tecsup.ayudacampus.viewmodel.NuevoTicketViewModel
import com.tecsup.ayudacampus.viewmodel.PanelViewModel
import com.tecsup.ayudacampus.viewmodel.RegistroViewModel
import com.tecsup.ayudacampus.viewmodel.SesionViewModel
import com.tecsup.ayudacampus.viewmodel.TicketDetalleViewModel

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

/** Clave para pasar el correo recien registrado del Registro al Login. */
private const val CLAVE_CORREO_REGISTRADO = "correoRegistrado"

private val RUTAS_BARRA_CLIENTE = ITEMS_CLIENTE.flatMap { it.tambien + it.ruta }.toSet()
private val RUTAS_BARRA_SOPORTE = ITEMS_SOPORTE.flatMap { it.tambien + it.ruta }.toSet()

@OptIn(ExperimentalLayoutApi::class)
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

    /** Cambio de perfil (modo practica): guarda el otro perfil y salta a su grafo. */
    fun cambiarPerfil() {
        sesionVm.cambiarPerfil { nuevoRol -> irAlGrafo(nuevoRol) }
    }

    /** Cierra sesion y vuelve al Login limpiando la pila. */
    fun cerrarSesion() {
        sesionVm.cerrarSesion()
        nav.navigate(Rutas.LOGIN) {
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
                // Fase 2: transicion suave entre pantallas (aparece y se desliza un poco).
                enterTransition = { fadeIn(tween(250)) + slideInHorizontally(tween(250)) { it / 10 } },
                exitTransition = { fadeOut(tween(200)) },
                popEnterTransition = { fadeIn(tween(250)) },
                popExitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(250)) { it / 10 } },
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .consumeWindowInsets(padding)
                    .imePadding() // el teclado no tapa el campo de comentarios
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

                composable(Rutas.LOGIN) { backStack ->
                    // El registro deja aqui el correo de la cuenta nueva (SavedStateHandle de la entrada).
                    val correoRegistrado by backStack.savedStateHandle
                        .getStateFlow<String?>(CLAVE_CORREO_REGISTRADO, null)
                        .collectAsState()
                    LoginScreen(
                        vm = sesionVm,
                        onIngreso = { irAlGrafo(it) },
                        onRegistrarse = { nav.navigate(Rutas.REGISTRO) },
                        correoRegistrado = correoRegistrado,
                        onCorreoUsado = { backStack.savedStateHandle[CLAVE_CORREO_REGISTRADO] = null }
                    )
                }

                // Fase 2: registro de estudiante. Como en una app real, despues de crear la cuenta
                // se vuelve al Login para iniciar sesion con el correo y la contrasena nuevos.
                composable(Rutas.REGISTRO) {
                    val vm: RegistroViewModel = viewModel(factory = factory)
                    RegistroScreen(
                        vm = vm,
                        onBack = { nav.popBackStack() },
                        onRegistrado = { nuevo ->
                            nav.previousBackStackEntry?.savedStateHandle?.set(CLAVE_CORREO_REGISTRADO, nuevo.correo)
                            nav.popBackStack()
                        }
                    )
                }

                // ================= Grafo del estudiante =================
                navigation(startDestination = Rutas.MIS_TICKETS, route = Rutas.GRAFO_CLIENTE) {
                    composable(Rutas.MIS_TICKETS) {
                        usuario?.let { u ->
                            val vm: MisTicketsViewModel = viewModel(factory = AyudaViewModelFactory(repo, prefs, u))
                            MisTicketsScreen(
                                vm = vm,
                                nombre = u.nombre,
                                onAbrir = { id -> nav.navigate(Rutas.detalle(id)) },
                                onNuevo = { irATab(nav, Rutas.NUEVO_TICKET, Rutas.MIS_TICKETS) }
                            )
                        }
                    }
                    composable(Rutas.NUEVO_TICKET) {
                        usuario?.let { u ->
                            val vm: NuevoTicketViewModel = viewModel(factory = AyudaViewModelFactory(repo, prefs, u))
                            NuevoTicketScreen(
                                vm = vm,
                                onBack = { irATab(nav, Rutas.MIS_TICKETS, Rutas.MIS_TICKETS) },
                                // al enviar volvemos a Mis tickets: el nuevo aparece arriba (orden fecha DESC)
                                onEnviado = { irATab(nav, Rutas.MIS_TICKETS, Rutas.MIS_TICKETS) }
                            )
                        }
                    }
                    composable(
                        Rutas.DETALLE,
                        arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
                    ) { backStack ->
                        val ticketId = backStack.arguments?.getInt("ticketId") ?: 0
                        usuario?.let { u ->
                            val vm: TicketDetalleViewModel = viewModel(
                                factory = AyudaViewModelFactory(repo, prefs, u, ticketId)
                            )
                            DetalleTicketScreen(vm = vm, usuarioId = u.id, onBack = { nav.popBackStack() })
                        }
                    }
                    composable(Rutas.PERFIL) {
                        usuario?.let { u ->
                            val ticketsVm: MisTicketsViewModel = viewModel(
                                factory = AyudaViewModelFactory(repo, prefs, u)
                            )
                            PerfilScreen(
                                sesionVm = sesionVm,
                                ticketsVm = ticketsVm,
                                onCambiarPerfil = { cambiarPerfil() },
                                onCerrarSesion = { cerrarSesion() }
                            )
                        }
                    }
                }

                // ================= Grafo de soporte =================
                navigation(startDestination = Rutas.PANEL, route = Rutas.GRAFO_SOPORTE) {
                    composable(Rutas.PANEL) {
                        val vm: PanelViewModel = viewModel(factory = factory)
                        PanelScreen(vm = vm, onVerAltas = { irATab(nav, Rutas.BANDEJA, Rutas.PANEL) })
                    }
                    composable(Rutas.BANDEJA) {
                        val vm: BandejaViewModel = viewModel(factory = factory)
                        BandejaScreen(vm = vm, onAtender = { id -> nav.navigate(Rutas.atender(id)) })
                    }
                    composable(
                        Rutas.ATENDER,
                        arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
                    ) { backStack ->
                        val ticketId = backStack.arguments?.getInt("ticketId") ?: 0
                        usuario?.let { u ->
                            val vm: AtenderViewModel = viewModel(
                                factory = AyudaViewModelFactory(repo, prefs, u, ticketId)
                            )
                            AtenderTicketScreen(vm = vm, usuarioId = u.id, onBack = { nav.popBackStack() })
                        }
                    }
                    composable(Rutas.CATEGORIAS) {
                        val vm: CategoriasViewModel = viewModel(factory = factory)
                        CategoriasScreen(
                            vm = vm,
                            onEditar = { id -> nav.navigate(Rutas.formCategoria(id)) },
                            onNueva = { nav.navigate(Rutas.formCategoria()) }
                        )
                    }
                    composable(
                        Rutas.FORM_CATEGORIA,
                        arguments = listOf(navArgument("id") {
                            type = NavType.IntType
                            defaultValue = 0
                        })
                    ) { backStack ->
                        val id = backStack.arguments?.getInt("id") ?: 0
                        val vm: CategoriasViewModel = viewModel(factory = factory)
                        FormularioCategoriaScreen(vm = vm, id = id, onListo = { nav.popBackStack() })
                    }
                    composable(Rutas.AJUSTES) {
                        AjustesScreen(
                            sesionVm = sesionVm,
                            onCambiarPerfil = { cambiarPerfil() },
                            onCerrarSesion = { cerrarSesion() }
                        )
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
