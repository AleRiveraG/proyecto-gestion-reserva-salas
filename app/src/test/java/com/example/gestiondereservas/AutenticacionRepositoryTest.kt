package com.example.gestiondereservas

import com.example.gestiondereservas.data.remote.AutenticacionApiService
import com.example.gestiondereservas.data.remote.SessionManager
import com.example.gestiondereservas.data.remote.dto.AuthResponseDto
import com.example.gestiondereservas.data.repository.AutenticacionRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response

class AutenticacionRepositoryTest {

    private lateinit var autenticacionRepository: AutenticacionRepository
    private lateinit var apiService: AutenticacionApiService

    @BeforeEach
    fun setUp() {
        apiService = mockk()
        autenticacionRepository = AutenticacionRepository(apiService)
    }

    @Test
    fun `login exitoso retorna usuario y guarda sesion`() = runTest {

        val correo = "usuario@duocuc.cl"
        val password = "123456"
        val rol = "DOCENTE"

        val respuesta = mockk<AuthResponseDto>(relaxed = true)

        coEvery {
            apiService.login(any())
        } returns Response.success(respuesta)

        mockkObject(SessionManager)

        every {
            SessionManager.guardarSesion(
                token = any(),
                idUsuario = any(),
                nombre = any(),
                correo = any(),
                rol = any(),
                telefono = any()
            )
        } just Runs

        val resultado = autenticacionRepository.login(correo, password, rol)

        assertTrue(resultado.isSuccess)
        assertEquals(respuesta, resultado.getOrNull())

        coVerify(exactly = 1) {
            apiService.login(any())
        }
    }

    @Test
    fun `registro exitoso retorna usuario creado`() = runTest {

        val respuesta = mockk<AuthResponseDto>()

        coEvery {
            apiService.registrar(any())
        } returns Response.success(respuesta)

        val resultado = autenticacionRepository.registrar(
            nombre = "Juan Perez",
            correo = "juan@duocuc.cl",
            password = "123456",
            rol = "DOCENTE",
            telefono = "912345678"
        )

        assertTrue(resultado.isSuccess)
        assertEquals(respuesta, resultado.getOrNull())

        coVerify(exactly = 1) {
            apiService.registrar(any())
        }
    }
}