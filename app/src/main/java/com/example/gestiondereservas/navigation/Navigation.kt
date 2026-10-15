package com.example.gestiondereservas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gestiondereservas.data.AppState
import com.example.gestiondereservas.ui.components.PantallaBase
import com.example.gestiondereservas.ui.views.RelatorScreen
import com.example.gestiondereservas.ui.views.ReservasScreen
import com.example.gestiondereservas.ui.views.UsoSalaScreen

@Composable
fun appNavigation(navController: NavHostController, appState: AppState) {

    NavHost(
        navController = navController, startDestination =  "paginaRelator" //"login" baseSupervisor
    ) {
        composable ("login"){ }

        composable("paginaRelator") { RelatorScreen(appState = appState,
            onReservas = {navController.navigate("reservas")}, onMiActividad = {navController.navigate("usoSalaDemo")}) }
        composable("calendario"){  }
        composable("salasRelator"){  }
        composable("reservas"){ ReservasScreen(appState,
            onAtras = { navController.navigate("paginaRelator") },
            onEnviar = { navController.navigate("paginaRelator")} ) }

        composable("baseSupervisor"){
            PantallaBase(navController, appState) }

        composable("usoSalaDemo") { UsoSalaScreen()  }
    }
}

