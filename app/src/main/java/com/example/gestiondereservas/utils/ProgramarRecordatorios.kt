
package com.example.gestiondereservas.utils

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ProgramadorRecordatorios {

    fun programarPrueba(
        context: Context,
        titulo: String,
        mensaje: String
    ) {

        // Datos que recibirá el Worker
        val datos = Data.Builder()
            .putString("titulo", titulo)
            .putString("mensaje", mensaje)
            .build()

        // PRUEBA: ejecutar después de 2 minutos
        val trabajo = OneTimeWorkRequestBuilder<RecordatorioWorker>()
            .setInitialDelay(2, TimeUnit.MINUTES)
            .setInputData(datos)
            .build()

        WorkManager.getInstance(context).enqueue(trabajo)
    }
}
