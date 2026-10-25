package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Estado del formulario de registro, con un error por campo. */
data class FormularioRegistro(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val confirmar: String = "",
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorClave: String? = null,
    val errorConfirmar: String? = null,
    val enviando: Boolean = false
)

/** Registro de estudiante: todas las reglas viven aqui, no en la pantalla. */
class RegistroViewModel(private val repo: AyudaRepository) : ViewModel() {

    private val _form = MutableStateFlow(FormularioRegistro())
    val form: StateFlow<FormularioRegistro> = _form.asStateFlow()

    private val formatoCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$")

    fun cambiarNombre(v: String) = _form.update { it.copy(nombre = v, errorNombre = null) }
    fun cambiarCorreo(v: String) = _form.update { it.copy(correo = v, errorCorreo = null) }
    fun cambiarClave(v: String) = _form.update { it.copy(clave = v, errorClave = null) }
    fun cambiarConfirmar(v: String) = _form.update { it.copy(confirmar = v, errorConfirmar = null) }

    /**
     * Reglas: nombre de al menos 3 letras, correo con formato valido y que no exista,
     * contrasena de al menos 4 caracteres y confirmacion igual.
     */
    fun registrar(onRegistrado: (Usuario) -> Unit) {
        val f = _form.value
        if (f.enviando) return
        val correo = f.correo.trim().lowercase()

        val errorNombre = if (f.nombre.trim().length < 3) "Escribe tu nombre completo" else null
        val errorCorreo = if (!formatoCorreo.matches(correo)) "Escribe un correo válido" else null
        val errorClave = if (f.clave.length < 4) "La contraseña debe tener al menos 4 caracteres" else null
        val errorConfirmar = if (f.confirmar != f.clave) "Las contraseñas no coinciden" else null

        if (listOf(errorNombre, errorCorreo, errorClave, errorConfirmar).any { it != null }) {
            _form.value = f.copy(
                errorNombre = errorNombre,
                errorCorreo = errorCorreo,
                errorClave = errorClave,
                errorConfirmar = errorConfirmar
            )
            return
        }

        _form.value = f.copy(enviando = true)
        viewModelScope.launch {
            // Regla: el correo debe ser unico.
            if (repo.existeCorreo(correo)) {
                _form.update { it.copy(errorCorreo = "Este correo ya está registrado", enviando = false) }
                return@launch
            }
            val usuario = repo.registrarEstudiante(f.nombre.trim(), correo, f.clave)
            _form.value = FormularioRegistro()
            onRegistrado(usuario)
        }
    }
}
