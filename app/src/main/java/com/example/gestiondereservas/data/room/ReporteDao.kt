
package com.example.gestiondereservas.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gestiondereservas.data.room.entidades.ReporteEntity

@Dao
interface ReporteDao {

    @Insert
    suspend fun guardarReporte(reporte: ReporteEntity)

    @Query("SELECT * FROM reporte ORDER BY id DESC")
    suspend fun obtenerReportes(): List<ReporteEntity>

    @Query("SELECT * FROM reporte WHERE id = :reporteId")
    suspend fun obtenerReportePorId(reporteId: Long): ReporteEntity?
}
