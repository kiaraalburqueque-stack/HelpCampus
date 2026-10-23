package com.tecsup.ayudacampus

import android.app.Application
import com.tecsup.ayudacampus.data.local.AyudaDatabase
import com.tecsup.ayudacampus.data.local.DatosIniciales
import com.tecsup.ayudacampus.data.prefs.PreferenciasStore
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.data.repository.AyudaRepositoryRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Registrada en el AndroidManifest (android:name=".AyudaApp").
 * Crea una sola base de datos y un solo repository para toda la app.
 */
class AyudaApp : Application() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: AyudaDatabase by lazy { AyudaDatabase.obtener(this) }

    val repository: AyudaRepository by lazy {
        AyudaRepositoryRoom(
            database.categoriaDao(),
            database.usuarioDao(),
            database.ticketDao()
        )
    }

    val preferencias: PreferenciasStore by lazy { PreferenciasStore(this) }

    override fun onCreate() {
        super.onCreate()
        // Siembra los datos de prueba en segundo plano (solo la primera vez).
        scope.launch { DatosIniciales.sembrar(database) }
    }
}
