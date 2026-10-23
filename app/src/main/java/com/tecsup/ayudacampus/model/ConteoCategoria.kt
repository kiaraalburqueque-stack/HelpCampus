package com.tecsup.ayudacampus.model

/**
 * Resultado de una consulta con LEFT JOIN + COUNT (no es una tabla).
 * Room llena cada campo con la columna del SELECT que tiene el mismo nombre.
 */
data class ConteoCategoria(
    val categoriaId: Int,
    val nombre: String,
    val icono: String,
    val cantidad: Int
)
