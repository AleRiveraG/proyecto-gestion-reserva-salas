
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
import com.example.gestiondereservas.data.room.entidades.EvaluacionSalaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EvaluacionesSalaScreen(
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var evaluaciones by remember {
        mutableStateOf<List<EvaluacionSalaEntity>>(emptyList())
    }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            evaluaciones = withContext(Dispatchers.IO) {
                AppDatabase.getInstance(context)
                    .evaluacionSalaDao()
                    .obtenerTodas()
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
            text = "Evaluaciones de salas",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            cargando -> CircularProgressIndicator()

            error -> Text("No se pudieron cargar las evaluaciones")

            evaluaciones.isEmpty() ->
                Text("Todavía no hay evaluaciones registradas")

            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(evaluaciones) { evaluacion ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Sala ${evaluacion.salaId}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text("Reserva N.º ${evaluacion.reservaId}")

                            Text(
                                text = "★".repeat(evaluacion.calificacion) +
                                        "☆".repeat(5 - evaluacion.calificacion)
                            )

                            Text("Calificación: ${evaluacion.calificacion}/5")

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = evaluacion.observaciones.ifBlank {
                                    "Sin observaciones"
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = evaluacion.fechaEvaluacion.take(16)
                                    .replace("T", " ")
                            )
                        }
                    }
                }
            }
        }
    }
}
