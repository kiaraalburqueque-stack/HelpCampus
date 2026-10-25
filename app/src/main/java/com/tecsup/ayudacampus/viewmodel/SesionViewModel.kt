package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.prefs.PreferenciasStore
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Lo que el Splash necesita saber para decidir a donde ir. */
sealed interface EstadoInicio {
    data object Cargando : EstadoInicio
    data object SinSesion : EstadoInicio
    data class ConSesion(val rol: Rol) : EstadoInicio
}

/**
 * Maneja quien esta usando la app. Vive a nivel de Activity (lo crea NavAyuda)
 * para que todas las pantallas compartan el mismo usuario.
 */
class SesionViewModel(
    private val repo: AyudaRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _inicio = MutableStateFlow<EstadoInicio>(EstadoInicio.Cargando)
    val inicio: StateFlow<EstadoInicio> = _inicio.asStateFlow()

    // Preferencias de DataStore expuestas como StateFlow (Eagerly: se leen desde el inicio).
    val modoOscuro: StateFlow<Boolean> = prefs.modoOscuro
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val notificaciones: StateFlow<Boolean> = prefs.notificaciones
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val recibirTickets: StateFlow<Boolean> = prefs.recibirTickets
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    init {
        // Si ya hay un perfil guardado en DataStore, recuperamos al usuario (el Splash salta el login).
        viewModelScope.launch {
            val rol = prefs.perfilActivo.first()
            val id = prefs.usuarioId.first()
            val usuario = if (rol.isNotEmpty()) repo.obtenerUsuario(id) else null
            if (usuario != null) {
                _usuarioActual.value = usuario
                _inicio.value = EstadoInicio.ConSesion(Rol.valueOf(usuario.rol))
            } else {
                _inicio.value = EstadoInicio.SinSesion
            }
        }
    }

    fun login(correo: String, clave: String, rolSeleccionado: Rol) {
        if (correo.isBlank() || clave.isBlank()) {
            _error.value = "Completa el correo y la contraseña"
            return
        }
        viewModelScope.launch {
            _cargando.value = true
            val usuario = repo.login(correo.trim(), clave)
            when {
                usuario == null ->
                    _error.value = "Correo o contraseña incorrectos"

                usuario.rol != rolSeleccionado.name ->
                    _error.value = "Esta cuenta no pertenece al perfil ${rolSeleccionado.etiqueta}"

                else -> {
                    prefs.guardarSesion(usuario.rol, usuario.id)
                    _error.value = null
                    _usuarioActual.value = usuario
                }
            }
            _cargando.value = false
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    fun guardarModoOscuro(valor: Boolean) {
        viewModelScope.launch { prefs.guardarModoOscuro(valor) }
    }

    fun guardarNotificaciones(valor: Boolean) {
        viewModelScope.launch { prefs.guardarNotificaciones(valor) }
    }

    fun guardarRecibirTickets(valor: Boolean) {
        viewModelScope.launch { prefs.guardarRecibirTickets(valor) }
    }

    /**
     * Modo practica: pasa al otro perfil (estudiante <-> soporte) sin volver a loguearse.
     * Guarda el nuevo perfil en DataStore y avisa a la pantalla para que navegue.
     */
    fun cambiarPerfil(onCambio: (Rol) -> Unit) {
        val actual = _usuarioActual.value ?: return
        val otroRol = if (actual.rol == Rol.CLIENTE.name) Rol.ADMIN else Rol.CLIENTE
        viewModelScope.launch {
            val otro = repo.obtenerUsuarioPorRol(otroRol)
            if (otro == null) {
                _error.value = "No hay un usuario de ${otroRol.etiqueta} para practicar"
                return@launch
            }
            prefs.guardarSesion(otro.rol, otro.id)
            _usuarioActual.value = otro
            onCambio(otroRol)
        }
    }

    /** Borra el perfil guardado; la pantalla vuelve al Login. */
    fun cerrarSesion() {
        _usuarioActual.value = null
        _error.value = null
        viewModelScope.launch { prefs.cerrarSesion() }
    }
}
