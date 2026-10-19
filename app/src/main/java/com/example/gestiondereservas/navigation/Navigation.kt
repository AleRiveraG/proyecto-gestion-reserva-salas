package com.example.gestiondereservas.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gestiondereservas.data.AppState
import com.example.gestiondereservas.data.repository.AuditoriaRepository
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.ui.components.PantallaBaseAdmin
import com.example.gestiondereservas.ui.components.PantallaBaseRelator
import com.example.gestiondereservas.ui.viewmodel.AuditoriaViewModel
import com.example.gestiondereservas.ui.views.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.gestiondereservas.ui.views.UsoSalaScreen

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
fun appNavigation(navController: NavHostController, appState: AppState) {
    var correoUsuario by remember { mutableStateOf<String?>(null) }
    val auditoriaViewModel = rememberAuditoriaViewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                alNavegarARegistro = { navController.navigate("registro") },
                alNavegarARelator = {
                    navController.navigate("baseRelator") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                alNavegarASupervisor = {
                    navController.navigate("baseSupervisor") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                alIniciarSesionExitoso = { correo, _, _ ->
                    correoUsuario = correo.trim().lowercase()
                }
            )
        }

        composable("registro") {
            RegistroScreen(
                alRegistrarExitoso = { _, correo, password, rol ->
                    val correoLimpio = correo.trim().lowercase()
                    val rolLogin = if (rol == RolRegistroUsuario.RELATOR) {
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
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            PantallaBaseRelator(
                navController = navController,

                appState = appState,

                onRegistrarAuditoria = { evento ->
                    auditoriaViewModel.registrarReserva(evento)
                },

                onMiActividad = { abrirUsoSala ->
                    val correo = correoUsuario

                    if (correo.isNullOrBlank()) {
                        Toast.makeText(
                            context,
                            "No se encontró el usuario de la sesión",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        scope.launch {
                            try {
                                val reservas = withContext(Dispatchers.IO) {
                                    AppDatabase.getInstance(context)
                                        .reservaDao()
                                        .obtenerReservasAprobadasPorCorreo(correo)
                                }

                                val reserva = reservas.firstOrNull()

                                if (reserva != null) {
                                    abrirUsoSala(reserva.id)
                                } else {
                                    Toast.makeText(
                                        context,
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
                                    context,
                                    "Error al consultar tus reservas",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                },

                onCerrarSesion = {
                    correoUsuario = null

                    navController.navigate("login") {
                        popUpTo("baseRelator") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
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

                    navController.navigate("login") {
                        popUpTo("baseSupervisor") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("usoSala/{reservaId}") { backStackEntry ->

            val reservaId = backStackEntry.arguments
                ?.getString("reservaId")
                ?.toLongOrNull()

            if (reservaId != null) {
                UsoSalaScreen(
                    reservaId = reservaId,
                    appState = appState
                )
            }
        }
    }
}