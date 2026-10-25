package com.tecsup.ayudacampus.viewmodel

import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.model.Prioridad
import java.text.Normalizer

/** Lo que el asistente propone al estudiante mientras escribe su ticket. */
data class Sugerencia(
    val categoria: Categoria?,
    val prioridad: Prioridad?,
    val pistas: List<String>          // palabras que detecto, para explicar el "por que"
)

/**
 * Asistente sin internet: lee el titulo y la descripcion y busca palabras clave.
 * Es una funcion pura (sin Android), asi que vive en la capa ViewModel y se puede probar facil.
 */
object AsistenteTicket {

    /** Palabras clave por icono de categoria (las categorias se identifican por su icono). */
    private val pistasCategoria = mapOf(
        "wifi" to listOf("wifi", "wi-fi", "internet", "red", "senal", "conexion", "router", "access point"),
        "pc" to listOf("teclado", "mouse", "monitor", "pantalla", "pc", "computadora", "enciende", "servidor", "proyector", "cpu"),
        "code" to listOf("instalar", "programa", "software", "android studio", "licencia", "office", "aplicacion", "actualizar"),
        "key" to listOf("contrasena", "clave", "cuenta", "usuario", "acceso", "correo", "aula virtual", "bloqueada"),
        "printer" to listOf("imprimir", "impresora", "impresion", "tinta", "toner", "papel", "escanear")
    )

    private val pistasAlta = listOf(
        "urgente", "todo el laboratorio", "todos", "alumnos", "examen", "no enciende", "caido", "clase", "no funciona nada"
    )
    private val pistasBaja = listOf("cuando puedan", "no es urgente", "consulta", "sugerencia", "a veces")

    fun analizar(titulo: String, descripcion: String, categorias: List<Categoria>): Sugerencia? {
        val texto = normalizar("$titulo $descripcion")
        if (texto.length < 6) return null

        // 1) Categoria: la que tenga mas palabras clave encontradas.
        var mejor: Categoria? = null
        var mejoresPistas = emptyList<String>()
        for (c in categorias) {
            val encontradas = pistasCategoria[c.icono].orEmpty().filter { contiene(texto, it) }
            if (encontradas.size > mejoresPistas.size) {
                mejor = c
                mejoresPistas = encontradas
            }
        }

        // 2) Prioridad: Alta si hay senales de urgencia, Baja si se nota que puede esperar.
        val altas = pistasAlta.filter { contiene(texto, it) }
        val bajas = pistasBaja.filter { contiene(texto, it) }
        val prioridad = when {
            altas.isNotEmpty() -> Prioridad.ALTA
            bajas.isNotEmpty() -> Prioridad.BAJA
            else -> null
        }

        if (mejor == null && prioridad == null) return null
        return Sugerencia(mejor, prioridad, (mejoresPistas + altas + bajas).distinct().take(4))
    }

    /** Busca la palabra completa (para que "red" no coincida con "redes sociales" a medias). */
    private fun contiene(texto: String, pista: String): Boolean =
        Regex("(^|[^a-z0-9])${Regex.escape(pista)}([^a-z0-9]|$)").containsMatchIn(texto)

    /** Minusculas y sin tildes: "Contraseña" -> "contrasena". */
    private fun normalizar(s: String): String =
        Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
}
