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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestiondereservas.data.AppState
import com.example.gestiondereservas.data.room.entidades.Area
import com.example.gestiondereservas.data.room.entidades.AuditoriaEntity
import com.example.gestiondereservas.ui.viewmodel.AuditoriaViewModel
import com.example.gestiondereservas.ui.viewmodel.GestionReservasViewModel
import com.example.gestiondereservas.ui.views.AuditoriaScreen
import com.example.gestiondereservas.ui.views.FormularioSalaScreen
import com.example.gestiondereservas.ui.views.GestionReservasScreen
import com.example.gestiondereservas.ui.views.SalasScreen
import com.example.gestiondereservas.ui.views.SupervisorScreen
import com.example.gestiondereservas.ui.views.rememberGestionReservasViewModel

object GestionSalasRutas {
    const val SALAS = "gestion_salas"
    const val FORMULARIO_SALA = "gestion_salas/formulario/{salaId}"

    fun formularioSala(salaId: Long) = "gestion_salas/formulario/$salaId"
}
@Composable
fun PantallaBaseAdmin(
    navController: NavHostController,
    auditoriaViewModel: AuditoriaViewModel,
    onRegistrarAuditoria: (AuditoriaEntity) -> Unit
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
                        onHorarios = { },
                        onGestionSalas = { tabNavController.navigate("gestion_salas")},
                        onAuditoria = { tabNavController.navigate("auditoria")},
                        onReportes = { },
                        onReservas = { tabNavController.navigate("gestionReserva")},
                        viewModel = rememberGestionReservasViewModel()
                    )
                }

                composable("calendarioSupervisor") {

                }

                composable("horarios") {

                }

                composable("gestionReserva") {
                    GestionReservasScreen()
                }

                composable("auditoria") {
                    AuditoriaScreen(
                        onAtras = { tabNavController.navigate("paginaSupervisor")},
                        auditoriaViewModel
                    )
                }

                composable("gestion_salas") {
                    SalasScreen(
                        areaCoordinador = Area.INFORMATICA,
                        onNuevaSala = {
                            tabNavController.navigate(GestionSalasRutas.formularioSala(0L))
                        },
                        onEditarSala = { id ->
                            tabNavController.navigate(GestionSalasRutas.formularioSala(id))
                        },
                        onRegistrarAuditoria = onRegistrarAuditoria
                    )
                }

                composable(
                    route = GestionSalasRutas.FORMULARIO_SALA,
                    arguments = listOf(navArgument("salaId") { type = NavType.LongType })
                ) { entrada ->
                    val salaId = entrada.arguments?.getLong("salaId") ?: 0L
                        FormularioSalaScreen(
                            salaId = salaId,
                            onVolver = { navController.popBackStack() },
                            onRegistrarAuditoria = onRegistrarAuditoria
                        )
                }
            }

        }
    }
}