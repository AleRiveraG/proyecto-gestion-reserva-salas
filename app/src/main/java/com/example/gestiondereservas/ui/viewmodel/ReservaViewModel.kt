package com.example.gestiondereservas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.repository.ReservaRepository
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservaViewModel(
    private val repository: ReservaRepository
): ViewModel() {

    private val sala = MutableStateFlow<SalaEntity?>(null)
    val salaSeleccionada: StateFlow<SalaEntity?> = sala.asStateFlow()
    private val salas = MutableStateFlow<List<SalaEntity>>(emptyList())
    val listaSalas: StateFlow<List<SalaEntity>> = salas.asStateFlow()

    init {
        cargarSalas()
    }

    fun registrarReserva(reserva: ReservaEntity) {
        viewModelScope.launch {
            repository.registrarReserva(reserva)
        }
    }

    fun cargarSalas() {
        viewModelScope.launch {
            salas.value = repository.obtenerSalas()
        }
    }

    fun buscarSala(id: Long){
        viewModelScope.launch {
            sala.value = repository.obtenerSalaPorId(id)
        }
    }


    //FALTA BUSCAR LA RESERVA POR ID DEL USUARIO

}