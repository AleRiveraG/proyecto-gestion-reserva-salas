
package com.example.gestiondereservas.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

// Ejecuta un recordatorio programado en segundo plano.
class RecordatorioWorker(
    context: Context,
    parametros: WorkerParameters
) : Worker(context, parametros) {


    override fun doWork(): Result {

        val titulo = inputData.getString("titulo")
            ?: "Recordatorio de reserva"

        val mensaje = inputData.getString("mensaje")
            ?: "Tienes una actividad programada"

        // Comprobar el permiso en Android 13 o superior.
        val tienePermiso =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

        if (tienePermiso) {
            try {
                NotificacionHelper.mostrarNotificacion(
                    applicationContext,
                    titulo,
                    mensaje
                )
            } catch (e: SecurityException) {
                // El permiso pudo cambiar antes de mostrar el aviso.
                return Result.failure()
            }
        } else {
            // No se puede mostrar el aviso sin autorización.
            return Result.failure()
        }

        return Result.success()
    }

}
