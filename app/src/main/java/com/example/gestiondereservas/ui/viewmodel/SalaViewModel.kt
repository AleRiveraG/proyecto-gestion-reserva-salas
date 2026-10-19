package com.example.gestiondereservas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.repository.SalaRepository
import com.example.gestiondereservas.data.room.entidades.SalaConEquipamiento
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FiltrosAvanzados(
    val fecha: String = "",
    val hora: String = "",
    val capacidad: String = "",
    val area: String = "",
    val equipamientos: Set<String> = emptySet()
)
data class SalasUiState(
    val busqueda: String = "",
    val filtros: FiltrosAvanzados = FiltrosAvanzados(),
    val salas: List<SalaConEquipamiento> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class SalasViewModel(
    private val repositorio: SalaRepository
) : ViewModel() {

    private val busqueda = MutableStateFlow("")

    private val filtrosAvanzados = MutableStateFlow(FiltrosAvanzados())

    init {
        viewModelScope.launch { repositorio.sembrarSiVacio() }
    }
    val uiState: StateFlow<SalasUiState> = combine(busqueda, filtrosAvanzados) { texto, filtros ->
        Pair(texto, filtros)
    }.flatMapLatest { (texto, filtros) ->

        repositorio.observarTodas(texto).map { listaDesdeBd ->

            val salasFiltradas = listaDesdeBd.filter { salaCompleta ->
                val sala = salaCompleta.salaEntity
                val equipamientosBd = salaCompleta.equipamiento

                val capacidadReq = filtros.capacidad.filter { it.isDigit() }.toIntOrNull() ?: 0
                val cumpleCapacidad = capacidadReq == 0 || sala.capacidad >= capacidadReq

                val cumpleArea = filtros.area.isBlank() ||
                        filtros.area == "Todas las áreas" ||
                        sala.area.nombre == filtros.area

                val cumpleEquipamiento = filtros.equipamientos.isEmpty() ||
                        filtros.equipamientos.all { eqRequerido ->
                            equipamientosBd.any { it.nombre == eqRequerido }
                        }
                cumpleCapacidad && cumpleArea && cumpleEquipamiento
            }

            SalasUiState(busqueda = texto, filtros = filtros, salas = salasFiltradas)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SalasUiState())


    fun onBusquedaChange(texto: String) {
        busqueda.value = texto
    }

    fun alternarEstado(salaEntity: SalaEntity) {
        viewModelScope.launch { repositorio.cambiarEstado(salaEntity.id, !salaEntity.activa) }
    }

    fun actualizarFiltros(
        fecha: String = filtrosAvanzados.value.fecha,
        hora: String = filtrosAvanzados.value.hora,
        capacidad: String = filtrosAvanzados.value.capacidad,
        area: String = filtrosAvanzados.value.area,
        equipamientos: Set<String> = filtrosAvanzados.value.equipamientos
    ) {
        filtrosAvanzados.update {
            it.copy(
                fecha = fecha,
                hora = hora,
                capacidad = capacidad,
                area = area,
                equipamientos = equipamientos
            )
        }
    }

}