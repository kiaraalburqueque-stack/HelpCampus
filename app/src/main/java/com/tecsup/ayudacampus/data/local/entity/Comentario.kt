package com.tecsup.ayudacampus.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comentarios",
    foreignKeys = [
        ForeignKey(
            entity = Ticket::class,
            parentColumns = ["id"],
            childColumns = ["ticketId"],
            onDelete = ForeignKey.CASCADE      // si se borra el ticket, se borran sus comentarios
        )
    ],
    indices = [Index("ticketId")]
)
data class Comentario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ticketId: Int,                // FK -> tickets
    val autorId: Int,
    val autorNombre: String,          // "foto" del autor en ese momento
    val autorRol: String,             // Rol.name
    val texto: String,
    val fecha: Long = System.currentTimeMillis()
)
