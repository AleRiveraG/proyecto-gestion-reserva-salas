package com.example.gestiondereservas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gestiondereservas.ui.components.PantallaBaseAdmin
import com.example.gestiondereservas.ui.components.PantallaBaseRelator
import com.example.gestiondereservas.ui.viewmodel.GestionReservasViewModel
import com.example.gestiondereservas.ui.viewmodel.ReservaViewModel
import com.example.gestiondereservas.ui.views.RelatorScreen
import com.example.gestiondereservas.ui.views.ReservasScreen

@Composable
fun appNavigation(navController: NavHostController) {

    NavHost(
        navController = navController, startDestination = "baseSupervisor" //"baseRelator" //"login"
    ) {
        composable ("login"){ }

        composable("baseRelator") {
            PantallaBaseRelator(navController)
        }

        composable("baseSupervisor"){
            PantallaBaseAdmin(navController) }

    }
}

