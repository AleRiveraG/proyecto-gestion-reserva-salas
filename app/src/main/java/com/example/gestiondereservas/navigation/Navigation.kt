
package com.example.gestiondereservas.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.gestiondereservas.ui.components.PantallaBaseAdmin
import com.example.gestiondereservas.ui.components.PantallaBaseRelator
import com.example.gestiondereservas.ui.views.LoginScreen
import com.example.gestiondereservas.ui.views.RegistroScreen
import com.example.gestiondereservas.ui.views.RolRegistroUsuario
import com.example.gestiondereservas.ui.views.RolUsuario
import com.example.gestiondereservas.ui.views.NotificacionesScreen
import com.example.gestiondereservas.ui.views.usuariosRegistradosMemoria
import com.example.gestiondereservas.ui.views.passwordsUsuariosMemoria

@Composable
fun appNavigation(
    navController: NavHostController,
    abrirNotificaciones: Boolean = false
) {
    // Datos de la sesión actual
    var correoUsuario by remember { mutableStateOf<String?>(null) }
    var rolUsuario by remember { mutableStateOf<RolUsuario?>(null) }

    val esSupervisor = rolUsuario == RolUsuario.COORDINADOR

    // Abrir notificaciones desde una alerta del teléfono
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

        // LOGIN DEL COMPAÑERO
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

        // REGISTRO DEL COMPAÑERO
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

        // PANTALLAS EXISTENTES
        composable("baseRelator") {
            PantallaBaseRelator(navController)
        }

        composable("baseRelator/reservas") {
            PantallaBaseRelator(
                navController = navController,
                abrirReservas = true
            )
        }

        composable("baseSupervisor") {
            PantallaBaseAdmin(navController)
        }

        // TUS NOTIFICACIONES
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
