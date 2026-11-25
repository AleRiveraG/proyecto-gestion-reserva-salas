package com.example.gestiondereservas

import com.example.gestiondereservas.data.remote.HorarioApiService
import com.example.gestiondereservas.data.remote.ReservaApiService
import com.example.gestiondereservas.data.remote.SalaApiService
import com.example.gestiondereservas.data.remote.dto.HorarioDto
import com.example.gestiondereservas.data.remote.dto.HorarioResponse
import com.example.gestiondereservas.data.repository.HorarioRepository
import com.example.gestiondereservas.data.room.UsoSalaDao
import com.example.gestiondereservas.data.room.entidades.DiaSemana
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalTime

class HorarioRepositoryTest {

    private lateinit var horarioRepository: HorarioRepository
    private lateinit var apiService: HorarioApiService
    private lateinit var salaApi: SalaApiService
    private lateinit var reservaApi: ReservaApiService
    private lateinit var usoSalaDao: UsoSalaDao


    @BeforeEach
    fun setUp() {
        apiService = mockk()
        salaApi = mockk()
        reservaApi = mockk()
        usoSalaDao = mockk()
        horarioRepository = HorarioRepository(apiService,
            salaApi,
            reservaApi,
            usoSalaDao)
    }

    @Test
    fun `obtener horarios por sala y dia`() = runTest {

        val salaId = 1L
        val dia = DiaSemana.LUNES

        val horarios = listOf(
            mockk<HorarioResponse>(),
            mockk<HorarioResponse>()
        )

        coEvery {
            apiService.listar(salaId, dia)
        } returns horarios

        val resultado = horarioRepository
            .obtenerHorariosPorSalaYDia(salaId, dia)

        assertEquals(horarios, resultado)

        coVerify(exactly = 1) {
            apiService.listar(salaId, dia)
        }
    }

    @Test
    fun `obtener horario por id`() = runTest {

        val id = 1L
        val horario = mockk<HorarioResponse>()

        coEvery {
            apiService.obtener(id)
        } returns horario

        val resultado = horarioRepository.obtener(id)

        assertEquals(horario, resultado)

        coVerify(exactly = 1) {
            apiService.obtener(id)
        }
    }

    @Test
    fun `cambiar estado retorna horario actualizado`() = runTest {
        val id = 1L
        val activo = false
        val horarioEsperado = mockk<HorarioResponse>()

        coEvery {
            apiService.cambiarEstado(id, activo)
        } returns horarioEsperado

        val resultado = horarioRepository.cambiarEstado(id, activo)

        assertEquals(horarioEsperado, resultado)

        coVerify(exactly = 1) {
            apiService.cambiarEstado(id, activo)
        }
    }

    @Test
    fun `guardar horario`() = runTest {

        val horarioDto = mockk<HorarioDto>()
        val horarioResponse = mockk<HorarioResponse>()

        coEvery {
            apiService.crear(horarioDto)
        } returns horarioResponse

        val resultado = horarioRepository.guardar(horarioDto)

        assertEquals(horarioResponse, resultado)

        coVerify(exactly = 1) {
            apiService.crear(horarioDto)
        }
    }

    @Test
    fun `eliminar horario`() = runTest {

        val id = 1L

        coEvery {
            apiService.eliminar(id)
        } returns Unit

        horarioRepository.eliminar(id)

        coVerify(exactly = 1) {
            apiService.eliminar(id)
        }
    }

    @Test
    fun `hay cruce retorna true cuando los horarios se superponen`() = runTest {

        val existente = mockk<HorarioResponse>()

        every { existente.id } returns 2L
        every { existente.horaInicio } returns LocalTime.of(10, 0)
        every { existente.horaTermino } returns LocalTime.of(12, 0)

        coEvery {
            apiService.listar(1L, DiaSemana.LUNES)
        } returns listOf(existente)

        val resultado = horarioRepository.hayCruce(
            salaId = 1L,
            dia = DiaSemana.LUNES,
            inicioMin = 660,
            finMin = 780,
            excluirId = 0L
        )

        assertTrue(resultado)
    }
}