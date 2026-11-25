package com.example.gestiondereservas.data.repository


import kotlinx.coroutines.flow.Flow
import com.example.gestiondereservas.data.remote.SalaApiService
import com.example.gestiondereservas.data.remote.dto.SalaDto
import com.example.gestiondereservas.data.remote.dto.SalaResponse
import kotlinx.coroutines.flow.flow

class SalaRepository(
    private val apiService: SalaApiService
) {


    fun observarTodas(busqueda: String): Flow<List<SalaResponse>> = flow {
        val termino = busqueda.trim()
        val todas = apiService.listar()
        val resultado = if(termino.isBlank()) {
            todas
        } else {
            todas.filter { sala ->
                sala.codigo.contains(termino, ignoreCase = true) ||
                        sala.nombre.contains(termino, ignoreCase = true)
            }
        }
        emit(resultado)
    }

    suspend fun obtener(id: Long): SalaResponse = apiService.obtener(id)

    suspend fun cambiarEstado(id: Long, activa: Boolean) = apiService.cambiarEstado(id, activa)

    suspend fun guardar(salaEntity: SalaDto): SalaResponse = apiService.crear(salaEntity)

    suspend fun codigoDisponible(codigo: String, excluirId: Long): Boolean =
        apiService.codigoDisponible(codigo.trim(), excluirId)

    suspend fun listarSalas(): List<SalaResponse> {
        return apiService.listar()
    }
}
