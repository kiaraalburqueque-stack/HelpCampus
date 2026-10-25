package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.ui.components.graficos.DatoGrafico
import com.tecsup.ayudacampus.ui.theme.VioletaPrimario
import com.tecsup.ayudacampus.ui.theme.VioletaSuave
import com.tecsup.ayudacampus.ui.theme.colorEstado
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Panel de Soporte. Todo viene de Room como Flow, asi que si un estudiante
 * crea un ticket, los contadores y los graficos cambian solos.
 */
class PanelViewModel(repo: AyudaRepository) : ViewModel() {

    /** Atajo: convierte un Flow en StateFlow ligado al ViewModel. */
    private fun <T> estado(flujo: Flow<T>, inicial: T): StateFlow<T> =
        flujo.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), inicial)

    private val flujoAbiertos = repo.observarCantidadPorEstado(EstadoTicket.ABIERTO)
    private val flujoEnProceso = repo.observarCantidadPorEstado(EstadoTicket.EN_PROCESO)
    private val flujoResueltos = repo.observarCantidadPorEstado(EstadoTicket.RESUELTO)
    private val flujoCerrados = repo.observarCantidadPorEstado(EstadoTicket.CERRADO)

    val abiertos: StateFlow<Int> = estado(flujoAbiertos, 0)
    val enProceso: StateFlow<Int> = estado(flujoEnProceso, 0)
    val resueltos: StateFlow<Int> = estado(flujoResueltos, 0)

    /** Convierte los conteos de Room al formato que entiende GraficoDona. */
    val porEstado: StateFlow<List<DatoGrafico>> = estado(
        combine(flujoAbiertos, flujoEnProceso, flujoResueltos, flujoCerrados) { a, p, r, c ->
            listOf(
                DatoGrafico(EstadoTicket.ABIERTO.etiqueta, a.toFloat(), colorEstado(EstadoTicket.ABIERTO)),
                DatoGrafico(EstadoTicket.EN_PROCESO.etiqueta, p.toFloat(), colorEstado(EstadoTicket.EN_PROCESO)),
                DatoGrafico(EstadoTicket.RESUELTO.etiqueta, r.toFloat(), colorEstado(EstadoTicket.RESUELTO)),
                DatoGrafico(EstadoTicket.CERRADO.etiqueta, c.toFloat(), colorEstado(EstadoTicket.CERRADO))
            )
        },
        emptyList()
    )

    /** Convierte el LEFT JOIN + COUNT por categoria al formato de GraficoBarras (la mayor resalta). */
    val porCategoria: StateFlow<List<DatoGrafico>> = estado(
        repo.observarCantidadPorCategoria().map { lista ->
            val maximo = lista.maxOfOrNull { it.cantidad } ?: 0
            lista.map {
                DatoGrafico(
                    etiqueta = it.nombre.substringBefore(" "),
                    valor = it.cantidad.toFloat(),
                    color = if (maximo > 0 && it.cantidad == maximo) VioletaPrimario else VioletaSuave
                )
            }
        },
        emptyList()
    )

    /** Tickets ALTA en estado ABIERTO creados hace mas de 1 hora (limite = ahora - 1 hora). */
    val altasSinAtender: StateFlow<Int> = estado(
        repo.observarAltasSinAtender(System.currentTimeMillis() - 60 * 60 * 1000L),
        0
    )
}
