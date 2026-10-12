package com.example.gestiondereservas.ui.views

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onVolver: () -> Unit
) {
    // Datos de ejemplo para esta primera versión
    val notificaciones = remember {
        mutableStateListOf(
            NotificacionUI(
                1,
                "Nueva solicitud por validar",
                "Taller de programación · Sala C204",
                TipoNotificacion.SOLICITUD
            ),
            NotificacionUI(
                2,
                "Reserva aprobada",
                "Workshop de experiencia digital",
                TipoNotificacion.APROBADA
            ),
            NotificacionUI(
                3,
                "Tu actividad comienza en 1 hora",
                "Sala C204 · 10:00",
                TipoNotificacion.RECORDATORIO
            ),
            NotificacionUI(
                4,
                "Equipamiento liberado",
                "Proyector disponible en Sala D302",
                TipoNotificacion.EQUIPAMIENTO,
                leida = true
            ),
            NotificacionUI(
                5,
                "Reserva rechazada",
                "Revisa el motivo en tu solicitud",
                TipoNotificacion.RECHAZADA,
                leida = true
            )
        )
    }

    val pendientes = notificaciones.count { !it.leida }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { NavBar() }
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
                        text = "Notificaciones",
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
                        notificaciones.indices.forEach { indice ->
                            notificaciones[indice] =
                                notificaciones[indice].copy(leida = true)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

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
                            val indice = notificaciones.indexOfFirst {
                                it.id == notificacion.id
                            }

                            if (indice != -1) {
                                notificaciones[indice] =
                                    notificaciones[indice].copy(leida = true)
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
