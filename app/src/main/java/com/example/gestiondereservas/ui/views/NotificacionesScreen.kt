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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.gestiondereservas.ui.theme.Black
import com.example.gestiondereservas.ui.theme.NotificationBackground
import com.example.gestiondereservas.ui.theme.NotificationBlue
import com.example.gestiondereservas.ui.theme.NotificationDarkYellow
import com.example.gestiondereservas.ui.theme.NotificationGray
import com.example.gestiondereservas.ui.theme.NotificationGreen
import com.example.gestiondereservas.ui.theme.NotificationRed
import com.example.gestiondereservas.ui.theme.NotificationSecondaryText
import com.example.gestiondereservas.ui.theme.White
import com.example.gestiondereservas.ui.theme.Yellow
import com.example.gestiondereservas.ui.theme.NotificationDivider
import com.example.gestiondereservas.ui.components.TopBar
import com.example.gestiondereservas.ui.components.NavBar
import com.example.gestiondereservas.utils.NotificacionHelper
import com.example.gestiondereservas.utils.ProgramadorRecordatorios
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.NotificacionEntity


// Representa una notificación
private data class NotificacionUI(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val tipo: TipoNotificacion,
    val leida: Boolean = false
)

// Tipos de notificaciones
private enum class TipoNotificacion {
    SOLICITUD,
    APROBADA,
    RECORDATORIO,
    EQUIPAMIENTO,
    RECHAZADA
}

