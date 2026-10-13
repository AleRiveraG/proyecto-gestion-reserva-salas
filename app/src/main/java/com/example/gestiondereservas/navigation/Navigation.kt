package com.example.gestiondereservas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gestiondereservas.data.AppState
import com.example.gestiondereservas.ui.views.GestionReservasScreen
import com.example.gestiondereservas.ui.views.LoginBody
import com.example.gestiondereservas.ui.views.RelatorScreen
import com.example.gestiondereservas.ui.views.ReservasScreen
import com.example.gestiondereservas.ui.views.SupervisorScreen
import com.example.gestiondereservas.ui.views.UsoSalaScreen
import com.example.gestiondereservas.ui.views.NotificacionesScreen


@Composable
fun appNavigation(navController: NavHostController, appState: AppState) {
    NavHost(
        navController = navController, startDestination =  "paginaRelator" //"login"
    ) {
        composable ("login"){ }

        composable("paginaRelator") { RelatorScreen(navController, appState) }
        composable("calendario"){  }
        composable("salasRelator"){  }
        composable("reservas"){ ReservasScreen(navController, appState) }

        composable("paginaSupervisor"){ SupervisorScreen(appState) }
        composable("gestionReservas"){ GestionReservasScreen(appState) }
        composable("calendarioSupervisor"){ }
        composable("salasSupervisor"){  }
        composable("auditoria"){ }
        composable("reportes"){  }
        composable("horarios"){  }

        composable("notificaciones") { NotificacionesScreen() }
        composable("usoSala/{reservaId}",
            arguments = listOf(
                navArgument("reservaId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val reservaId = backStackEntry.arguments?.getLong("reservaId") ?: return@composable

            UsoSalaScreen(reservaId = reservaId, appState = appState)
        }

    }
}

