package com.example.gestiondereservas.ui.components

import com.example.gestiondereservas.ui.views.UsoSalaScreen
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gestiondereservas.data.room.entidades.AuditoriaEntity
import com.example.gestiondereservas.ui.views.BusquedaSalasScreen
import com.example.gestiondereservas.ui.views.HorariosScreen
import com.example.gestiondereservas.ui.views.RelatorScreen
import com.example.gestiondereservas.ui.views.ReservasScreen
import com.example.gestiondereservas.ui.views.UsoSalaScreen
import com.example.gestiondereservas.data.AppState

@Composable
fun PantallaBaseRelator(
    navController: NavHostController,
    idUsuario: Long,
    onRegistrarAuditoria: (AuditoriaEntity) -> Unit = {},
    abrirReservas: Boolean = false,
    onCerrarSesion: () -> Unit = {},
    appState: AppState,
    onMiActividad: ((Long) -> Unit) -> Unit = {}
) {
    val contexto = LocalContext.current
    val tabNavController = rememberNavController()

    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    val opcionSeleccionada = when (rutaActual) {
        "paginaRelator" -> 0
        "calendarioRelator" -> 1
        "salasRelator" -> 2
        else -> 0
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                onNotificacionesClick = {
                    navController.navigate("notificaciones/false")

                },
                onCerrarSesion = onCerrarSesion
            )
        },
        bottomBar = {
            NavBar(
                opcionSeleccionada,
                onInicio = { tabNavController.navigate("paginaRelator") },
                onCalendario = { tabNavController.navigate("calendarioRelator") },
                onSalas = { tabNavController.navigate("salasRelator") },
            ) },
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
                            onMiActividad { reservaId ->
                                tabNavController.navigate("usoSala/$reservaId")
                            }
                        }
                    )
                }

                composable("calendarioRelator") {
                    HorariosScreen(
                        puedeEditar = false,
                        onSolicitarBloque = { salaId, fecha, horaInicio, horaTermino ->
                            Toast.makeText(
                                contexto,
                                "Solicitar sala $salaId · $fecha · $horaInicio - $horaTermino",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        onNuevoHorario = { salaId, dia, inicioMin ->
                            navController.navigate(
                                GestionHorariosRutas.formularioHorario(
                                    horarioId = 0L,
                                    salaId = salaId,
                                    dia = dia?.name,
                                    inicioMin = inicioMin
                                )
                            )
                        },
                        onEditarHorario = { id ->
                            navController.navigate(
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
                        onAtras = { tabNavController.navigate("paginaRelator")},
                        onEnviar = { tabNavController.navigate("paginaRelator")},
                        onRegistrarAuditoria = onRegistrarAuditoria
                    // TIENE QUE DEVOLVER A CALENDARIO
                    )
                }

                composable("usoSala/{reservaId}") { backStackEntry ->
                    val reservaId = backStackEntry.arguments
                        ?.getString("reservaId")
                        ?.toLongOrNull()

                    if (reservaId != null) {
                        UsoSalaScreen(
                            reservaId = reservaId,
                            appState = appState,
                            onReservaCancelada = {
                                tabNavController.navigate("paginaRelator")
                            }
                        )
                    }
                }
            }
        }
    }
}