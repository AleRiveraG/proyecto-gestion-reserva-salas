package com.example.gestiondereservas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

import com.example.gestiondereservas.data.AppState
import com.example.gestiondereservas.ui.components.PantallaBaseAdmin
import com.example.gestiondereservas.ui.components.PantallaBaseRelator
import com.example.gestiondereservas.ui.views.NotificacionesScreen
import com.example.gestiondereservas.ui.views.UsoSalaScreen

@Composable
fun appNavigation(
    navController: NavHostController,
    appState: AppState
) {

    NavHost(
        navController = navController,
        startDestination = "baseRelator"
    ) {

        composable("login") {
            // Pendiente de integración del login
        }

        // Pantalla principal del relator
        composable("baseRelator") {
            PantallaBaseRelator(navController)
        }

        // Formulario de reservas
        composable("baseRelator/reservas") {
            PantallaBaseRelator(
                navController = navController,
                abrirReservas = true
            )
        }

        // Pantalla principal del supervisor
        composable("baseSupervisor") {
            PantallaBaseAdmin(navController)
        }

        // Notificaciones según el rol
        composable("notificaciones/{esSupervisor}") { backStackEntry ->

            val esSupervisor = backStackEntry.arguments
                ?.getString("esSupervisor")
                ?.toBoolean() ?: false

            NotificacionesScreen(
                navController = navController,
                esSupervisor = esSupervisor,
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // Uso de sala con una reserva real
        composable(
            route = "usoSala/{reservaId}",
            arguments = listOf(
                navArgument("reservaId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val reservaId = backStackEntry.arguments
                ?.getLong("reservaId")
                ?: return@composable

            UsoSalaScreen(
                reservaId = reservaId,
                appState = appState
            )
        }
    }
}