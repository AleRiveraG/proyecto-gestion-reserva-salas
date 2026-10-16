package com.example.gestiondereservas.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gestiondereservas.ui.viewmodel.ReservaViewModel
import com.example.gestiondereservas.ui.views.RelatorScreen
import com.example.gestiondereservas.ui.views.ReservasScreen
import com.example.gestiondereservas.ui.views.UsoSalaScreen

@Composable
fun PantallaBaseRelator(
    navController: NavHostController,
    abrirReservas: Boolean = false
) {
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
        topBar = { TopBar( onNotificacionesClick = {navController.navigate("notificaciones/false")}) },
        bottomBar = {
            NavBar(
                opcionSeleccionada,
                onInicio = { tabNavController.navigate("paginaRelator") },
                onCalendario = { tabNavController.navigate("") },
                onSalas = { tabNavController.navigate("") },
            ) },
        containerColor = MaterialTheme.colorScheme.secondary
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ){
            NavHost(
                navController = tabNavController,
                startDestination = if (abrirReservas) "reservas" else "paginaRelator"
            ) {

                composable("paginaRelator") {
                    RelatorScreen(
                        onReservas = { tabNavController.navigate("reservas")},
                        onMiActividad = {
                            // navController.navigate("usoSala/$reservaId")  Pendiente: obtener el ID de una reserva aprobada
                        })
                }


                composable("reservas") {
                    ReservasScreen(
                        onAtras = { tabNavController.navigate("paginaRelator")},
                        onEnviar = { tabNavController.navigate("paginaRelator")} // TIENE QUE DEVOLVER A CALENDARIO
                    )
                }


            }
        }
    }
}