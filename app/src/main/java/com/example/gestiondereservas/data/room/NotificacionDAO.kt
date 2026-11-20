
package com.example.gestiondereservas.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gestiondereservas.data.room.entidades.NotificacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificacionDao {

    @Query("""
        SELECT * FROM notificacion
        WHERE esSupervisor = :esSupervisor
        ORDER BY id DESC
    """)
    fun obtenerNotificaciones(
        esSupervisor: Boolean
    ): Flow<List<NotificacionEntity>>

    @Query("""
        SELECT * FROM notificacion
        WHERE esSupervisor = 0
        AND correoDestinatario = :correo
        ORDER BY id DESC
    """)
    fun obtenerNotificacionesRelator(
        correo: String
    ): Flow<List<NotificacionEntity>>

    @Query("""
        SELECT * FROM notificacion
        WHERE esSupervisor = 1
        ORDER BY id DESC
    """)
    fun obtenerNotificacionesSupervisor(): Flow<List<NotificacionEntity>>

    @Insert
    suspend fun guardarNotificacion(
        notificacion: NotificacionEntity
    )

    @Query("""
        UPDATE notificacion SET leida = 1
        WHERE id = :id
        AND esSupervisor = 0
        AND correoDestinatario = :correo
    """)
    suspend fun marcarLeidaRelator(
        id: Long,
        correo: String
    )

    @Query("""
        UPDATE notificacion SET leida = 1
        WHERE id = :id
        AND esSupervisor = 1
    """)
    suspend fun marcarLeidaSupervisor(id: Long)

    @Query("""
        UPDATE notificacion SET leida = 1
        WHERE esSupervisor = 0
        AND correoDestinatario = :correo
    """)
    suspend fun marcarTodasLeidasRelator(
        correo: String
    )

    @Query("""
        UPDATE notificacion SET leida = 1
        WHERE esSupervisor = 1
    """)
    suspend fun marcarTodasLeidasSupervisor()

    // Se conservan por compatibilidad
    @Query("UPDATE notificacion SET leida = 1 WHERE id = :id")
    suspend fun marcarLeida(id: Long)

    @Query("""
        UPDATE notificacion SET leida = 1
        WHERE esSupervisor = :esSupervisor
    """)
    suspend fun marcarTodasLeidas(esSupervisor: Boolean)

    @Query("""
        SELECT COUNT(*) FROM notificacion
        WHERE reservaId = :reservaId
        AND tipo = :tipo
    """)
    suspend fun contarPorReservaYTipo(
        reservaId: Long,
        tipo: String
    ): Int
}
