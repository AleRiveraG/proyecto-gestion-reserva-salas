
package com.example.gestiondereservas.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.ReporteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ReportesScreen(
    onVolver: () -> Unit,
    onEvaluaciones: () -> Unit
) {
    val context = LocalContext.current
    var reportes by remember {
        mutableStateOf<List<ReporteEntity>>(emptyList())
    }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            reportes = withContext(Dispatchers.IO) {
                AppDatabase.getInstance(context)
                    .reporteDao()
                    .obtenerReportes()
            }
        } catch (e: Exception) {
            error = true
        } finally {
            cargando = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextButton(onClick = onVolver) {
            Text("← Volver")
        }

        Text(
            text = "Reportes de inconvenientes",
            style = MaterialTheme.typography.headlineSmall
        )

        TextButton(onClick = onEvaluaciones) {
            Text("Ver evaluaciones de salas →")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            cargando -> CircularProgressIndicator()

            error -> Text("No se pudieron cargar los reportes")

            reportes.isEmpty() -> Text(
                "No hay inconvenientes reportados"
            )

            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reportes) { reporte ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Sala ${reporte.salaId}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text("Reserva N.º ${reporte.reservaId}")

                            Text(
                                "${reporte.fechaReporte} · ${reporte.horaReporte}"
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = reporte.descripcion,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
