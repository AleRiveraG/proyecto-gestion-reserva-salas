package com.example.gestiondereservas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestiondereservas.data.room.entidades.Area
import com.example.gestiondereservas.ui.components.PantallaBase
import com.example.gestiondereservas.ui.views.FormularioSalaScreen
import com.example.gestiondereservas.ui.views.SalasScreen

object GestionSalasRutas {
    const val SALAS = "gestion_salas"
    const val FORMULARIO_SALA = "gestion_salas/formulario/{salaId}"

    fun formularioSala(salaId: Long) =
        "gestion_salas/formulario/$salaId"
}

@Composable
fun GestionSalasNavigation(areaCoordinador: Area = Area.INFORMATICA) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = GestionSalasRutas.SALAS
    ) {
        composable(GestionSalasRutas.SALAS) {
            PantallaBase(itemSeleccionado = 2) {
                SalasScreen(
                    areaCoordinador = areaCoordinador,
                    onNuevaSala = {
                        navController.navigate(
                            GestionSalasRutas.formularioSala(0L)
                        )
                    },
                    onEditarSala = { id ->
                        navController.navigate(
                            GestionSalasRutas.formularioSala(id)
                        )
                    }
                )
            }
        }

        composable(
            route = GestionSalasRutas.FORMULARIO_SALA,
            arguments = listOf(
                navArgument("salaId") {
                    type = NavType.LongType
                }
            )
        ) { entrada ->
            val salaId = entrada.arguments?.getLong("salaId") ?: 0L

            PantallaBase(itemSeleccionado = 2) {
                FormularioSalaScreen(
                    salaId = salaId,
                    areaCoordinador = areaCoordinador,
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}