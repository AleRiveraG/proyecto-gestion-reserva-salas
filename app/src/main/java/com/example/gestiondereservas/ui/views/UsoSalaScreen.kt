
package com.example.gestiondereservas.ui.views

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

import com.example.gestiondereservas.ui.viewmodel.EstadoActividad
import com.example.gestiondereservas.ui.viewmodel.UsoSalaViewModel
import com.example.gestiondereservas.ui.theme.Black
import com.example.gestiondereservas.ui.theme.White
import com.example.gestiondereservas.ui.theme.Yellow

@Composable
fun UsoSalaScreen(
    viewModel: UsoSalaViewModel = viewModel()
) {

    val estadoActividad = viewModel.estadoActividad

    val horaTermino = viewModel.finReserva.format(
        DateTimeFormatter.ofPattern("HH:mm")
    )

  // Duración total de la reserva registrada calculando el tiempo entre el incio y fin de reserva
    val duracionTotal = java.time.Duration.between(
        viewModel.inicioReserva,
        viewModel.finReserva
    ).seconds.coerceAtLeast(1)


   // Durante la actividad: tiempo real hasta finalizar la reserva
    val tiempoRestante = if (
        estadoActividad == EstadoActividad.PENDIENTE
    ) {
        duracionTotal
    } else {
        viewModel.segundosRestantes
    }

    val minutos = tiempoRestante / 60
    val segundos = tiempoRestante % 60

    val tiempoFormateado = "%02d:%02d".format(minutos, segundos)

    // Porcentaje de tiempo transcurrido de la reserva
    val progreso = (
                tiempoRestante.toFloat()/duracionTotal
            ).coerceIn(0f, 1f)


    val actividadEnCurso =
        estadoActividad == EstadoActividad.EN_CURSO

    val colorProgreso =
        if (actividadEnCurso) Color(0xFF4CAF17) else Yellow

    val colorBoton =
        if (actividadEnCurso) Color(0xFFD50032) else Yellow

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
                text = "Sala C204",
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
            text = "Título de la actividad",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Black,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Workshop de experiencia digital",
            style = MaterialTheme.typography.bodySmall,
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
                        "Comienza cuando estés listo",
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
                        "Duración estimada: ${duracionTotal / 60} min"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Black,
                    textAlign = TextAlign.Center
                )
            }

        }

        Spacer(modifier = Modifier.weight(1f))

        when (estadoActividad) {

            EstadoActividad.PENDIENTE -> {

                Button(
                    onClick = { viewModel.iniciarActividad() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Yellow,
                        contentColor = Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("▷  Inicio de Actividad")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {},
                    enabled = false,
                    border = BorderStroke(1.dp,Black),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Black),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancelar Reserva")
                }
            }

            EstadoActividad.EN_CURSO -> {

                Button(
                    onClick = { viewModel.finalizarActividad() },
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
                    onClick = {},
                    enabled = false,
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
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
