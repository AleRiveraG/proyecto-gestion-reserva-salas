package com.example.gestiondereservas.ui.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gestiondereservas.R
import com.example.gestiondereservas.data.repository.ReservaRepository
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.AuditoriaEntity
import com.example.gestiondereservas.data.room.entidades.EstadoReserva
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import com.example.gestiondereservas.data.room.entidades.TipoAuditoria
import com.example.gestiondereservas.ui.viewmodel.GestionReservasViewModel
import com.example.gestiondereservas.utils.formatearFechaPerso
import com.example.gestiondereservas.utils.formatearHora
import java.time.LocalDate
import kotlin.collections.emptyList

@Composable
fun rememberGestionReservasViewModel(): GestionReservasViewModel {
    val contexto = LocalContext.current.applicationContext
    return viewModel(
        factory = viewModelFactory {
            initializer {
                val dao = AppDatabase.getInstance(contexto).reservaDao()
                val daoSalas = AppDatabase.getInstance(contexto).salaDao()
                val repository = ReservaRepository(dao, daoSalas)
                GestionReservasViewModel(repository,contexto)
            }
        }
    )
}
@Composable
fun GestionReservasScreen(
    viewModel: GestionReservasViewModel = rememberGestionReservasViewModel(),
    onRegistrarAuditoria: (AuditoriaEntity) -> Unit
) {
    val reservas by viewModel.reservasPendientes.collectAsState()
    val aprobadas by viewModel.aprobadas.collectAsState()
    val pendientes by viewModel.pendientes.collectAsState()
    val rechazadas by viewModel.rechazadas.collectAsState()

    fun crearAuditoria(accion: String, detalle: String): AuditoriaEntity{
        return AuditoriaEntity(
            accion = accion,
            detalle = detalle,
            fecha = LocalDate.now().toString(),
            hora = formatearHora(),
            tipo = TipoAuditoria.RESERVA.name
        )
    }

    Contenido(reservas,
        aprobadas,
        rechazadas,
        pendientes,
        onBuscarSala = { id -> viewModel.buscarSala(id)},
        onAprobar = { id ->
            viewModel.modificarEstado(id, EstadoReserva.APROBADA.name)
                    val auditoria = crearAuditoria("Se aprobo una solicitud", "")
                    onRegistrarAuditoria(auditoria)
                    } ,
        onRechazar = { id ->
            viewModel.modificarEstado(id, EstadoReserva.RECHAZADA.name)
            val auditoria = crearAuditoria("Se rechazo una solicitud", "")
            onRegistrarAuditoria(auditoria)
        }
        )
}

