package com.example.gestiondereservas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.repository.SalaRepository
import com.example.gestiondereservas.data.room.entidades.Sala
import com.example.gestiondereservas.data.room.entidades.SalaConEquipamiento
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SalasUiState(
    val busqueda: String = "",
    val salas: List<SalaConEquipamiento> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class SalasViewModel(
    private val repositorio: SalaRepository
) : ViewModel() {

    private val busqueda = MutableStateFlow("")

    init {
        viewModelScope.launch { repositorio.sembrarSiVacio() }
    }

    val uiState: StateFlow<SalasUiState> = busqueda
        .flatMapLatest { texto ->
            repositorio.observarTodas(texto).map { SalasUiState(texto, it) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SalasUiState())

    fun onBusquedaChange(texto: String) {
        busqueda.value = texto
    }

    fun alternarEstado(sala: Sala) {
        viewModelScope.launch { repositorio.cambiarEstado(sala.id, !sala.activa) }
    }
}
