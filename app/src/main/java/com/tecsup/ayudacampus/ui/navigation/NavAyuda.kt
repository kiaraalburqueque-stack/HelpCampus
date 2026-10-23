package com.tecsup.ayudacampus.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.ayudacampus.AyudaApp
import com.tecsup.ayudacampus.model.Rol
import com.tecsup.ayudacampus.ui.cliente.SplashScreen
import com.tecsup.ayudacampus.ui.soporte.CategoriasScreen
import com.tecsup.ayudacampus.ui.theme.AyudaTheme
import com.tecsup.ayudacampus.viewmodel.AyudaViewModelFactory
import com.tecsup.ayudacampus.viewmodel.CategoriasViewModel

@Composable
fun NavAyuda(app: AyudaApp) {
    val factory = remember { AyudaViewModelFactory(app.repository) }
    val nav = rememberNavController()

    AyudaTheme(rol = Rol.ADMIN) {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            NavHost(
                navController = nav,
                startDestination = Rutas.SPLASH,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                composable(Rutas.SPLASH) {
                    SplashScreen(onComenzar = { nav.navigate(Rutas.CATEGORIAS) })
                }
                composable(Rutas.CATEGORIAS) {
                    val vm: CategoriasViewModel = viewModel(factory = factory)
                    CategoriasScreen(vm = vm, onEditar = { /* TODO (Paso 11) */ })
                }
                // TODO: login, grafo del estudiante y grafo de soporte
            }
        }
    }
}
