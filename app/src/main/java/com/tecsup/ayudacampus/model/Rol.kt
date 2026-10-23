package com.tecsup.ayudacampus.model

/** Perfil del usuario. En la tabla se guarda como texto (Rol.name). */
enum class Rol(val etiqueta: String) {
    CLIENTE("Estudiante"),
    ADMIN("Soporte TI")
}
