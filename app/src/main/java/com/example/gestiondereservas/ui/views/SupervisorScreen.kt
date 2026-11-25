package com.example.gestiondereservas.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestiondereservas.R
import com.example.gestiondereservas.data.remote.SessionManager
import com.example.gestiondereservas.data.remote.dto.ReservaResponse
import com.example.gestiondereservas.data.room.entidades.EstadoReserva
import com.example.gestiondereservas.viewmodel.GestionReservasViewModel
import com.example.gestiondereservas.utils.formatearFecha

@Composable
fun SupervisorScreen(
                     onHorarios: () -> Unit,
                     onGestionSalas: () -> Unit,
                     onAuditoria: () -> Unit,
                     onReportes: () -> Unit,
                     onReservas: () -> Unit,
                     viewModel: GestionReservasViewModel
) {
    val listaReservas by viewModel.reservasPendientes.collectAsState()

    val sesion = remember(SessionManager.nombre) {
        SessionManager.nombre
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.split(" ")
            ?.firstOrNull()
            ?: "usuario"
    }


    Contenido(nombreUsuario = sesion,
            onHorarios,
            onGestionSalas,
            onAuditoria,
            onReportes,
            onReservas,
            listaReservas,
            viewModel)
}

@Composable
private fun Contenido(nombreUsuario: String,
                      onHorarios: () -> Unit,
                      onGestionSalas: () -> Unit,
                      onAuditoria: () -> Unit,
                      onReportes: () -> Unit,
                      onReservas: () -> Unit,
                      listaReservas: List<ReservaResponse>,
                      viewModel: GestionReservasViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = formatearFecha(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3B3B3B),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Hola, $nombreUsuario",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Reservas por confirmar",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold)

            TextButton(onClick = { onReservas() }) {
                Text("Ver todas",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if(listaReservas.isEmpty()){
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Text("No hay reservas pendientes",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(16.dp))

                //Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = { onReservas()},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Ir a reservas",
                        color = MaterialTheme.colorScheme.primary)
                }
            }
        } else
            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listaReservas.forEach { reserva ->
                    ReservasPendientes(
                        reserva,
                        onAprobar = { id ->
                            viewModel.modificarEstado(
                                id, EstadoReserva.APROBADA,
                                EstadoReserva.PENDIENTE
                            )
                        },
                        onRechazar = { id ->
                            viewModel.modificarEstado(
                                id, EstadoReserva.RECHAZADA,
                                EstadoReserva.PENDIENTE
                            )
                        }
                    )
                }
            }
        Spacer(modifier = Modifier.height(16.dp))

        AccesosDirectos(onHorarios,
            onGestionSalas,
            onReportes,
            onAuditoria
        )
    }
}

