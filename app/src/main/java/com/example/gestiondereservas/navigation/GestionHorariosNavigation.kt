package com.example.gestiondereservas.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestiondereservas.ui.components.PantallaBaseAdmin
import com.example.gestiondereservas.ui.views.FormularioHorarioScreen
import com.example.gestiondereservas.ui.views.HorariosScreen
/*
//rutas para las funcionalidades de gestion de horarios
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
fun GestionHorariosNavigation(puedeEditar: Boolean = true) {
    val navController = rememberNavController()
    val contexto = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = GestionHorariosRutas.HORARIOS
    ) {
        composable(GestionHorariosRutas.HORARIOS) {
            PantallaBaseAdmin(itemSeleccionado = 2) {
                HorariosScreen(
                    puedeEditar = puedeEditar,
                    onSolicitarBloque = { salaId, fecha, horaInicio, horaTermino ->
                        Toast.makeText(
                            contexto,
                            "Solicitar sala $salaId · $fecha · $horaInicio - $horaTermino",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    onNuevoHorario = { salaId, dia, inicioMin ->
                        navController.navigate(
                            GestionHorariosRutas.formularioHorario(
                                horarioId = 0L,
                                salaId = salaId,
                                dia = dia?.name,
                                inicioMin = inicioMin
                            )
                        )
                    },
                    onEditarHorario = { id ->
                        navController.navigate(GestionHorariosRutas.formularioHorario(id))
                    }
                )
            }
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
            PantallaBaseAdmin(itemSeleccionado = 2) {
                FormularioHorarioScreen(
                    horarioId = horarioId,
                    salaInicialId = salaId,
                    diaInicial = dia,
                    inicioInicialMin = inicio,
                    onVolver = { navController.popBackStack() }
                )
            }
        }
    }
}
 */