@Composable
fun NotificacionesScreen(
    onVolver: () -> Unit,
    navController: NavController,
    esSupervisor: Boolean
) {
    // Datos de ejemplo para esta primera versión


    val context = LocalContext.current

    val tituloNotificacion = "Recordatorio de actividad"
    val mensajeNotificacion = "Tu actividad comienza pronto"

    // Solicitar permiso en Android 13 o superior
    val solicitarPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permitido ->
        if (permitido) {
            NotificacionHelper.mostrarNotificacion(
                context,
                tituloNotificacion,
                mensajeNotificacion
            )
        }
    }

    // Prueba de notificación inmediata
    fun probarNotificacion() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            solicitarPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            NotificacionHelper.mostrarNotificacion(
                context,
                tituloNotificacion,
                mensajeNotificacion
            )
        }
    }

    val database = remember(context) {
        AppDatabase.getInstance(context)
    }
    val notificacionDao = remember(database) {
        database.notificacionDao()
    }
    val scope = rememberCoroutineScope()

    val ejemplos = remember(esSupervisor) {
        mutableStateListOf<NotificacionUI>().apply {
            if (esSupervisor) {
                add(
                    NotificacionUI(
                        1,
                        "Nueva solicitud por validar",
                        "Taller de programación · Sala C204",
                        TipoNotificacion.SOLICITUD
                    )
                )
                add(
                    NotificacionUI(
                        2,
                        "Solicitudes pendientes",
                        "Tienes reservas por revisar",
                        TipoNotificacion.SOLICITUD
                    )
                )
            } else {
                add(
                    NotificacionUI(
                        3,
                        "Reserva aprobada",
                        "Workshop de experiencia digital",
                        TipoNotificacion.APROBADA
                    )
                )
                add(
                    NotificacionUI(
                        4,
                        "Tu actividad comienza en 1 hora",
                        "Sala C204 · 10:00",
                        TipoNotificacion.RECORDATORIO
                    )
                )
                add(
                    NotificacionUI(
                        5,
                        "Equipamiento liberado",
                        "Proyector disponible en Sala D302",
                        TipoNotificacion.EQUIPAMIENTO
                    )
                )
                add(
                    NotificacionUI(
                        6,
                        "Reserva rechazada",
                        "Revisa el motivo de tu solicitud",
                        TipoNotificacion.RECHAZADA
                    )
                )
            }
        }
    }

    LaunchedEffect(esSupervisor) {
        val guardadas = notificacionDao
            .obtenerNotificaciones(esSupervisor)
            .first()

        if (guardadas.isEmpty()) {
            ejemplos.forEach { ejemplo ->
                notificacionDao.guardarNotificacion(
                    NotificacionEntity(
                        titulo = ejemplo.titulo,
                        descripcion = ejemplo.descripcion,
                        tipo = ejemplo.tipo.name,
                        esSupervisor = esSupervisor,
                        leida = ejemplo.leida
                    )
                )
            }
        }
    }

    val registros by notificacionDao
        .obtenerNotificaciones(esSupervisor)
        .collectAsState(initial = emptyList())

    val notificaciones = registros.map { registro ->
        NotificacionUI(
            id = registro.id.toInt(),
            titulo = registro.titulo,
            descripcion = registro.descripcion,
            tipo = TipoNotificacion.valueOf(registro.tipo),
            leida = registro.leida
        )
    }

    val pendientes = notificaciones.count { !it.leida }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = {
            NavBar(
                inicial = 0,
                onInicio = {
                    navController.popBackStack()
                },
                onCalendario = {
                    // TODO: Conectar con la ruta del calendario
                },
                onSalas = {
                    // TODO: Conectar con la ruta de salas
                }
            )
        }
    ) { paddingScaffold ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingScaffold)
                .background(White)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVolver) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
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

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Marcar todas como leídas",
                    fontSize = 12.sp,
                    color = NotificationDarkYellow,
                    modifier = Modifier.clickable {
                        scope.launch {
                            notificacionDao.marcarTodasLeidas(esSupervisor)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Prueba de notificación inmediata
            Button(
                onClick = { probarNotificacion() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = Black
                ),
                border = BorderStroke(1.dp, Black)
            ) {
                Text("Probar notificación")
            }

            // Prueba de recordatorio automático
            Button(
                onClick = {
                    ProgramadorRecordatorios.programarPrueba(
                        context,
                        tituloNotificacion,
                        mensajeNotificacion
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = Black
                ),
                border = BorderStroke(1.dp, Black)
            ) {
                Text("Programar recordatorio (2 min)")
            }

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
                            scope.launch {
                                notificacionDao.marcarLeida(notificacion.id.toLong())
                            }

                            // Navegar según el tipo de notificación
                            when (notificacion.tipo) {
                                TipoNotificacion.SOLICITUD -> {
                                    navController.navigate("baseSupervisor/gestionReserva")
                                }

                                TipoNotificacion.APROBADA,
                                TipoNotificacion.RECORDATORIO,
                                TipoNotificacion.RECHAZADA -> {
                                   navController.navigate("baseRelator/reservas")
                                }

                                else -> {
                                    // Los demás tipos todavía no tienen navegación.
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
    }
}

@Composable
private fun TarjetaNotificacion(
    notificacion: NotificacionUI,
    onClick: () -> Unit
) {
    val icono = when (notificacion.tipo) {
        TipoNotificacion.SOLICITUD -> Icons.Default.HourglassTop
        TipoNotificacion.APROBADA -> Icons.Default.CheckCircle
        TipoNotificacion.RECORDATORIO -> Icons.Default.Alarm
        TipoNotificacion.EQUIPAMIENTO -> Icons.Default.Build
        TipoNotificacion.RECHAZADA -> Icons.Default.Cancel
    }

    val colorIcono = when (notificacion.tipo) {
        TipoNotificacion.SOLICITUD -> NotificationDarkYellow
        TipoNotificacion.APROBADA -> NotificationGreen
        TipoNotificacion.RECORDATORIO -> NotificationBlue
        TipoNotificacion.EQUIPAMIENTO -> NotificationGray
        TipoNotificacion.RECHAZADA -> NotificationRed
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (notificacion.leida) White
                else NotificationBackground
            )
            .clickable(onClick = onClick)
            .padding(vertical = 17.dp, horizontal = 4.dp),
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

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = notificacion.titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = notificacion.descripcion,
                fontSize = 11.sp,
                color = NotificationSecondaryText
            )
        }

        if (!notificacion.leida) {
            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(Yellow, CircleShape)
            )
        }
    }
}
