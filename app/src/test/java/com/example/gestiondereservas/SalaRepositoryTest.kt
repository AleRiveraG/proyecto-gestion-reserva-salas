package com.example.gestiondereservas

import com.example.gestiondereservas.data.remote.SalaApiService
import com.example.gestiondereservas.data.remote.dto.SalaDto
import com.example.gestiondereservas.data.remote.dto.SalaResponse
import com.example.gestiondereservas.data.repository.SalaRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SalaRepositoryTest {
    private lateinit var apiService: SalaApiService
    private lateinit var salaRepository: SalaRepository

    @BeforeEach
    fun setup() {
        apiService = mockk()
        salaRepository = SalaRepository(apiService)
    }

    @Test
    fun `observar todas retorna todas las salas cuando busqueda esta vacia`() = runTest {
        val salas = listOf(
            mockk<SalaResponse>(),
            mockk<SalaResponse>()
        )

        coEvery { apiService.listar() } returns salas

        val resultado = salaRepository.observarTodas("").first()

        assertEquals(salas, resultado)

        coVerify(exactly = 1) {
            apiService.listar()
        }
    }

    @Test
    fun `obtener retorna sala por id`() = runTest {
        val id = 1L
        val sala = mockk<SalaResponse>()

        coEvery {
            apiService.obtener(id)
        } returns sala

        val resultado = salaRepository.obtener(id)

        assertEquals(sala, resultado)

        coVerify(exactly = 1) {
            apiService.obtener(id)
        }
    }

    @Test
    fun `cambiar estado retorna sala actualizada`() = runTest {
        val id = 1L
        val activa = false
        val sala = mockk<SalaResponse>()

        coEvery {
            apiService.cambiarEstado(id, activa)
        } returns sala

        val resultado = salaRepository.cambiarEstado(id, activa)

        assertEquals(sala, resultado)

        coVerify(exactly = 1) {
            apiService.cambiarEstado(id, activa)
        }
    }

    @Test
    fun `guardar retorna sala creada`() = runTest {
        val salaDto = mockk<SalaDto>()
        val salaRespuesta = mockk<SalaResponse>()

        coEvery {
            apiService.crear(salaDto)
        } returns salaRespuesta

        val resultado = salaRepository.guardar(salaDto)

        assertEquals(salaRespuesta, resultado)

        coVerify(exactly = 1) {
            apiService.crear(salaDto)
        }
    }

    @Test
    fun `codigo disponible retorna true cuando codigo esta disponible`() = runTest {
        val codigo = "LAB-101"
        val excluirId = 1L

        coEvery {
            apiService.codigoDisponible(codigo, excluirId)
        } returns true

        val resultado = salaRepository.codigoDisponible(codigo, excluirId)

        assertTrue(resultado)

        coVerify(exactly = 1) {
            apiService.codigoDisponible(codigo, excluirId)
        }
    }

    @Test
    fun `listar salas retorna todas las salas`() = runTest {
        val salas = listOf(
            mockk<SalaResponse>(),
            mockk<SalaResponse>()
        )

        coEvery {
            apiService.listar()
        } returns salas

        val resultado = salaRepository.listarSalas()

        assertEquals(salas, resultado)

        coVerify(exactly = 1) {
            apiService.listar()
        }
    }
}