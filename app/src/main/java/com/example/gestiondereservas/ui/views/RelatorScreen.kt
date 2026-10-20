package com.example.gestiondereservas.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.util.TableInfo
import com.example.gestiondereservas.R
import com.example.gestiondereservas.data.AppState
import com.example.gestiondereservas.data.repository.ReservaRepository
import com.example.gestiondereservas.data.repository.UsuarioRepository
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.EstadoReserva
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.ui.viewmodel.ReservaViewModel
import com.example.gestiondereservas.ui.views.accesosDirectos
import com.example.gestiondereservas.ui.viewmodel.UsuarioViewModel
import com.example.gestiondereservas.utils.formatearFecha
@Composable
fun rememberUsuarioViewModel(): UsuarioViewModel {
    val contexto = LocalContext.current.applicationContext
    return viewModel(
        factory = viewModelFactory {
            initializer {
                val dao = AppDatabase.getInstance(contexto).usuarioDao()
                val repository = UsuarioRepository(dao, )
                UsuarioViewModel(repository)
            }
        }
    )
}
@Composable
fun RelatorScreen(viewModel: ReservaViewModel = rememberReservaViewModel(),
                  usuarioViewModel: UsuarioViewModel = rememberUsuarioViewModel(),
                  idUsuario: Long,
                  onReservas: () -> Unit,
                  onBuscarSala: () -> Unit,
                  onCalendario: () -> Unit,
                  onActividad: () -> Unit)
{
    val reservas by viewModel.reservas.collectAsState()
    val usuario by usuarioViewModel.usuario.collectAsState()

    LaunchedEffect(idUsuario) {
        viewModel.buscarReservaPorId(idUsuario)
        usuarioViewModel.cargarUsuario(idUsuario)
    }
    Contenido(nombreUsuario = usuario?.nombre?.split(" ")[0] ?: "usuario",
        onReservas,
        onActividad,
        onCalendario,
        onBuscarSala,
        reservas)
}

@Composable
private fun Contenido(
    nombreUsuario: String,
    onReservas: () -> Unit,
    onActividad: () -> Unit,
    onCalendario: () -> Unit,
    onBuscarSala: () -> Unit,
    reservas: List<ReservaEntity>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = formatearFecha(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3B3B3B)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Hola, $nombreUsuario",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            "¿Qué espacio necesitas hoy?",
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            color = Color(0xFF3B3B3B)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (reservas.isEmpty()) {
            noInfoReserva(onReservas)
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                reservas.forEach { reserva ->
                    infoReserva(reserva)
                }
            }
        }


        Spacer(modifier = Modifier.height(12.dp))

        accesosDirectos(onBuscarSala, onCalendario, onReservas, onActividad)
    }
}


@Composable
fun infoReserva(
                reserva: ReservaEntity) {
    var checked by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary)
    ){
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Información de tu reserva",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )

                if(reserva.estado == EstadoReserva.PENDIENTE) {
                    etiquetaEstado("PENDIENTE")
                } else if (reserva.estado == EstadoReserva.APROBADA) {
                    etiquetaEstado("APROBADA")
                } else {
                    etiquetaEstado("RECHAZADA")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2C2C2C)),
                contentAlignment = Alignment.Center
            ){
                Icon(
                    painter = painterResource(R.drawable.school),
                    contentDescription = "Icono escuela",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.scale(2.5f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(reserva.nombreActividad,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            Row() {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.calendar),
                        contentDescription = "Icono calendario",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(reserva.fechaActividad,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.clock),
                        contentDescription = "Icono reloj",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text("${reserva.horaInicio} - ${reserva.horaTermino}",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.door),
                        contentDescription = "Icono sala",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text("${reserva.salaId}",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recordarme",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp)

                Switch(
                    modifier = Modifier.scale(0.8f),
                    checked = checked,
                    onCheckedChange = { checked = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.secondary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                        checkedTrackColor = MaterialTheme.colorScheme.tertiary

                    )
                )
            }
        }
    }
}

@Composable
private fun accesosDirectos(onBuscarSala: () -> Unit,
                            onCalendario: () -> Unit,
                            onReservas: () -> Unit,
                            onActividad: () -> Unit) {
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
            IconButton(onClick = { onBuscarSala() },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)
                )) {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = "Icono buscar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Buscar sala",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp)
        }

        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { onCalendario() },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)
                    )) {
                Icon(
                    painter = painterResource(R.drawable.calendar_alt),
                    contentDescription = "Icono calendario alt",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Calendario",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp)
        }

        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { onReservas() },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)
                    )) {
                Icon(
                    painter = painterResource(R.drawable.task),
                    contentDescription = "Icono reserva",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Reservar",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp)
        }

        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { onActividad() },
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

            Text("Mi actividad",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp)
        }
    }

}

@Composable
fun noInfoReserva(onReservas: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("No tienes reservas actuales",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onReservas() },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("Reservar sala",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}