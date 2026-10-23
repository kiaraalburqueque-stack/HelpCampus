package com.tecsup.ayudacampus.data.repository

import com.tecsup.ayudacampus.data.local.entity.Categoria
import kotlinx.coroutines.flow.Flow

/**
 * El contrato de datos de la app. Los ViewModels solo conocen esta interface,
 * nunca los DAOs. Hoy la implementa Room; manana podria implementarla un servicio web.
 */
interface AyudaRepository {
    // Categorias (ejemplo resuelto)
    fun observarCategorias(): Flow<List<Categoria>>
    fun observarCategoriasActivas(): Flow<List<Categoria>>
    suspend fun guardarCategoria(c: Categoria)
    suspend fun eliminarCategoria(c: Categoria)
    suspend fun cambiarActiva(id: Int, activa: Boolean)

    // TODO (Paso 4): Usuarios, Tickets y Panel
}
