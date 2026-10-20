
package com.example.gestiondereservas.ui.views


import android.R.attr.enabled
import android.widget.Toast
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestiondereservas.data.AppState
import com.example.gestiondereservas.ui.viewmodel.EstadoActividad
import com.example.gestiondereservas.ui.viewmodel.UsoSalaViewModel
import com.example.gestiondereservas.ui.theme.Black
import com.example.gestiondereservas.ui.theme.White
import com.example.gestiondereservas.ui.theme.Yellow
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.gestiondereservas.data.room.entidades.EstadoReserva
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.ReporteEntity
import java.time.LocalDateTime
import androidx.compose.runtime.rememberCoroutineScope
import com.example.gestiondereservas.data.room.entidades.EvaluacionSalaEntity


@Composable
fun UsoSalaScreen(
    reservaId : Long,
    appState: AppState,
    onReservaCancelada : () -> Unit ,
    viewModel: UsoSalaViewModel = viewModel()
) {

    val estadoActividad = viewModel.estadoActividad

    var mostrarReporte by remember { mutableStateOf(false) }

    var mostrarCancelarReserva by remember { mutableStateOf(false) }

    var cancelandoReserva by remember { mutableStateOf(false) }

    var descripcionReporte by remember { mutableStateOf("") }

    val context = LocalContext.current

    var reporteGuardado by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val reporteDao = remember(context) {
        AppDatabase.getInstance(context).reporteDao()
    }

    val usoSalaDao = remember(context) {
        AppDatabase.getInstance(context).usoSalaDao()
    }
    var enviandoReporte by remember { mutableStateOf(false) }

    var errorReporte by remember { mutableStateOf<String?>(null) }

    var mostrarEvaluacion by remember { mutableStateOf(false) }

    var calificacion by remember { mutableIntStateOf(0) }

    var observaciones by remember { mutableStateOf("") }

    var evaluacionGuardada by remember(reservaId) { mutableStateOf(false) }

    var guardandoEvaluacion by remember { mutableStateOf(false) }

    var errorEvaluacion by remember { mutableStateOf<String?>(null) }

    val evaluacionDao = remember(context) {
        AppDatabase.getInstance(context).evaluacionSalaDao()
    }

    val horaTermino = viewModel.finReserva.format(
        DateTimeFormatter.ofPattern("HH:mm")
    )

  // Duración total de la reserva registrada calculando el tiempo entre el incio y fin de reserva
    val duracionTotal = java.time.Duration.between(
        viewModel.inicioReserva,
        viewModel.finReserva
    ).seconds.coerceAtLeast(1)


    val horasDuracion = duracionTotal / 3600
    val minutosDuracion = (duracionTotal % 3600) / 60

    val duracionFormateada = if (horasDuracion > 0) {
        "${horasDuracion} h ${minutosDuracion} min"
    } else {
        "${minutosDuracion} min"
    }


   // Durante la actividad: tiempo real hasta finalizar la reserva
    val tiempoRestante = if (
        estadoActividad == EstadoActividad.PENDIENTE
    ) {
        duracionTotal
    } else {
        viewModel.segundosRestantes
    }
    val horas = tiempoRestante / 3600
    val minutos = (tiempoRestante % 3600) / 60
    val segundos = tiempoRestante % 60

    val tiempoFormateado = if (horas > 0) {
        "%02d:%02d:%02d".format(horas, minutos, segundos)
    } else {
        "%02d:%02d".format(minutos, segundos)
    }

    // Porcentaje de tiempo transcurrido de la reserva
    val progreso = (
                tiempoRestante.toFloat()/duracionTotal
            ).coerceIn(0f, 1f)


    val actividadEnCurso =
        estadoActividad == EstadoActividad.EN_CURSO


    val colorBoton =
        if (actividadEnCurso) Color(0xFFD50032) else Yellow

    var reservaAprobada by remember(reservaId) { mutableStateOf(false) }

    var cargando by remember(reservaId) {
        mutableStateOf(true)
    }

    val reservaDao = remember(context) {
        AppDatabase.getInstance(context).reservaDao()
    }

    LaunchedEffect(reservaId) {
        cargando = true
        reservaAprobada = false

        try {
            val reserva = appState.consultarReservaPorId(reservaId)

            val usoGuardado = withContext(Dispatchers.IO) {
                usoSalaDao.obtenerPorReserva(reservaId)
            }

            val evaluacionGuardadaRoom = withContext(Dispatchers.IO) {
                evaluacionDao.obtenerPorReserva(reservaId)
            }

            if (reserva != null && reserva.estado == EstadoReserva.APROBADA) {
                viewModel.cargarReserva(reserva)
                viewModel.cargarEstadoGuardado(usoSalaDao, reservaId)

                val reservaActualizada = withContext(Dispatchers.IO) {
                    reservaDao.obtenerReservaPorId(reservaId)
                }

                if (reservaActualizada?.estado != EstadoReserva.APROBADA) {
                    reservaAprobada = false
                    return@LaunchedEffect
                }

                val evaluacionExistente = withContext(Dispatchers.IO) {
                    evaluacionDao.obtenerPorReserva(reservaId)
                }

                evaluacionGuardada = evaluacionExistente != null

                reservaAprobada = true
            }
        } finally {
            cargando = false
        }
    }

    if (cargando) {
        Text("Cargando actividad...")
        return
    }

    if (!reservaAprobada) {
        Text("No hay una reserva aprobada para mostrar")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            color = Color(0xFFF5F5F5),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = viewModel.salaId?.let { "Sala $it" } ?: "Sala no disponible",
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
                style = MaterialTheme.typography.labelMedium,
                color = Black
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = when (estadoActividad) {
                EstadoActividad.PENDIENTE -> "PRÓXIMA ACTIVIDAD"
                EstadoActividad.EN_CURSO -> "ACTIVIDAD EN CURSO"
                EstadoActividad.FINALIZADA -> "ACTIVIDAD FINALIZADA"
            },
            style = MaterialTheme.typography.labelMedium,
            color = Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = viewModel.nombreActividad,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Círculo de progreso
        Box(
            modifier = Modifier.size(230.dp),
            contentAlignment = Alignment.Center
        ) {


            CircularProgressIndicator(
                progress = { progreso },
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = -1f
                    },
                color = if (actividadEnCurso) Color(0xFF4CAF17) else Yellow,
                trackColor = if (actividadEnCurso) {
                    Color(0xFFE8E8E8)
                } else {
                    Color(0xFFB57C00)
                },
                strokeWidth = 8.dp
            )


            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (actividadEnCurso)
                        "Tiempo restante"
                    else
                        "Duración de la reserva",
                    style = MaterialTheme.typography.bodySmall,
                    color = Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = tiempoFormateado,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (actividadEnCurso) {
                        "Finaliza a las $horaTermino"
                    } else {
                        "Duración estimada: $duracionFormateada"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Black,
                    textAlign = TextAlign.Center
                )
            }


            if (mostrarReporte) {
                AlertDialog(
                    onDismissRequest = {
                        if (!enviandoReporte) mostrarReporte = false
                    },
                    containerColor = White,
                    shape = RoundedCornerShape(16.dp),

                    title = {
                        Text(
                            text = "Reportar inconveniente",
                            color = Black,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },

                    text = {
                        Column {
                            Text(
                                text = "Describe el problema ocurrido durante el uso de la sala.",
                                color = Black
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = descripcionReporte,
                                onValueChange = {
                                    descripcionReporte = it
                                    errorReporte = null
                                },
                                label = {
                                    Text("Descripción del problema")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                enabled = !enviandoReporte,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Yellow,
                                    focusedLabelColor = Black,
                                    cursorColor = Black
                                )
                            )

                            errorReporte?.let { mensaje ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = mensaje,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    },

                    confirmButton = {
                        Button(
                            onClick = {
                                val idReserva = viewModel.reservaId
                                val idSala = viewModel.salaId

                                if (idReserva != null &&
                                    idReserva > 0L &&
                                    idSala != null
                                ) {
                                    scope.launch {
                                        enviandoReporte = true
                                        errorReporte = null

                                        try {
                                            val ahora = LocalDateTime.now()

                                            val reporte = ReporteEntity(
                                                reservaId = idReserva,
                                                salaId = idSala,
                                                descripcion = descripcionReporte.trim(),
                                                fechaReporte = ahora.toLocalDate().toString(),
                                                horaReporte = ahora.toLocalTime()
                                                    .format(DateTimeFormatter.ofPattern("HH:mm"))
                                            )

                                            withContext(Dispatchers.IO) {
                                                reporteDao.guardarReporte(reporte)
                                            }

                                            descripcionReporte = ""
                                            mostrarReporte = false
                                            reporteGuardado = true
                                            Toast.makeText(
                                                context,
                                                "Reporte enviado correctamente",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        } catch (e: Exception) {
                                            errorReporte = "No se pudo guardar el reporte"
                                        } finally {
                                            enviandoReporte = false
                                        }
                                    }
                                } else {
                                    errorReporte = "No hay una reserva cargada para este reporte"
                                }
                            },
                            enabled = descripcionReporte.isNotBlank() && !enviandoReporte,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Yellow,
                                contentColor = Black
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                if (enviandoReporte) "Guardando..." else "Enviar reporte"
                            )
                        }
                    },

                    dismissButton = {
                        TextButton(
                            onClick = {
                                mostrarReporte = false
                            },
                            enabled = !enviandoReporte
                        ) {
                            Text(
                                text = "Cancelar",
                                color = Black
                            )
                        }
                    }
                )
            }


        }

        Spacer(modifier = Modifier.weight(1f))

        viewModel.errorActividad?.let { mensaje ->
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))
        }


        if (mostrarEvaluacion) {
            AlertDialog(
                onDismissRequest = {
                    if (!guardandoEvaluacion) mostrarEvaluacion = false
                },
                containerColor = White,
                shape = RoundedCornerShape(16.dp),
                titleContentColor = Black,
                textContentColor = Black,
                title = {
                    Text(
                        text = "Evaluar sala",
                        color = Black,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge )
                },
                text = {
                    Column {
                        Text("¿Cómo estaban las condiciones de la sala?")

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (estrella in 1..5) {
                                TextButton(
                                    onClick = { calificacion = estrella }
                                ) {
                                    Text(
                                        text = if (estrella <= calificacion) "★" else "☆",
                                        color = Yellow,
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = observaciones,
                            onValueChange = { observaciones = it },
                            label = { Text("Observaciones (opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Yellow,
                                focusedLabelColor = Black,
                                cursorColor = Black
                             )
                        )

                        errorEvaluacion?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        enabled = calificacion in 1..5 && !guardandoEvaluacion,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Yellow,
                            contentColor = Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        onClick = {
                            val idSala = viewModel.salaId

                            if (idSala != null) {
                                scope.launch {
                                    guardandoEvaluacion = true
                                    errorEvaluacion = null

                                    try {
                                        val evaluacion = EvaluacionSalaEntity(
                                            reservaId = reservaId,
                                            salaId = idSala,
                                            calificacion = calificacion,
                                            observaciones = observaciones.trim(),
                                            fechaEvaluacion = LocalDateTime.now().toString()
                                        )

                                        withContext(Dispatchers.IO) {
                                            evaluacionDao.guardar(evaluacion)
                                        }

                                        evaluacionGuardada = true
                                        mostrarEvaluacion = false

                                    } catch (e: Exception) {
                                        errorEvaluacion = "No se pudo guardar la evaluación"
                                    } finally {
                                        guardandoEvaluacion = false
                                    }
                                }
                            } else {
                                errorEvaluacion = "No se encontró la sala"
                            }
                        }
                    ) {
                        Text(if (guardandoEvaluacion) "Guardando..." else "Enviar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { mostrarEvaluacion = false },
                        enabled = !guardandoEvaluacion,
                        colors = ButtonDefaults.textButtonColors(contentColor = Black)
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }


        if (mostrarCancelarReserva) {
            AlertDialog(
                onDismissRequest = {
                    if (!cancelandoReserva) mostrarCancelarReserva = false
                },
                containerColor = White,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Text(
                        "Cancelar reserva",
                        color = Black,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "¿Estás seguro de que deseas cancelar esta reserva?",
                        color = Black
                    )
                },
                confirmButton = {
                    Button(
                        enabled = !cancelandoReserva,
                        onClick = {
                            scope.launch {
                                cancelandoReserva = true
                                try {
                                    val canceladas = withContext(Dispatchers.IO) {
                                        val reserva = reservaDao.obtenerReservaPorId(reservaId)

                                        if (reserva != null &&
                                            LocalDateTime.now().isBefore(
                                                viewModel.inicioReserva
                                            ) &&
                                            usoSalaDao.obtenerPorReserva(reservaId)?.estado != "EN_CURSO"
                                        ) {
                                            reservaDao.cancelarReservaAprobada(reservaId)
                                        } else {
                                            0
                                        }
                                    }

                                    mostrarCancelarReserva = false

                                    if (canceladas > 0) {
                                        Toast.makeText(
                                            context,
                                            "Reserva cancelada correctamente",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        onReservaCancelada()

                                    } else {
                                        Toast.makeText(
                                            context,
                                            "La reserva ya no se puede cancelar",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Error al cancelar la reserva",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } finally {
                                    cancelandoReserva = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Yellow,
                            contentColor = Black
                        )
                    ) {
                        Text(if (cancelandoReserva) "Cancelando..." else "Sí, cancelar")
                    }
                },
                dismissButton = {
                    TextButton(
                        enabled = !cancelandoReserva,
                        onClick = { mostrarCancelarReserva = false }
                    ) {
                        Text("Volver", color = Black)
                    }
                }
            )
        }

        when (estadoActividad) {

            EstadoActividad.PENDIENTE -> {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.registrarInicio(
                                AppDatabase.getInstance(context),
                                context
                            )
                        }
                    },
                    enabled = reservaAprobada,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Yellow,
                        contentColor = Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Inicio de Actividad")
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        mostrarCancelarReserva = true
                    },
                    enabled = LocalDateTime.now().isBefore(viewModel.inicioReserva),
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Black),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancelar Reserva")
                }
            }

            EstadoActividad.EN_CURSO -> {

                Button(
                    onClick = {
                        scope.launch {
                            viewModel.registrarFinalizacion(usoSalaDao)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorBoton,
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Finalizar actividad")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {mostrarReporte = true},
                    enabled = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp,Black),
                    colors = ButtonDefaults.outlinedButtonColors(disabledContentColor = Black)
                ) {
                    Text("Reportar inconveniente")
                }
            }

            EstadoActividad.FINALIZADA -> {
                Text(
                    text = "Actividad finalizada correctamente",
                    color = Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (evaluacionGuardada) {
                    Text(
                        text = "Evaluación registrada. ¡Gracias!",
                        color = Black
                    )
                } else {
                    Button(
                        onClick = { mostrarEvaluacion = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Yellow,
                            contentColor = Black
                        )
                    ) {
                        Text("Evaluar sala")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
