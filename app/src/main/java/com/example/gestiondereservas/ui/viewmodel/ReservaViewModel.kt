package com.example.gestiondereservas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.repository.ReservaRepository
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import com.example.gestiondereservas.utils.StringALocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class ReservaViewModel(
    private val repository: ReservaRepository
): ViewModel() {

    private val _sala = MutableStateFlow<SalaEntity?>(null)
    val sala: StateFlow<SalaEntity?> = _sala.asStateFlow()
    private val _listasalas = MutableStateFlow<List<SalaEntity>>(emptyList())
    val listaSalas: StateFlow<List<SalaEntity>> = _listasalas.asStateFlow()
    private val _reservas = MutableStateFlow<List<ReservaEntity>>(emptyList())
    val reservas: StateFlow<List<ReservaEntity>> = _reservas

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
            _listasalas.value = repository.obtenerSalas()
        }
    }

    fun buscarSala(id: Long){
        viewModelScope.launch {
            _sala.value = repository.obtenerSalaPorId(id)
        }
    }


    fun buscarReservaPorId(id:Long) {
        viewModelScope.launch {
            val reservasUsuario = repository.obtenerReservaPorIdUsuario(id)
            val reservas = mutableListOf<ReservaEntity>()

            reservasUsuario.forEach { reserva ->
                if(StringALocalDate(reserva.fechaActividad) >= LocalDate.now()) {
                    reservas.add(reserva)
                }
            }
            _reservas.value = reservas
        }
    }

}