package com.example.gestiondereservas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.remote.dto.ReservaResponse
import com.example.gestiondereservas.data.remote.dto.SalaResponse
import com.example.gestiondereservas.data.repository.ReservaRepository
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import com.example.gestiondereservas.utils.StringALocalDate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate

sealed interface ReservaState {

    data object normal : ReservaState

    data object cargando : ReservaState

    data class exito(
        val reserva: ReservaResponse
    ): ReservaState

    data class error(
        val mensaje: String
    ): ReservaState
}


class ReservaViewModel(
    private val repository: ReservaRepository
): ViewModel() {

    private val _sala = MutableStateFlow<SalaResponse?>(null)
    val sala: StateFlow<SalaResponse?> = _sala.asStateFlow()
    private val _listasalas = MutableStateFlow<List<SalaResponse>>(emptyList())
    val listaSalas: StateFlow<List<SalaResponse>> = _listasalas.asStateFlow()
    private val _reservas = MutableStateFlow<List<ReservaResponse>>(emptyList())
    val reservas: StateFlow<List<ReservaResponse>> = _reservas

    private val _reservaState = MutableStateFlow<ReservaState>(ReservaState.normal)
    val reservaState: StateFlow<ReservaState> = _reservaState.asStateFlow()

    init {
        cargarSalas()
    }

    fun registrarReserva(
        reserva: RequestBody,
        archivo: MultipartBody.Part
    ) {

        _reservaState.value = ReservaState.cargando

        viewModelScope.launch {
            try {

                val respuesta = repository.registrarReserva(reserva, archivo)

                _reservaState.value = ReservaState.exito(respuesta)

            } catch (e: CancellationException) {
                throw  e
            } catch (e: HttpException) {
                _reservaState.value = ReservaState.error("Error: ${e.code()}")
            } catch (e: IOException) {
                _reservaState.value = ReservaState.error("No se pudo conectar al servidor")
            } catch (e: Exception) {
                _reservaState.value = ReservaState.error("Ocurrio un error inesperado")
            }
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

    fun reiniciarEstado() {
        _reservaState.value = ReservaState.normal
    }

}