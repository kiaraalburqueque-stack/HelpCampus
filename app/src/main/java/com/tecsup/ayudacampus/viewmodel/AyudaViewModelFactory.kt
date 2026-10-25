package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.prefs.PreferenciasStore
import com.tecsup.ayudacampus.data.repository.AyudaRepository

/**
 * Le dice a Compose como construir cada ViewModel.
 * Algunos necesitan datos extra: el usuario actual o el id del ticket.
 */
class AyudaViewModelFactory(
    private val repo: AyudaRepository,
    private val prefs: PreferenciasStore,
    private val usuario: Usuario? = null,
    private val ticketId: Int = 0
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(CategoriasViewModel::class.java) -> CategoriasViewModel(repo)
        modelClass.isAssignableFrom(SesionViewModel::class.java) -> SesionViewModel(repo, prefs)
        modelClass.isAssignableFrom(MisTicketsViewModel::class.java) -> MisTicketsViewModel(repo, usuarioId())
        modelClass.isAssignableFrom(NuevoTicketViewModel::class.java) -> NuevoTicketViewModel(repo, prefs, usuarioId())
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    } as T

    private fun usuarioId(): Int = requireNotNull(usuario) { "Este ViewModel necesita un usuario" }.id
}
