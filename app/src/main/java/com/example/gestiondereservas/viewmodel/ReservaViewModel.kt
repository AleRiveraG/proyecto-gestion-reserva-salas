package com.example.gestiondereservas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.remote.dto.ReservaResponse
import com.example.gestiondereservas.data.repository.ReservaRepository
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import com.example.gestiondereservas.utils.StringALocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.time.LocalDate

class ReservaViewModel(
    private val repository: ReservaRepository
): ViewModel() {

    private val _sala = MutableStateFlow<SalaEntity?>(null)
    val sala: StateFlow<SalaEntity?> = _sala.asStateFlow()
    private val _listasalas = MutableStateFlow<List<SalaEntity>>(emptyList())
    val listaSalas: StateFlow<List<SalaEntity>> = _listasalas.asStateFlow()
    private val _reservas = MutableStateFlow<List<ReservaResponse>>(emptyList())
    val reservas: StateFlow<List<ReservaResponse>> = _reservas

    init {
        cargarSalas()
    }

    fun registrarReserva(
        reserva: RequestBody,
        archivo: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            repository.registrarReserva(reserva, archivo)
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
            val reservas = mutableListOf<ReservaResponse>()

            reservasUsuario.forEach { reserva ->
                if(reserva.fechaActividad >= LocalDate.now()) {
                    reservas.add(reserva)
                }
            }
            _reservas.value = reservas
        }
    }

}