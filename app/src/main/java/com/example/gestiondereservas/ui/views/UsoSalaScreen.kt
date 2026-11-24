package com.example.gestiondereservas.ui.views

import android.widget.Toast
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestiondereservas.data.AppState
import com.example.gestiondereservas.viewmodel.EstadoActividad
import com.example.gestiondereservas.viewmodel.UsoSalaViewModel
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
import androidx.compose.ui.Alignment
import com.example.gestiondereservas.data.room.entidades.EvaluacionSalaEntity
import com.example.gestiondereservas.utils.AnimacionCalendarioPendiente
import com.example.gestiondereservas.utils.AnimacionConfetti
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gestiondereservas.data.remote.RetrofitClient
import com.example.gestiondereservas.data.repository.ReservaRepository
import com.example.gestiondereservas.viewmodel.ReservaViewModel
import kotlinx.coroutines.delay

@Composable
fun rememberUsoSalaViewModel(): UsoSalaViewModel {
    return viewModel(
        factory = viewModelFactory {
            initializer {
                val repository = ReservaRepository(RetrofitClient.apiSalas, RetrofitClient.api,
                    RetrofitClient.apiHorarios)
                UsoSalaViewModel(repository)
            }
        }
    )
}
@Composable
fun UsoSalaScreen(
    reservaId : Long,
    onReservaCancelada : () -> Unit ,
    viewModel: UsoSalaViewModel = rememberUsoSalaViewModel(),
    reservaRepository: ReservaRepository
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

    val horaInicio = viewModel.inicioReserva.format(
        DateTimeFormatter.ofPattern("HH:mm")
    )

    val fechaActividad = viewModel.inicioReserva.format(
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
    )

    val textoEstado = when (estadoActividad) {
        EstadoActividad.PENDIENTE -> "Pendiente"
        EstadoActividad.EN_CURSO -> "En curso"
        EstadoActividad.FINALIZADA -> "Finalizada"
    }

    val colorEstado = when (estadoActividad) {
        EstadoActividad.PENDIENTE -> Yellow
        EstadoActividad.EN_CURSO -> Color(0xFF2E7D32)
        EstadoActividad.FINALIZADA -> Color(0xFF616161)
    }

    val colorTextoEstado = when (estadoActividad) {
        EstadoActividad.PENDIENTE -> Black
        else -> White
    }

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

    var reloj by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(estadoActividad) {
        while (estadoActividad == EstadoActividad.PENDIENTE) {
            reloj = LocalDateTime.now()
            delay(1000)
        }
    }

    val segundosParaEmpezar = java.time.Duration.between(
        reloj,
        viewModel.inicioReserva
    ).seconds.coerceAtLeast(0)

    val tiempoVisible = when (estadoActividad) {
        EstadoActividad.PENDIENTE -> segundosParaEmpezar
        EstadoActividad.EN_CURSO -> viewModel.segundosRestantes
        EstadoActividad.FINALIZADA -> 0L
    }

    val horas = tiempoVisible / 3600
    val minutos = (tiempoVisible % 3600) / 60
    val segundos = tiempoVisible % 60

    val tiempoFormateado = if (horas > 0) {
        "%02d:%02d:%02d".format(horas, minutos, segundos)
    } else {
        "%02d:%02d".format(minutos, segundos)
    }

    val progresoObjetivo = when (estadoActividad) {
        EstadoActividad.EN_CURSO ->
            (viewModel.segundosRestantes.toFloat() / duracionTotal).coerceIn(0f, 1f)
        else -> 0f
    }

    val progresoAnimado by animateFloatAsState(
        targetValue = progresoObjetivo,
        animationSpec = tween(durationMillis = 600),
        label = "progresoUso"
    )

    val colorAnillo by animateColorAsState(
        targetValue = when (estadoActividad) {
            EstadoActividad.EN_CURSO -> Color(0xFF2E7D32)
            EstadoActividad.FINALIZADA -> Color(0xFF9E9E9E)
            EstadoActividad.PENDIENTE -> Yellow
        },
        label = "colorAnillo"
    )


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

    var estadoYaCargado by remember(reservaId) { mutableStateOf(false) }
    var mostrarExito by remember(reservaId) { mutableStateOf(false) }

    LaunchedEffect(cargando, estadoActividad) {
        if (cargando) return@LaunchedEffect

        if (!estadoYaCargado) {
            estadoYaCargado = true
            return@LaunchedEffect
        }

        if (estadoActividad == EstadoActividad.FINALIZADA) {
            mostrarExito = true
        }
    }

    LaunchedEffect(reservaId) {
        cargando = true
        reservaAprobada = false

        try {
            val reserva = reservaRepository.obtenerReservaPorId(reservaId)

            if (reserva.estado == EstadoReserva.APROBADA) {
                viewModel.cargarReserva(reserva)
                viewModel.cargarEstadoGuardado(usoSalaDao, reservaId)

                val reservaActualizada = withContext(Dispatchers.IO) {
                    reservaRepository.obtenerReservaPorId(reservaId)
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

        Text(
            text = "Uso de Sala",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = viewModel.nombreActividad,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )

                    Surface(
                        color = colorEstado,
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = textoEstado,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = colorTextoEstado,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Sala ${viewModel.salaId ?: "-"}",
                    color = Black
                )

                Text(
                    text = "$fechaActividad   $horaInicio - $horaTermino",
                    color = Black
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Círculo de progreso
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (estadoActividad) {
                EstadoActividad.EN_CURSO -> {
                    CircularProgressIndicator(
                        progress = { progresoAnimado },
                        modifier = Modifier
                            .size(230.dp)
                            .graphicsLayer { scaleX = -1f },
                        color = colorAnillo,
                        trackColor = Color(0xFFE8E8E8),
                        strokeWidth = 8.dp
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when (estadoActividad) {
                                EstadoActividad.PENDIENTE ->
                                    if (segundosParaEmpezar == 0L) "Ya puedes iniciar" else "Comienza en"
                                EstadoActividad.EN_CURSO -> "Tiempo restante"
                                EstadoActividad.FINALIZADA -> "Actividad completada"
                            },
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
                            text = when (estadoActividad) {
                                EstadoActividad.PENDIENTE ->
                                    if (segundosParaEmpezar == 0L) {
                                        "La actividad todavía no ha sido iniciada"
                                    } else {
                                        "La actividad todavía no comienza"
                                    }
                                EstadoActividad.EN_CURSO -> "En desarrollo · finaliza a las $horaTermino"
                                EstadoActividad.FINALIZADA -> "Terminó dentro del horario reservado"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                EstadoActividad.PENDIENTE -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AnimacionCalendarioPendiente(tamano = 120.dp)
                        Text(
                            text = "La actividad todavía no comienza",
                            style = MaterialTheme.typography.bodySmall,
                            color = Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = tiempoFormateado,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Duración estimada: $duracionFormateada",
                            style = MaterialTheme.typography.bodySmall,
                            color = Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                EstadoActividad.FINALIZADA -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AnimacionConfetti(tamano = 180.dp)
                        }
                        Text(
                            text = "¡Actividad completada!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Terminó dentro del horario reservado",
                            style = MaterialTheme.typography.bodySmall,
                            color = Black,
                            textAlign = TextAlign.Center
                        )
                    }


                }
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

        val mensajeError = viewModel.errorActividad

        if (mensajeError != null) {
            if (mensajeError == "Todavía no comienza el horario de la reserva") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF4CC)
                    )
                ) {
                    Text(
                        text = "Aún no puedes iniciar la actividad. " +
                                "El horario todavía no comienza.",
                        modifier = Modifier.padding(10.dp),
                        color = Black,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(
                    text = mensajeError,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

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
                        style = MaterialTheme.typography.titleLarge
                    )
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
                                        val reserva = reservaRepository.obtenerReservaPorId(reservaId)

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
                    onClick = { mostrarReporte = true },
                    enabled = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Black),
                    colors = ButtonDefaults.outlinedButtonColors(disabledContentColor = Black)
                ) {
                    Text("Reportar inconveniente")
                }
            }

            EstadoActividad.FINALIZADA -> {

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

