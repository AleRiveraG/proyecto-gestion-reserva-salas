package com.example.gestiondereservas.ui.views

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestiondereservas.R
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.viewmodel.ReservaViewModel

@Composable
fun ReservasRelatorScreen(viewModel: ReservaViewModel = rememberReservaViewModel(),
                          idUsuario: Long
                          ) {
    val reservas by viewModel.reservas.collectAsState()

    LaunchedEffect(idUsuario) {
        viewModel.buscarReservaPorId(idUsuario)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text("Mis Reservas",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Text("Consulta aquí tus reservas realizadas",
            color = MaterialTheme.colorScheme.primary.copy(0.6f),
            fontSize = 16.sp)

        Spacer(modifier = Modifier.height(12.dp))

        reservas.forEach { reserva ->

            Spacer(modifier = Modifier.height(12.dp))

            ReservaCard(reserva)
        }
    }
}

@Composable
fun ReservaCard(reserva: ReservaEntity) {
    Row() {
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
                    etiquetaOrganizador(reserva.organizador)

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
                            painter = painterResource(com.example.gestiondereservas.R.drawable.calendar_alt),
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
            }
        }
    }
}
