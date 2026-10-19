
package com.example.gestiondereservas.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.repository.AuditoriaRepository
import com.example.gestiondereservas.ui.components.PantallaBaseAdmin
import com.example.gestiondereservas.ui.components.PantallaBaseRelator
import com.example.gestiondereservas.ui.viewmodel.AuditoriaViewModel
import com.example.gestiondereservas.ui.views.LoginScreen
import com.example.gestiondereservas.ui.views.NotificacionesScreen
import com.example.gestiondereservas.ui.views.RegistroScreen
import com.example.gestiondereservas.ui.views.RolRegistroUsuario
import com.example.gestiondereservas.ui.views.RolUsuario
import com.example.gestiondereservas.ui.views.passwordsUsuariosMemoria
import com.example.gestiondereservas.ui.views.usuariosRegistradosMemoria

@Composable
fun rememberAuditoriaViewModel(): AuditoriaViewModel {
    val contexto = LocalContext.current.applicationContext

    return viewModel(
        factory = viewModelFactory {
            initializer {
                val dao = AppDatabase.getInstance(contexto).auditoriaDao()
                val repository = AuditoriaRepository(dao)
                AuditoriaViewModel(repository)
            }
        }
    )
}

@Composable
fun appNavigation(
    navController: NavHostController,
    abrirNotificaciones: Boolean = false
) {
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
            PantallaBaseRelator(
                navController = navController,
                onRegistrarAuditoria = { evento ->
                    auditoriaViewModel.registrarReserva(evento)
                }
            )
        }

        composable("baseRelator/reservas") {
            PantallaBaseRelator(
                navController = navController,
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
                }
            )
        }

        composable("notificaciones/{esSupervisor}") { backStackEntry ->
            val rolDeRuta = backStackEntry.arguments
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
