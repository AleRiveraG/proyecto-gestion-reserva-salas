package com.example.gestiondereservas

import com.example.gestiondereservas.data.remote.AuditoriaApiService
import com.example.gestiondereservas.data.remote.dto.AuditoriaDto
import com.example.gestiondereservas.data.remote.dto.AuditoriaResponse
import com.example.gestiondereservas.data.repository.AuditoriaRepository
import com.example.gestiondereservas.data.room.entidades.TipoAuditoria
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime

class AuditoriaRepositoryTest {

    private lateinit var auditoriaRepository: AuditoriaRepository
    private lateinit var apiAuditoria: AuditoriaApiService

    @BeforeEach
    fun setUp() {
        apiAuditoria = mockk()
        auditoriaRepository = AuditoriaRepository(apiAuditoria)
    }

    @Test
    fun `registrar auditoria llama a la api correctamente`() = runTest {

        val auditoria = AuditoriaDto(
            accion = "Se creo una reserva",
            detalle = "Se solicito una reserva para la sala LC12",
            fecha = LocalDate.of(2026, 11, 24),
            hora = LocalTime.of(23, 30),
            tipo = TipoAuditoria.RESERVA.name
        )

        coEvery {
            apiAuditoria.registrarAuditoria(auditoria)
        } returns AuditoriaResponse(
            auditoria.accion,
            auditoria.detalle,
            auditoria.fecha,
            auditoria.hora,
            auditoria.tipo
        )


        auditoriaRepository.registrarAuditoria(auditoria)

        coVerify(exactly = 1) {
            apiAuditoria.registrarAuditoria(auditoria)
        }
    }

    @Test
    fun `consultar todas las auditorias`() = runTest {

        val auditoriasR = listOf(
            AuditoriaResponse(
                accion = "Se creo una reserva",
                detalle = "Se solicito una reserva para la sala LC12",
                fecha = LocalDate.of(2026, 11, 24),
                hora = LocalTime.of(23, 30),
                tipo = TipoAuditoria.RESERVA.name
            ),
            AuditoriaResponse(
                accion = "Se creo una sala",
                detalle = "Se creo la sala LC5",
                fecha = LocalDate.of(2025, 11, 24),
                hora = LocalTime.of(23, 30),
                tipo = TipoAuditoria.SALAS.name
            )
        )

        coEvery {
            apiAuditoria.obtenerAuditorias()
        } returns auditoriasR

        val auditorias = auditoriaRepository.obtenerTodas()

        assertEquals(auditoriasR, auditorias)

        coVerify(exactly = 1) {
            apiAuditoria.obtenerAuditorias()
        }
    }

    @Test
    fun `consulta de auditorias por tipo`() = runTest {
        val auditoriasR = listOf(
            AuditoriaResponse(
                accion = "Se creo una reserva",
                detalle = "Se solicito una reserva para la sala LC12",
                fecha = LocalDate.of(2026, 11, 24),
                hora = LocalTime.of(23, 30),
                tipo = TipoAuditoria.RESERVA.name
            ),
            AuditoriaResponse(
                accion = "Se creo una reserva",
                detalle = "Se solicito una reserva para la sala LC5",
                fecha = LocalDate.of(2025, 11, 24),
                hora = LocalTime.of(23, 30),
                tipo = TipoAuditoria.RESERVA.name
            )
        )

        coEvery {
            apiAuditoria.obtenerPorTipo(TipoAuditoria.RESERVA.name)
        } returns auditoriasR

        val auditorias = auditoriaRepository.obtenerPorTipo(TipoAuditoria.RESERVA.name)

        assertEquals(auditoriasR, auditorias)

        coVerify(exactly = 1) {
            apiAuditoria.obtenerPorTipo(TipoAuditoria.RESERVA.name)
        }
    }

    @Test
    fun `consulta de auditorias por fecha`() = runTest {
        val auditoriasR = listOf(
            AuditoriaResponse(
                accion = "Se creo una reserva",
                detalle = "Se solicito una reserva para la sala LC12",
                fecha = LocalDate.of(2026, 11, 24),
                hora = LocalTime.of(23, 30),
                tipo = TipoAuditoria.RESERVA.name
            )
        )

        coEvery {
            apiAuditoria.obtenerPorFecha(LocalDate.of(2026,11,24))
        } returns auditoriasR

        val auditorias = auditoriaRepository.obtenerPorFecha(LocalDate.of(2026,11,24))

        assertEquals(auditoriasR, auditorias)

        coVerify(exactly = 1) {
            apiAuditoria.obtenerPorFecha(LocalDate.of(2026,11,24))
        }
    }
}