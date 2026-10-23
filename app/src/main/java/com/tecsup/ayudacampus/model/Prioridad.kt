package com.tecsup.ayudacampus.model

/** Prioridad del ticket. En la tabla se guarda como texto (Prioridad.name). */
enum class Prioridad(val etiqueta: String) {
    BAJA("Baja"),
    MEDIA("Media"),
    ALTA("Alta")
}
