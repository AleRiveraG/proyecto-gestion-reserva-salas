
package com.example.gestiondereservas.ui.views

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.NotificacionEntity
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.ui.components.NavBar
import com.example.gestiondereservas.ui.components.TopBar
import com.example.gestiondereservas.ui.theme.Black
import com.example.gestiondereservas.ui.theme.NotificationBackground
import com.example.gestiondereservas.ui.theme.NotificationBlue
import com.example.gestiondereservas.ui.theme.NotificationDarkYellow
import com.example.gestiondereservas.ui.theme.NotificationDivider
import com.example.gestiondereservas.ui.theme.NotificationGray
import com.example.gestiondereservas.ui.theme.NotificationGreen
import com.example.gestiondereservas.ui.theme.NotificationRed
import com.example.gestiondereservas.ui.theme.NotificationSecondaryText
import com.example.gestiondereservas.ui.theme.White
import com.example.gestiondereservas.ui.theme.Yellow
import com.example.gestiondereservas.utils.NotificacionHelper
import com.example.gestiondereservas.utils.ProgramadorRecordatorios
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

private data class NotificacionUI(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val tipo: TipoNotificacion,
    val leida: Boolean,
    val reservaId: Long?
)

private enum class TipoNotificacion {
    SOLICITUD,
    APROBADA,
    RECORDATORIO,
    EQUIPAMIENTO,
    RECHAZADA
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(
    onVolver: () -> Unit,
    navController: NavController,
    esSupervisor: Boolean,
    correoUsuario: String? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val database = remember(context) {
        AppDatabase.getInstance(context)
    }

    val notificacionDao = remember(database) {
        database.notificacionDao()
    }

    var reservaSeleccionada by remember {
        mutableStateOf<ReservaEntity?>(null)
    }

    val correoNormalizado = remember(correoUsuario) {
        correoUsuario
            ?.trim()
            ?.lowercase()
            ?.takeIf { it.isNotBlank() }
    }

    val flujoNotificaciones = remember(
        notificacionDao,
        esSupervisor,
        correoNormalizado
    ) {
        when {
            esSupervisor -> {
                notificacionDao.obtenerNotificacionesSupervisor()
            }

            correoNormalizado != null -> {
                notificacionDao.obtenerNotificacionesRelator(
                    correoNormalizado
                )
            }

            else -> {
                flowOf(emptyList<NotificacionEntity>())
            }
        }
    }

    val registros by flujoNotificaciones.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    val notificaciones = registros.mapNotNull { registro ->
        val tipo = TipoNotificacion.entries.find {
            it.name == registro.tipo
        }

        tipo?.let {
            NotificacionUI(
                id = registro.id,
                titulo = registro.titulo,
                descripcion = registro.descripcion,
                tipo = it,
                leida = registro.leida,
                reservaId = registro.reservaId
            )
        }
    }

    val pendientes = notificaciones.count { !it.leida }

    // Botones de prueba para desarrollo
    val tituloPrueba = "Recordatorio de actividad"
    val mensajePrueba = "Tu actividad comienza pronto"

    val solicitarPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permitido ->
        if (permitido) {
            NotificacionHelper.mostrarNotificacion(
                context,
                tituloPrueba,
                mensajePrueba
            )
        }
    }

