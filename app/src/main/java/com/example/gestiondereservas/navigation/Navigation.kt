package com.example.gestiondereservas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestiondereservas.ui.components.PantallaBaseAdmin
import com.example.gestiondereservas.ui.components.PantallaBaseRelator
//import com.example.gestiondereservas.ui.viewmodel.GestionReservasViewModel
//import com.example.gestiondereservas.ui.viewmodel.ReservaViewModel
import com.example.gestiondereservas.ui.views.LoginScreen
import com.example.gestiondereservas.ui.views.RegistroScreen
//import com.example.gestiondereservas.ui.views.RelatorScreen
import com.example.gestiondereservas.ui.views.ReservasScreen

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "login" // <<< MODIFICADO (antes apuntaba a otra ruta)
    ) {
        composable("login") {
            LoginScreen(
                alNavegarARegistro = { navController.navigate("registro") },
                alNavegarARelator = {
                    navController.navigate("relator") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                alNavegarASupervisor = {
                    navController.navigate("supervisor") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("registro") {
            RegistroScreen(
                alRegistrarExitoso = { _, _, _, _ ->
                    navController.popBackStack()
                }
            )
        }
        composable("baseRelator") {
            PantallaBaseRelator(navController)
        }

        composable("baseSupervisor"){
            PantallaBaseAdmin(navController)
        }
    }
}
