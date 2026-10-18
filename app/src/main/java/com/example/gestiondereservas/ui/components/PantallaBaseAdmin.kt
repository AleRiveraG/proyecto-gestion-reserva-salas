package com.example.gestiondereservas.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.gestiondereservas.ui.views.FormularioHorarioScreen
import com.example.gestiondereservas.ui.views.FormularioSalaScreen
import com.example.gestiondereservas.ui.views.GestionReservasScreen
import com.example.gestiondereservas.ui.views.HorariosScreen
import com.example.gestiondereservas.ui.views.SalasScreen
import com.example.gestiondereservas.ui.views.SupervisorScreen
import com.example.gestiondereservas.ui.views.rememberGestionReservasViewModel

object GestionSalasRutas {
    const val SALAS = "gestion_salas"
    const val FORMULARIO_SALA = "gestion_salas/formulario/{salaId}"

    fun formularioSala(salaId: Long) = "gestion_salas/formulario/$salaId"
}

object GestionHorariosRutas {
    const val HORARIOS = "gestion_horarios"
    const val FORMULARIO_HORARIO =
        "gestion_horarios/formulario/{horarioId}?salaId={salaId}&dia={dia}&inicio={inicio}"

    //salaId, dia e inicio sirven para abrir un horario nuevo ya rellenado desde la grilla
    fun formularioHorario(
        horarioId: Long,
        salaId: Long = 0L,
        dia: String? = null,
        inicioMin: Int = -1
    ): String {
        val base = "gestion_horarios/formulario/$horarioId?salaId=$salaId&inicio=$inicioMin"
        return if (dia != null) "$base&dia=$dia" else base
    }
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
                onCalendario = { tabNavController.navigate("calendarioSupervisor") },
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
                        onHorarios = { tabNavController.navigate("calendarioSupervisor")},
                        onGestionSalas = { tabNavController.navigate("gestion_salas")},
                        onAuditoria = { tabNavController.navigate("auditoria")},
                        onReportes = { },
                        onReservas = { tabNavController.navigate("gestionReserva")},
                        viewModel = rememberGestionReservasViewModel()
                    )
                }

                composable("calendarioSupervisor") {
                    HorariosScreen(
                        puedeEditar = true,
                        onNuevoHorario = { salaId, dia, inicioMin ->
                            tabNavController.navigate(
                                GestionHorariosRutas.formularioHorario(
                                    horarioId = 0L,
                                    salaId = salaId,
                                    dia = dia?.name,
                                    inicioMin = inicioMin
                                )
                            )
                        },
                        onEditarHorario = { id ->
                            tabNavController.navigate(GestionHorariosRutas.formularioHorario(id))
                        }
                    )
                }

                composable("gestionReserva") {
                    GestionReservasScreen(
                        onRegistrarAuditoria = onRegistrarAuditoria
                    )
                }

                composable("auditoria") {
                    AuditoriaScreen(
                        onAtras = { tabNavController.navigate("paginaSupervisor")},
                        auditoriaViewModel
                    )
                }

                composable("gestion_salas") {
                    SalasScreen(
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
                        onVolver = { tabNavController.popBackStack() },
                        onRegistrarAuditoria = onRegistrarAuditoria
                    )
                }

                composable(
                    route = GestionHorariosRutas.FORMULARIO_HORARIO,
                    arguments = listOf(
                        navArgument("horarioId") { type = NavType.LongType },
                        navArgument("salaId") {
                            type = NavType.LongType
                            defaultValue = 0L
                        },
                        navArgument("dia") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                        navArgument("inicio") {
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    )
                ) { entrada ->
                    val horarioId = entrada.arguments?.getLong("horarioId") ?: 0L
                    val salaId = entrada.arguments?.getLong("salaId") ?: 0L
                    val dia = entrada.arguments?.getString("dia")
                    val inicio = entrada.arguments?.getInt("inicio") ?: -1
                    FormularioHorarioScreen(
                        horarioId = horarioId,
                        salaInicialId = salaId,
                        diaInicial = dia,
                        inicioInicialMin = inicio,
                        onVolver = { navController.popBackStack() }
                    )
                }

                //FALTA REPORTES
            }

        }
    }
}