    fun probarNotificacion() {
        val necesitaPermiso =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED

        if (necesitaPermiso) {
            solicitarPermiso.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            NotificacionHelper.mostrarNotificacion(
                context,
                tituloPrueba,
                mensajePrueba
            )
        }
    }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // Encabezado
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVolver) {
                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Black
                    )
                }

                Column {
                    Text(
                        text = "Notificaciones ($pendientes)",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )

                    Text(
                        text = "Actualizaciones y recordatorios",
                        fontSize = 12.sp,
                        color = NotificationSecondaryText
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Marcar todas como leídas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Marcar todas como leídas",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NotificationDarkYellow,
                    modifier = Modifier.clickable {
                        scope.launch {
                            if (esSupervisor) {
                                notificacionDao
                                    .marcarTodasLeidasSupervisor()
                            } else {
                                val correo =
                                    correoNormalizado ?: return@launch

                                notificacionDao
                                    .marcarTodasLeidasRelator(correo)
                            }
                        }
                    }
                )
            }

            Spacer(Modifier.height(14.dp))

            // Botones de prueba
            OutlinedButton(
                onClick = {
                    probarNotificacion()
                },
                border = BorderStroke(1.dp, Black),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Black
                )
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text("Probar notificación")
            }

            OutlinedButton(
                onClick = {
                    ProgramadorRecordatorios.programarPrueba(
                        context,
                        tituloPrueba,
                        mensajePrueba
                    )
                },
                border = BorderStroke(1.dp, Black),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Black
                )
            ) {
                Icon(
                    Icons.Default.Alarm,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text("Programar recordatorio (2 min)")
            }

            Spacer(Modifier.height(12.dp))

            if (notificaciones.isEmpty()) {
                Text(
                    text = "No tienes notificaciones por ahora",
                    color = NotificationSecondaryText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    textAlign = TextAlign.Center
                )
            }

            if (!esSupervisor && correoNormalizado == null) {
                Text(
                    text = "Inicia sesión para ver tus notificaciones",
                    color = NotificationSecondaryText,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Listado de notificaciones
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = notificaciones,
                    key = { it.id }
                ) { notificacion ->

                    TarjetaNotificacion(
                        notificacion = notificacion,
                        onClick = {

                            // Coordinador: ir a Gestión de Reservas
                            if (
                                esSupervisor &&
                                notificacion.tipo ==
                                TipoNotificacion.SOLICITUD
                            ) {
                                scope.launch {
                                    notificacionDao
                                        .marcarLeidaSupervisor(
                                            notificacion.id
                                        )
                                }

                                navController.navigate(
                                    "baseSupervisor/reservas"
                                ) {
                                    launchSingleTop = true
                                }

                            } else {

                                // Relator: mostrar detalle
                                scope.launch {
                                    if (esSupervisor) {
                                        notificacionDao
                                            .marcarLeidaSupervisor(
                                                notificacion.id
                                            )
                                    } else {
                                        val correo =
                                            correoNormalizado
                                                ?: return@launch

                                        notificacionDao
                                            .marcarLeidaRelator(
                                                notificacion.id,
                                                correo
                                            )
                                    }

                                    val reservaId =
                                        notificacion.reservaId

                                    if (reservaId != null) {
                                        reservaSeleccionada =
                                            database.reservaDao()
                                                .obtenerReservaPorId(
                                                    reservaId
                                                )
                                    }
                                }
                            }
                        }
                    )

                    HorizontalDivider(
                        color = NotificationDivider,
                        thickness = 1.dp
                    )
                }
            }
        }

    // Detalle institucional de reserva
    reservaSeleccionada?.let { reserva ->

        val estado = reserva.estado.toString().uppercase()

        val colorEstado = when (estado) {
            "APROBADA" -> NotificationGreen
            "RECHAZADA" -> NotificationRed
            else -> NotificationDarkYellow
        }

        val iconoEstado = when (estado) {
            "APROBADA" -> Icons.Default.CheckCircle
            "RECHAZADA" -> Icons.Default.Cancel
            else -> Icons.Default.HourglassTop
        }

        val mensajeEstado = when (estado) {
            "APROBADA" ->
                "Tu reserva fue aprobada por coordinación."

            "RECHAZADA" ->
                "Tu solicitud de reserva fue rechazada."

            else ->
                "Tu solicitud está pendiente de revisión."
        }

        ModalBottomSheet(
            onDismissRequest = {
                reservaSeleccionada = null
            },
            containerColor = White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Encabezado de estado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(
                                colorEstado.copy(alpha = 0.12f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconoEstado,
                            contentDescription = null,
                            tint = colorEstado,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "DETALLE DE RESERVA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NotificationSecondaryText
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = estado,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorEstado
                        )
                    }
                }

                HorizontalDivider(
                    color = NotificationDivider
                )

                // Nombre de actividad
                Text(
                    text = reserva.nombreActividad,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )

                // Información de la reserva
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = NotificationBackground
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(16.dp)
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = NotificationDarkYellow
                            )

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Fecha",
                                    fontSize = 12.sp,
                                    color =
                                        NotificationSecondaryText
                                )

                                Text(
                                    text = reserva.fechaActividad
                                        .toString(),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Black
                                )
                            }
                        }

                        HorizontalDivider(
                            color = NotificationDivider
                        )

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Default.Schedule,
                                contentDescription = null,
                                tint = NotificationDarkYellow
                            )

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Horario",
                                    fontSize = 12.sp,
                                    color =
                                        NotificationSecondaryText
                                )

                                Text(
                                    text =
                                        "${reserva.horaInicio} - ${reserva.horaTermino}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Black
                                )
                            }
                        }
                    }
                }

                // Mensaje del estado
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = colorEstado.copy(alpha = 0.10f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = colorEstado
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = mensajeEstado,
                            fontSize = 13.sp,
                            color = Black
                        )
                    }
                }

                // Botón amarillo institucional
                Button(
                    onClick = {
                        reservaSeleccionada = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Yellow,
                        contentColor = Black
                    )
                ) {
                    Text(
                        text = "Cerrar detalle",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaNotificacion(
    notificacion: NotificacionUI,
    onClick: () -> Unit
) {
    val icono = when (notificacion.tipo) {
        TipoNotificacion.SOLICITUD ->
            Icons.Default.HourglassTop

        TipoNotificacion.APROBADA ->
            Icons.Default.CheckCircle

        TipoNotificacion.RECORDATORIO ->
            Icons.Default.Alarm

        TipoNotificacion.EQUIPAMIENTO ->
            Icons.Default.Build

        TipoNotificacion.RECHAZADA ->
            Icons.Default.Cancel
    }

    val colorIcono = when (notificacion.tipo) {
        TipoNotificacion.SOLICITUD ->
            NotificationDarkYellow

        TipoNotificacion.APROBADA ->
            NotificationGreen

        TipoNotificacion.RECORDATORIO ->
            NotificationBlue

        TipoNotificacion.EQUIPAMIENTO ->
            NotificationGray

        TipoNotificacion.RECHAZADA ->
            NotificationRed
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (notificacion.leida) {
                    White
                } else {
                    NotificationBackground
                }
            )
            .clickable(onClick = onClick)
            .padding(
                vertical = 17.dp,
                horizontal = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    colorIcono.copy(alpha = 0.10f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = colorIcono,
                modifier = Modifier.size(21.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = notificacion.titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Black
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = notificacion.descripcion,
                fontSize = 11.sp,
                color = NotificationSecondaryText
            )
        }

        if (!notificacion.leida) {
            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(
                        Yellow,
                        CircleShape
                    )
            )
        }
    }
}
