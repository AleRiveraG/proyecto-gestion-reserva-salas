
package com.example.gestiondereservas.ui.components

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gestiondereservas.data.AppState
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.AuditoriaEntity
import com.example.gestiondereservas.data.room.entidades.NotificacionEntity
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.ui.theme.Black
import com.example.gestiondereservas.ui.theme.White
import com.example.gestiondereservas.ui.theme.Yellow
import com.example.gestiondereservas.ui.views.BusquedaSalasScreen
import com.example.gestiondereservas.ui.views.HorariosScreen
import com.example.gestiondereservas.ui.views.NotificacionesScreen
import com.example.gestiondereservas.ui.views.RelatorScreen
import com.example.gestiondereservas.ui.views.ReservasScreen
import com.example.gestiondereservas.ui.views.UsoSalaScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PantallaBaseRelator(
    navController: NavHostController,
    idUsuario: Long,
    onRegistrarAuditoria: (AuditoriaEntity) -> Unit = {},
    abrirReservas: Boolean = false,
    onCerrarSesion: () -> Unit,
    appState: AppState,
    correoUsuario: String? = null
) {
    val tabNavController = rememberNavController()
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()

    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    val opcionSeleccionada = when (rutaActual) {
        "paginaRelator" -> 0
        "calendarioRelator" -> 1
        "salasRelator" -> 2
        else -> 0
    }

    val notificacionDao = remember(contexto) {
        AppDatabase.getInstance(contexto).notificacionDao()
    }

    val flujoNotificaciones: Flow<List<NotificacionEntity>> =
        remember(notificacionDao, correoUsuario) {
            val correo = correoUsuario?.trim()?.lowercase()
            if (correo.isNullOrBlank()) {
                flowOf(emptyList())
            } else {
                notificacionDao.obtenerNotificacionesRelator(correo)
            }
        }

    val notificaciones by flujoNotificaciones.collectAsStateWithLifecycle(
        initialValue = emptyList<NotificacionEntity>()
    )

    val tieneSinLeer: Boolean = notificaciones.any { notificacion ->
        !notificacion.leida
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                onNotificacionesClick = {
                    tabNavController.navigate("notificaciones/false")

                },
                onCerrarSesion = onCerrarSesion,
                tieneNotificacionesSinLeer = tieneSinLeer
            )
        },
        bottomBar = {
            NavBar(
                opcionSeleccionada,
                onInicio = {
                    tabNavController.navigate("paginaRelator")
                },
                onCalendario = {
                    tabNavController.navigate("calendarioRelator")
                },
                onSalas = {
                    tabNavController.navigate("salasRelator")
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.secondary
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            NavHost(
                navController = tabNavController,
                startDestination = if (abrirReservas) "reservas" else "paginaRelator"
            ) {
                composable("paginaRelator") {

                    var mostrarSelectorReservas by remember { mutableStateOf(false) }
                    var reservasParaElegir by remember { mutableStateOf<List<ReservaEntity>>(emptyList()) }
                    var abrirActividadSeleccionada by remember {
                        mutableStateOf<(Long) -> Unit>({})
                    }
                    RelatorScreen(
                        idUsuario = idUsuario,
                        onReservas = {
                            tabNavController.navigate("reservas")
                        },
                        onBuscarSala = {
                            tabNavController.navigate("salasRelator")
                        },
                        onCalendario = {
                            tabNavController.navigate("calendarioRelator")
                        },
                        onActividad = {
                            abrirActividadSeleccionada = { reservaId ->
                                tabNavController.navigate("usoSala/$reservaId")
                            }
                            val correo = correoUsuario
                            if (correo.isNullOrBlank()) {
                                Toast.makeText(
                                    contexto,
                                    "No se encontró el usuario de la sesión",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                scope.launch {
                                    try {
                                        val reservas = withContext(Dispatchers.IO) {
                                            AppDatabase.getInstance(contexto)
                                                .reservaDao()
                                                .obtenerReservasAprobadasPorCorreo(correo)
                                        }

                                        if (reservas.isNotEmpty()) {
                                            reservasParaElegir = reservas
                                            mostrarSelectorReservas = true
                                        } else {
                                            Toast.makeText(
                                                contexto,
                                                "No tienes reservas aprobadas",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        Log.e(
                                            "USO_SALA",
                                            "Error al consultar reservas",
                                            e
                                        )

                                        Toast.makeText(
                                            contexto,
                                            "Error al consultar tus reservas",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        }
                    )

                                if (mostrarSelectorReservas) {
                                    AlertDialog(
                                        onDismissRequest = {
                                            mostrarSelectorReservas = false
                                        },
                                        title = {
                                            Text(
                                                "Selecciona tu actividad",
                                                color = Black,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        text = {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .verticalScroll(rememberScrollState())
                                            ) {
                                                reservasParaElegir.forEach { reserva ->
                                                    Card(
                                                        onClick = {
                                                            mostrarSelectorReservas = false
                                                            abrirActividadSeleccionada(reserva.id)
                                                        },
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 5.dp),
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = Yellow
                                                        )
                                                    ) {
                                                        Column(
                                                            modifier = Modifier.padding(14.dp)
                                                        ) {
                                                            Text(
                                                                text = reserva.nombreActividad,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Black
                                                            )

                                                            Text(
                                                                text = "${reserva.fechaActividad} · " +
                                                                        "${reserva.horaInicio} - ${reserva.horaTermino}",
                                                                color = Black
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                        confirmButton = {
                                            TextButton(
                                                onClick = {
                                                    mostrarSelectorReservas = false
                                                }
                                            ) {
                                                Text("Cancelar", color = Black)
                                            }
                                        },
                                        containerColor = White
                                    )
                                }
                            }

                composable("calendarioRelator") {
                    HorariosScreen(
                        puedeEditar = false,
                        onSolicitarBloque = { salaId, fecha, horaInicio, horaTermino ->
                            Toast.makeText(
                                contexto,
                                "Solicitar sala $salaId - $fecha - $horaInicio - $horaTermino",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        onNuevoHorario = { salaId, dia, inicioMin ->
                            tabNavController.navigate(
                                GestionHorariosRutas.formularioHorario(
                                    horarioId = 0L,
                                    salaId = salaId,
                                    dia = dia?.name,
                                    inicioMin = inicioMin
                                )
                            )
                        },
                        onEditarHorario = { id ->
                            tabNavController.navigate(
                                GestionHorariosRutas.formularioHorario(id)
                            )
                        }
                    )
                }

                composable("salasRelator") {
                    BusquedaSalasScreen(
                        onReservas = {
                            tabNavController.navigate("reservas")
                        }
                    )
                }
                composable("reservas") {
                    ReservasScreen(
                        idUsuario = idUsuario,
                        onAtras = { tabNavController.navigate("calendarioSupervisor")},
                        onEnviar = { tabNavController.navigate("paginaRelator")},
                        onRegistrarAuditoria = onRegistrarAuditoria
                    )
                }

                composable("usoSala/{reservaId}") { entrada ->
                    val reservaId = entrada.arguments
                        ?.getString("reservaId")
                        ?.toLongOrNull()

                    if (reservaId != null) {
                        UsoSalaScreen(
                            reservaId = reservaId,
                            appState = appState,
                            onReservaCancelada = {
                                tabNavController.navigate("paginaRelator") {
                                    popUpTo("paginaRelator") {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }

                composable("notificaciones/false"){
                    NotificacionesScreen(
                        onVolver =  { tabNavController.popBackStack()},
                        navController = tabNavController,
                        esSupervisor = false,
                        correoUsuario = correoUsuario
                    )
                }



            }
        }
    }
}