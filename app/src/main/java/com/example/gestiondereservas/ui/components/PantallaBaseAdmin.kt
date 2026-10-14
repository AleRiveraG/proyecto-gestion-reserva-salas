package com.example.gestiondereservas.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gestiondereservas.data.AppState
import com.example.gestiondereservas.data.room.entidades.Area
import com.example.gestiondereservas.ui.views.GestionReservasScreen
import com.example.gestiondereservas.ui.views.SalasScreen
import com.example.gestiondereservas.ui.views.SupervisorScreen

@Composable
fun PantallaBase(
    navController: NavHostController,
    appState: AppState
) {
    val tabNavController = rememberNavController()

    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    val opcionSeleccionada = when (rutaActual) {
        "paginaSupervisor" -> 0
        "calendarioSupervisor" -> 1
        "gestion_salas" -> 2
        "gestionReservas" -> 3
        else -> 0
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar() },
        bottomBar = {
            NavBarAdmin(
                opcionSeleccionada,
                onInicio = { tabNavController.navigate("paginaSupervisor") },
                onCalendario = { tabNavController.navigate("") },
                onSalas = { tabNavController.navigate("gestion_salas") },
                onReservas = { tabNavController.navigate("gestionReserva") }
            ) },
        containerColor = MaterialTheme.colorScheme.secondary
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            NavHost(
                navController = tabNavController,
                startDestination = "paginaSupervisor",
            ) {
                composable("paginaSupervisor") {
                    SupervisorScreen(
                        appState = appState,
                        onHorarios = { },
                        onGestionSalas = { tabNavController.navigate("gestion_salas")},
                        onAuditoria = { },
                        onReportes = { },
                    )
                }

                composable("calendarioSupervisor") {

                }

                composable("horarios") {

                }

                composable("gestionReserva") {
                    GestionReservasScreen(appState)
                }

                composable("gestion_salas") {
                    SalasScreen(
                        areaCoordinador = Area.INFORMATICA,
                        onNuevaSala = {
                            navController.navigate(
                                "gestion_salas"
                            )
                        },
                        onEditarSala = { id ->
                            navController.navigate(
                                "gestion_salas/formulario/$id"                          )
                        }
                    )
                }
            }

        }
    }
}