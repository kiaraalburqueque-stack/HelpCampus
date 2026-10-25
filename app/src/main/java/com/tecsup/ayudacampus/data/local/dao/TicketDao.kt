package com.tecsup.ayudacampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.tecsup.ayudacampus.data.local.entity.Comentario
import com.tecsup.ayudacampus.data.local.entity.Ticket
import com.tecsup.ayudacampus.data.local.relation.TicketConDetalle
import com.tecsup.ayudacampus.model.ConteoCategoria
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {

    // ---------- Escritura (suspend: se ejecuta una vez) ----------
    @Insert
    suspend fun insertarTicket(t: Ticket): Long

    @Insert
    suspend fun insertarComentario(c: Comentario): Long

    @Query("UPDATE tickets SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String)

    @Query("UPDATE tickets SET prioridad = :prioridad WHERE id = :id")
    suspend fun actualizarPrioridad(id: Int, prioridad: String)

    @Query("UPDATE tickets SET estado = 'CERRADO', calificacion = :calificacion, fechaCierre = :fechaCierre WHERE id = :id")
    suspend fun cerrar(id: Int, calificacion: Int, fechaCierre: Long)

    @Query("UPDATE tickets SET estado = 'ABIERTO', calificacion = NULL, fechaCierre = NULL WHERE id = :id")
    suspend fun reabrir(id: Int)

    @Query("SELECT COUNT(*) FROM tickets WHERE categoriaId = :categoriaId")
    suspend fun contarDeCategoria(categoriaId: Int): Int

    // ---------- Lectura (Flow: avisa cada vez que cambian los datos) ----------
    @Transaction
    @Query("SELECT * FROM tickets WHERE clienteId = :clienteId ORDER BY fecha DESC")
    fun observarPorCliente(clienteId: Int): Flow<List<TicketConDetalle>>

    @Transaction
    @Query(
        """
        SELECT * FROM tickets
        ORDER BY CASE prioridad WHEN 'ALTA' THEN 0 WHEN 'MEDIA' THEN 1 ELSE 2 END,
                 fecha DESC
        """
    )
    fun observarTodos(): Flow<List<TicketConDetalle>>

    @Transaction
    @Query("SELECT * FROM tickets WHERE id = :id")
    fun observarUno(id: Int): Flow<TicketConDetalle?>

    // ---------- Panel ----------
    @Query("SELECT COUNT(*) FROM tickets WHERE estado = :estado")
    fun contarPorEstado(estado: String): Flow<Int>

    @Query(
        """
        SELECT c.id AS categoriaId, c.nombre AS nombre, c.icono AS icono, COUNT(t.id) AS cantidad
        FROM categorias c
        LEFT JOIN tickets t ON t.categoriaId = c.id
        GROUP BY c.id
        ORDER BY c.id
        """
    )
    fun contarPorCategoria(): Flow<List<ConteoCategoria>>

    @Query("SELECT COUNT(*) FROM tickets WHERE prioridad = 'ALTA' AND estado = 'ABIERTO' AND fecha < :limite")
    fun contarAltasSinAtender(limite: Long): Flow<Int>

    // ---------- Indicadores de calidad (Fase 2) ----------
    /** Promedio en millis de (fechaCierre - fecha) de los tickets cerrados. null si no hay ninguno. */
    @Query("SELECT AVG(fechaCierre - fecha) FROM tickets WHERE fechaCierre IS NOT NULL")
    fun promedioResolucion(): Flow<Double?>

    /** Promedio de estrellas de los tickets calificados. null si no hay ninguno. */
    @Query("SELECT AVG(calificacion) FROM tickets WHERE calificacion IS NOT NULL")
    fun promedioCalificacion(): Flow<Double?>
}
