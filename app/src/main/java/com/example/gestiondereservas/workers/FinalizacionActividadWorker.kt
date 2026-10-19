package com.example.gestiondereservas.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gestiondereservas.data.room.AppDatabase
import java.time.LocalDateTime

class FinalizacionActividadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val reservaId = inputData.getLong("reservaId", -1L)
        val finProgramado = inputData.getString("finProgramado")
            ?: return Result.failure()

        if (reservaId <= 0L) return Result.failure()

        return try {
            val fin = LocalDateTime.parse(finProgramado)

            if (LocalDateTime.now().isBefore(fin)) {
                return Result.retry()
            }

            val db = AppDatabase.getInstance(applicationContext)

            db.usoSalaDao().finalizar(
                reservaId,
                fin.toString()
            )

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

