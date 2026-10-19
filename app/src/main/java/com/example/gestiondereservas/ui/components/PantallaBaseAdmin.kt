package com.example.gestiondereservas.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestiondereservas.R
import com.example.gestiondereservas.data.*
import com.example.gestiondereservas.data.room.*
import com.example.gestiondereservas.ui.viewmodel.*
import com.example.gestiondereservas.ui.views.*


open class AuditoriaViewModel : ViewModel()
open class GestionReservasViewModel : ViewModel()

enum class DiaSemana {
    LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO
}

@Composable
fun SupervisorScreen(
    onHorarios: () -> Unit = {},
    onGestionSalas: () -> Unit = {},
    onAuditoria: () -> Unit = {},
    onReportes: () -> Unit = {},
    onReservas: () -> Unit = {},
    viewModel: GestionReservasViewModel = viewModel()
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Panel de Supervisor")
    }
}

@Composable
fun HorariosScreen(
    puedeEditar: Boolean = true,
    onNuevoHorario: (salaId: Long, dia: DiaSemana?, inicioMin: Int) -> Unit = { _, _, _ -> },
    onEditarHorario: (id: Long) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Horarios")
    }
}

@Composable
fun GestionReservasScreen(
    onRegistrarAuditoria: (Any) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Gestión de Reservas")
    }
}

@Composable
fun AuditoriaScreen(
    onAtras: () -> Unit = {},
    viewModel: AuditoriaViewModel = viewModel()
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Auditoría")
    }
}

@Composable
fun SalasScreen(
    onNuevaSala: () -> Unit = {},
    onEditarSala: (Long) -> Unit = {},
    onRegistrarAuditoria: (Any) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Salas")
    }
}

@Composable
fun FormularioSalaScreen(
    salaId: Long,
    onVolver: () -> Unit = {},
    onRegistrarAuditoria: (Any) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Formulario Sala")
    }
}

@Composable
fun FormularioHorarioScreen(
    horarioId: Long,
    salaInicialId: Long = 0L,
    diaInicial: String? = null,
    inicioInicialMin: Int = -1,
    onVolver: () -> Unit = {},
    onRegistrarAuditoria: (Any) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Formulario Horario")
    }
}



object GestionSalasRutas {
    const val SALAS = "gestion_salas"
    const val FORMULARIO_SALA = "gestion_salas/formulario/{salaId}"

    fun formularioSala(salaId: Long) = "gestion_salas/formulario/$salaId"
}

object GestionHorariosRutas {
    const val HORARIOS = "gestion_horarios"
    const val FORMULARIO_HORARIO =
        "gestion_horarios/formulario/{horarioId}?salaId={salaId}&dia={dia}&inicio={inicio}"

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

//PantallaBaseAdmin
@Composable
fun PantallaBaseAdmin(
    navController: NavHostController,
    auditoriaViewModel: AuditoriaViewModel = viewModel(),
    onRegistrarAuditoria: (Any) -> Unit = {}
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
                seleccion = opcionSeleccionada,
                onInicio = { tabNavController.navigate("paginaSupervisor") },
                onCalendario = { tabNavController.navigate("calendarioSupervisor") },
                onSalas = { tabNavController.navigate("gestion_salas") },
                onReservas = { tabNavController.navigate("gestionReserva") }
            )
        },
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
                    val gestionReservasVm: GestionReservasViewModel = viewModel()
                    SupervisorScreen(
                        onHorarios = { tabNavController.navigate("calendarioSupervisor") },
                        onGestionSalas = { tabNavController.navigate("gestion_salas") },
                        onAuditoria = { tabNavController.navigate("auditoria") },
                        onReportes = { tabNavController.navigate("reportes") },
                        onReservas = { tabNavController.navigate("gestionReserva") },
                        viewModel = gestionReservasVm
                    )
                }

                composable("calendarioSupervisor") {
                    HorariosScreen(
                        puedeEditar = true,
                        onNuevoHorario = { salaId: Long, dia: DiaSemana?, inicioMin: Int ->
                            tabNavController.navigate(
                                GestionHorariosRutas.formularioHorario(
                                    horarioId = 0L,
                                    salaId = salaId,
                                    dia = dia?.name,
                                    inicioMin = inicioMin
                                )
                            )
                        },
                        onEditarHorario = { id: Long ->
                            tabNavController.navigate(GestionHorariosRutas.formularioHorario(id))
                        }
                    )
                }

                composable("gestionReserva") {
                    GestionReservasScreen(
                        onRegistrarAuditoria = { evento ->
                            onRegistrarAuditoria(evento)
                        }
                    )
                }

                composable("auditoria") {
                    AuditoriaScreen(
                        onAtras = { tabNavController.navigate("paginaSupervisor") },
                        viewModel = auditoriaViewModel
                    )
                }

                composable("gestion_salas") {
                    SalasScreen(
                        onNuevaSala = {
                            tabNavController.navigate(GestionSalasRutas.formularioSala(0L))
                        },
                        onEditarSala = { id: Long ->
                            tabNavController.navigate(GestionSalasRutas.formularioSala(id))
                        },
                        onRegistrarAuditoria = { evento ->
                            onRegistrarAuditoria(evento)
                        }
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
                        onRegistrarAuditoria = { evento ->
                            onRegistrarAuditoria(evento)
                        }
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
                        onVolver = { navController.popBackStack() },
                        onRegistrarAuditoria = { evento ->
                            onRegistrarAuditoria(evento)
                        }
                    )
                }

                composable("reportes") {
                    ReporteScreen()
                }
            }
        }
    }
}