package com.example.gestiondereservas

import com.example.gestiondereservas.data.remote.HorarioApiService
import com.example.gestiondereservas.data.remote.ReservaApiService
import com.example.gestiondereservas.data.remote.SalaApiService
import com.example.gestiondereservas.data.remote.dto.ReservaResponse
import com.example.gestiondereservas.data.remote.dto.SalaResponse
import com.example.gestiondereservas.data.repository.ReservaRepository
import com.example.gestiondereservas.data.room.entidades.EstadoReserva
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ReservaRepositoryTest {
    private lateinit var salaApi: SalaApiService
    private lateinit var api: ReservaApiService
    private lateinit var horarioApi: HorarioApiService
    private lateinit var reservaRepository: ReservaRepository

    @BeforeEach
    fun setup() {
        salaApi = mockk()
        api = mockk()
        horarioApi = mockk()

        reservaRepository = ReservaRepository(
            salaApi,
            api,
            horarioApi
        )

    }

    @Test
    fun `registrar reserva retorna respuesta de la API`() = runTest {
        val reserva = mockk<RequestBody>()
        val archivo = mockk<MultipartBody.Part>()
        val respuesta = mockk<ReservaResponse>()

        coEvery {
            api.registrarReserva(reserva, archivo)
        } returns respuesta

        val resultado = reservaRepository.registrarReserva(reserva, archivo)

        assertEquals(respuesta, resultado)
        coVerify(exactly = 1) {
            api.registrarReserva(reserva, archivo)
        }
    }

    @Test
    fun `modificar estado llama a la API`() = runTest {
        val id = 1L
        val estadoNuevo = EstadoReserva.APROBADA
        val estadoActual = EstadoReserva.PENDIENTE

        coEvery {
            api.modificarEstadoReserva(id, estadoNuevo, estadoActual)
        } just Runs

        reservaRepository.modificarEstado(id, estadoNuevo, estadoActual)

        coVerify(exactly = 1) {
            api.modificarEstadoReserva(id, estadoNuevo, estadoActual)
        }
    }

    @Test
    fun `obtener reservas pendientes retorna reservas pendientes (funciona igual para aprobadas)`() = runTest {
        val reservas = listOf(
            mockk<ReservaResponse>(),
            mockk<ReservaResponse>()
        )

        coEvery {
            api.obtenerReservasPorEstado(EstadoReserva.PENDIENTE)
        } returns reservas

        val resultado = reservaRepository.obtenerReservasPendientes()

        assertEquals(reservas, resultado)

        coVerify(exactly = 1) {
            api.obtenerReservasPorEstado(EstadoReserva.PENDIENTE)
        }
    }

    @Test
    fun `obtener aprobadas retorna cantidad de reservas (funciona igual para los otros estados)`() = runTest {
        val cantidad = 5L

        coEvery {
            api.obtenerCantidadPorEstado(EstadoReserva.APROBADA)
        } returns cantidad

        val resultado = reservaRepository.obtenerAprobadas()

        assertEquals(cantidad, resultado)

        coVerify(exactly = 1) {
            api.obtenerCantidadPorEstado(EstadoReserva.APROBADA)
        }
    }

    @Test
    fun `obtener salas retorna lista de salas`() = runTest {
        val salas = listOf(mockk<SalaResponse>())

        coEvery { salaApi.listar() } returns salas

        val resultado = reservaRepository.obtenerSalas()

        assertEquals(salas, resultado)

        coVerify(exactly = 1) { salaApi.listar() }
    }

    @Test
    fun `obtener sala por id retorna sala`() = runTest {
        val id = 1L
        val sala = mockk<SalaResponse>()

        coEvery { salaApi.obtener(id) } returns sala

        val resultado = reservaRepository.obtenerSalaPorId(id)

        assertEquals(sala, resultado)

        coVerify(exactly = 1) { salaApi.obtener(id) }
    }
}