@Composable
private fun Contenido(reservas: List<ReservaEntity>,
                      aprobadas: Int,
                      rechazadas: Int,
                      pendientes: Int,
                      onBuscarSala: suspend (Long) -> SalaEntity?,
                      onAprobar: (Long) -> Unit,
                      onRechazar: (Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Gestión de Reservas",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Valida las solicitudes de reservas",
            color = Color(0xFF3B3B3B),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        contador(pendientes, aprobadas, rechazadas)

        Spacer(modifier = Modifier.height(12.dp))

        reservas.forEach { reserva ->
            reserva(
                reserva,
                onAprobar,
                onRechazar,
                onBuscarSala)

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun contador(pendientes: Int, aprobadas: Int, rechazadas: Int) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)){
                Text(text = pendientes.toString(),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 24.sp)

                Spacer(modifier = Modifier.height(4.dp))

                Text("Pendientes",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp)
            }

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)){
                Text(text = aprobadas.toString(),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 24.sp)

                Spacer(modifier = Modifier.height(4.dp))

                Text("Aprobadas",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp)
            }

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)){
                Text(text = rechazadas.toString(),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 24.sp)

                Spacer(modifier = Modifier.height(4.dp))

                Text("Rechazadas",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun reserva(reserva: ReservaEntity, onAprobar: (Long) -> Unit, onRechazar: (Long) -> Unit,
            onBuscarSala: suspend (Long) -> SalaEntity?) {
    var mostrarModal by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var sala by remember { mutableStateOf<SalaEntity?>(null) }

    LaunchedEffect(reserva.salaId) {
        sala = onBuscarSala(reserva.salaId)
    }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation =6.dp
        ),
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                etiquetaOrganizador(reserva.organizador)
                etiquetaEstado(reserva.estado.name)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = reserva.nombreActividad,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                etiquetaTipoActividad(reserva.tipoActividad)

                Spacer(modifier = Modifier.width(8.dp))

                etiquetaTieneAutorizacion(if (reserva.tieneValidacion) "Si" else "No")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Responsable: ${reserva.nombreResponsable}",
                    color = Color(0xFF3B3B3B),
                    fontSize = 12.sp)

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = reserva.telefonoResponsable,
                    color = Color(0xFF3B3B3B),
                    fontSize = 12.sp)
            }

            Text(text = reserva.correoResponsable,
                color = Color(0xFF3B3B3B),
                fontSize = 12.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Icon(
                        painter = painterResource(R.drawable.door),
                        contentDescription = "Icono Puerta",
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text("Recinto",
                            color = Color(0xFF63788C),
                            fontSize = 14.sp)
                        Text(text = sala?.codigo ?: reserva.salaId.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Icon(
                        painter = painterResource(R.drawable.clock),
                        contentDescription = "Icono Hora",
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text("Inicio y término",
                            color = Color(0xFF63788C),
                            fontSize = 14.sp)
                        Text("${reserva.horaInicio} - ${reserva.horaTermino}",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Icon(
                        painter = painterResource(R.drawable.spanner),
                        contentDescription = "Icono Montaje",
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text("Montaje",
                            color = Color(0xFF63788C),
                            fontSize = 14.sp)
                        Text(text = reserva.horaMontaje,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Icon(
                        painter = painterResource(R.drawable.community),
                        contentDescription = "Icono Publico",
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text("Público",
                            color = Color(0xFF63788C),
                            fontSize = 14.sp)
                        Text(text = reserva.publico,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary
                    ),
                onClick = { mostrarModal = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Ver detalle de la solicitud",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
    if (mostrarModal) {
        ModalBottomSheet(
            onDismissRequest = { mostrarModal = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            detalleSolicitud(
                onCerrar = { mostrarModal = false },
                reserva = reserva,
                onAprobar = onAprobar,
                onRechazar = onRechazar,
                soloLectura = false,
                sala = sala
            )
        }
    }
}

@Composable
fun etiquetaTipoActividad(tipo: String){
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(32.dp))
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.tertiary
            )
            .background(Color(0xFFFFF9E6))
    ){
        Text(text = tipo,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
    }
}

@Composable
fun etiquetaTieneAutorizacion(tieneAuth: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(32.dp))
            .background(color = if (tieneAuth == "Si") Color(0xFFE5F2E1) else Color(0xFFFFF9E6))
    ){
        Text(text = "Dirección: $tieneAuth",
            color = if (tieneAuth == "Si") Color(0xFF005C35) else Color(0xFF6B5403),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
    }
}

@Composable
fun etiquetaOrganizador(organizador: String){
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                color =
                    if (organizador == "Informática y Telecomunicaciones") Color(0xFF8BB8E8)
                    else if (organizador == "Administración y Negocios") Color(0xFFC98BDB)
                    else if (organizador == "Comunicación") Color(0xFFDF4661)
                    else if (organizador == "Construcción") Color(0xFFECA154)
                    else if (organizador == "Diseño") Color(0xFFDBE442)
                    else if (organizador == "Gastronomía") Color(0xFFFF808B)
                    else if (organizador == "Ingeniería y Recursos Naturales") Color(0xFFA1D884)
                    else if (organizador == "Salud y Bienestar") Color(0xFF99D6EA)
                    else Color(0xFF2AD2C9)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("•",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp),
                fontWeight = FontWeight.Bold)
            Text(
                text = if (organizador == "Informática y Telecomunicaciones") "Informática"
                else if (organizador == "Administración y Negocios") "Negocios"
                else if (organizador == "Comunicación") "Comunicación"
                else if (organizador == "Construcción") organizador
                else if (organizador == "Diseño") organizador
                else if (organizador == "Gastronomía") organizador
                else if (organizador == "Ingeniería y Recursos Naturales") "Ingeniería, RN"
                else if (organizador == "Salud y Bienestar") "Salud"
                else "Turismo",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, end = 8.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun etiquetaEstado(estado: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                color =
                    if (estado == "PENDIENTE") Color(0xFFFFF9E6)
                    else if (estado == "APROBADA") Color(0xFFE5F2E1)
                    else Color(0xFFFCE7EC)
            )
    ) {
        Text(text =
            if (estado == "PENDIENTE") "Pendiente"
            else if (estado == "APROBADA") "Aprobada"
            else "Rechazado",
            color = Color(0xFF6B5403),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp))
    }
}

@Composable
fun detalleSolicitud(
    onCerrar: () -> Unit,
    reserva: ReservaEntity,
    onAprobar: (Long) -> Unit,
    onRechazar: (Long) -> Unit,
    soloLectura: Boolean = false,
    sala: SalaEntity? = null
) {
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("DETALLE DE LA SOLICITUD",
                color = Color(0xFF3B3B3B),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            IconButton(
                onClick = { onCerrar() } ) {
                Icon(
                    painter = painterResource(R.drawable.x),
                    contentDescription = "Icono Cerrar",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = reserva.nombreActividad,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(12.dp))

        Row{
            Text("Recinto ${sala?.codigo ?: reserva.salaId.toString()}",
                color = Color(0xFF3B3B3B),
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = formatearFechaPerso(reserva.fechaActividad),
                color = Color(0xFF3B3B3B),
                fontSize = 16.sp
                )
        }

        Spacer(modifier = Modifier.height(12.dp))

        servicios(reserva)

        if (!soloLectura && reserva.estado == EstadoReserva.PENDIENTE) {

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier
                    .weight(1f),
                onClick = {
                    onRechazar(reserva.id)
                    onCerrar() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD00236)
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.x),
                    contentDescription = "Icono Cerrar",
                    tint = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    "Denegar",
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                modifier = Modifier
                    .weight(1f),
                onClick = {
                    onAprobar(reserva.id)
                    onCerrar()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3DB83B)
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.check),
                    contentDescription = "Icono Aprobar",
                    tint = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    "Aprobar",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        }
    }
}

@Composable
fun servicios(reserva: ReservaEntity) {
    val servicios = reserva.servicios

    if (servicios.isBlank()) return

    servicios.split("; ").forEach { area ->
        if (area.isBlank()) return@forEach

        val partes = area.split(": ")
        if (partes.size < 2) return@forEach

        val categoria = partes[0]
        val opciones = partes[1].split(",")

        OutlinedCard (
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary),
            border = BorderStroke(1.dp, Color(0xFFE5E5E5))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = categoria.replaceFirstChar { it.uppercase() },
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = opciones.joinToString(", "),
                    color = Color(0xFF3B3B3B),
                    fontSize = 12.sp,
                )
            }
        }
    }





}