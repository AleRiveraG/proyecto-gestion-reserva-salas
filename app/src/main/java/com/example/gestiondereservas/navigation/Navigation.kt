
package com.example.gestiondereservas.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gestiondereservas.data.AppState
import com.example.gestiondereservas.data.repository.AuditoriaRepository
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.ui.components.PantallaBaseAdmin
import com.example.gestiondereservas.ui.components.PantallaBaseRelator
import com.example.gestiondereservas.ui.theme.Black
import com.example.gestiondereservas.ui.theme.White
import com.example.gestiondereservas.ui.theme.Yellow
import com.example.gestiondereservas.ui.viewmodel.AuditoriaViewModel
import com.example.gestiondereservas.ui.views.LoginScreen
import com.example.gestiondereservas.ui.views.NotificacionesScreen
import com.example.gestiondereservas.ui.views.RegistroScreen
import com.example.gestiondereservas.ui.views.RolRegistroUsuario
import com.example.gestiondereservas.ui.views.RolUsuario
import com.example.gestiondereservas.ui.views.UsoSalaScreen
import com.example.gestiondereservas.ui.views.passwordsUsuariosMemoria
import com.example.gestiondereservas.ui.views.usuariosRegistradosMemoria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun rememberAuditoriaViewModel(): AuditoriaViewModel {
    val contexto = LocalContext.current.applicationContext

    return viewModel(
        factory = viewModelFactory {
            initializer {
                val dao = AppDatabase.getInstance(contexto).auditoriaDao()
                AuditoriaViewModel(AuditoriaRepository(dao))
            }
        }
    )
}

@Composable
fun appNavigation(
    navController: NavHostController,
    appState: AppState,
    abrirNotificaciones: Boolean = false
) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()
    val auditoriaViewModel = rememberAuditoriaViewModel()

    var correoUsuario by remember { mutableStateOf<String?>(null) }
    var rolUsuario by remember { mutableStateOf<RolUsuario?>(null) }

    val esSupervisor = rolUsuario == RolUsuario.COORDINADOR

    LaunchedEffect(abrirNotificaciones, correoUsuario, rolUsuario) {
        if (abrirNotificaciones && correoUsuario != null) {
            navController.navigate("notificaciones/$esSupervisor") {
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                alNavegarARegistro = {
                    navController.navigate("registro")
                },
                alIniciarSesionExitoso = { correo, _, rol ->
                    correoUsuario = correo.trim().lowercase()
                    rolUsuario = rol
                },
                alNavegarARelator = {
                    navController.navigate("baseRelator") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                alNavegarASupervisor = {
                    navController.navigate("baseSupervisor") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("registro") {
            RegistroScreen(
                alRegistrarExitoso = { _, correo, password, rol ->
                    val correoLimpio = correo.trim().lowercase()

                    val rolLogin =
                        if (rol == RolRegistroUsuario.RELATOR) {
                            RolUsuario.RELATOR
                        } else {
                            RolUsuario.COORDINADOR
                        }

                    usuariosRegistradosMemoria[correoLimpio] = rolLogin
                    passwordsUsuariosMemoria[correoLimpio] = password

                    navController.popBackStack()
                }
            )
        }

        composable("baseRelator") {
            var reservasParaElegir by remember {
                mutableStateOf<List<ReservaEntity>>(emptyList())
            }

            var mostrarSelectorReservas by remember {
                mutableStateOf(false)
            }

            var abrirActividadSeleccionada by remember {
                mutableStateOf<(Long) -> Unit>({})
            }

            PantallaBaseRelator(
                navController = navController,
                appState = appState,
                correoUsuario = correoUsuario,
                onRegistrarAuditoria = { evento ->
                    auditoriaViewModel.registrarReserva(evento)
                },
                onMiActividad = { abrirActividad ->
                    abrirActividadSeleccionada = abrirActividad

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
                },
                onCerrarSesion = {
                    correoUsuario = null
                    rolUsuario = null

                    navController.navigate("login") {
                        popUpTo("baseRelator") { inclusive = true }
                        launchSingleTop = true
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

        composable("baseRelator/reservas") {
            PantallaBaseRelator(
                navController = navController,
                appState = appState,
                correoUsuario = correoUsuario,
                abrirReservas = true,
                onRegistrarAuditoria = { evento ->
                    auditoriaViewModel.registrarReserva(evento)
                }
            )
        }

        composable("baseSupervisor") {
            PantallaBaseAdmin(
                navController = navController,
                auditoriaViewModel = auditoriaViewModel,
                onRegistrarAuditoria = { evento ->
                    auditoriaViewModel.registrarReserva(evento)
                },
                onCerrarSesion = {
                    correoUsuario = null
                    rolUsuario = null

                    navController.navigate("login") {
                        popUpTo("baseSupervisor") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("baseSupervisor/reservas") {
            PantallaBaseAdmin(
                navController = navController,
                auditoriaViewModel = auditoriaViewModel,
                onRegistrarAuditoria = { evento ->
                    auditoriaViewModel.registrarReserva(evento)
                },
                abrirReservas = true
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
                        navController.navigate("baseRelator") {
                            popUpTo("baseRelator") {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        composable("notificaciones/{esSupervisor}") { entrada ->
            val rolDeRuta = entrada.arguments
                ?.getString("esSupervisor")
                ?.toBoolean() ?: false

            NotificacionesScreen(
                navController = navController,
                esSupervisor = rolDeRuta,
                correoUsuario = correoUsuario,
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}