@Composable
fun ReservasPendientes(reserva: ReservaResponse,
                       onAprobar: (Long) -> Unit,
                       onRechazar: (Long) -> Unit) {
   Row {
       val (icono, colorFondo) = when (reserva.organizador) {
           "Administración y Negocios" -> Pair(R.drawable.bar_chart, Color(0xFFC98BDB))
           "Comunicación" -> Pair(R.drawable.message_square, Color(0xFFDF4661))
           "Construcción" -> Pair(R.drawable.box, Color(0xFFECA154))
           "Diseño" -> Pair(R.drawable.zap, Color(0xFFDBE442))
           "Gastronomía" -> Pair(R.drawable.award, Color(0xFFFF808B))
           "Informática y Telecomunicaciones" -> Pair(R.drawable.code, Color(0xFF8BB8E8))
           "Ingeniería y Recursos Naturales" -> Pair(R.drawable.sun, Color(0xFFA1D884))
           "Salud y Bienestar" -> Pair(R.drawable.activity, Color(0xFF99D6EA))
           else -> Pair(R.drawable.map, Color(0xFF2AD2C9))
       }

       ElevatedCard(
           elevation = CardDefaults.cardElevation(
               defaultElevation = 3.dp
           ),
           modifier = Modifier
               .fillMaxWidth(),
           colors = CardDefaults.cardColors(
               containerColor = MaterialTheme.colorScheme.secondary
           )

       ) {
           Row(
               modifier = Modifier.padding(16.dp),
               verticalAlignment = Alignment.CenterVertically
           ) {
               Box(
                   modifier = Modifier
                       .width(64.dp)
                       .height(120.dp)
                       .clip(RoundedCornerShape(16.dp))
                       .background(colorFondo),
                   contentAlignment = Alignment.Center
               ){
                   Icon(
                       painter = painterResource(icono),
                       contentDescription = "Icono reserva",
                       modifier = Modifier.size(30.dp)
                   )
               }

               Spacer(modifier = Modifier.width(12.dp))

               Column(
                   modifier = Modifier.weight(1f)
                       .fillMaxHeight()
               ) {
                   EtiquetaOrganizador(reserva.organizador)

                   Spacer(modifier = Modifier.height(4.dp))

                   Text(
                       reserva.nombreActividad,
                       color = MaterialTheme.colorScheme.primary,
                       fontSize = 14.sp,
                       fontWeight = FontWeight.Bold)

                   Spacer(modifier = Modifier.height(4.dp))

                   Text(
                       "Encargado/a: ${reserva.nombreResponsable}",
                       color = Color(0xFF3B3B3B),
                       fontSize = 12.sp)

                   Spacer(modifier = Modifier.height(2.dp))

                   Row (
                       verticalAlignment = Alignment.CenterVertically
                   ){
                       Icon(
                           painter = painterResource(R.drawable.calendar_alt),
                           contentDescription = "Icono calendario",
                           modifier = Modifier.size(15.dp)
                       )

                       Spacer(modifier = Modifier.width(4.dp))

                       Text(
                           "${reserva.fechaActividad} - ${reserva.horaInicio}",
                           color = Color(0xFF3B3B3B),
                           fontSize = 12.sp
                       )
                   }
               }
               Column{
                   IconButton(
                       modifier = Modifier
                           .clip(RoundedCornerShape(12.dp))
                           .background(Color(0xFFEBF5EA)),
                       onClick = { onAprobar(reserva.idReserva) }
                   ) {
                       Icon(
                           painter = painterResource(R.drawable.check),
                           contentDescription = "Icono aceptar",
                           tint = Color(0xFF1E8E3E)
                       )
                   }

                   Spacer(modifier = Modifier.height(8.dp))

                   IconButton(
                       modifier = Modifier
                           .clip(RoundedCornerShape(12.dp))
                           .background(Color(0xFFFAEAEC)),
                       onClick = { onRechazar(reserva.idReserva) }
                   ) {
                       Icon(
                           painter = painterResource(R.drawable.x),
                           contentDescription = "Icono rechazar",
                           tint = Color(0xFFD93025)
                       )
                   }
               }
           }
       }
   }
}

@Composable
private fun AccesosDirectos(onHorarios: () -> Unit,
                            onGestionSalas: () -> Unit,
                            onReportes: () -> Unit,
                            onAuditoria: () -> Unit) {
    Text("Accesos directos",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold)

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { onHorarios() },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)
                    )) {
                Icon(
                    painter = painterResource(R.drawable.clock),
                    contentDescription = "Icono reloj",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Horarios",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp)
        }

        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { onAuditoria() },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)
                    )) {
                Icon(
                    painter = painterResource(R.drawable.history),
                    contentDescription = "Icono auditoria",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Auditoria",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp)
        }

        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { onReportes() },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)
                    )) {
                Icon(
                    painter = painterResource(R.drawable.chart_bar),
                    contentDescription = "Icono grafico",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Reportes",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp)
        }

        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { onGestionSalas() },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)
                    )) {
                Icon(
                    painter = painterResource(R.drawable.door),
                    contentDescription = "Icono sala",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Salas",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp)
        }
    }

}