package com.example.gestiondereservas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestiondereservas.data.repository.AuditoriaRepository
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.ui.components.PantallaBaseAdmin
import com.example.gestiondereservas.ui.components.PantallaBaseRelator
import com.example.gestiondereservas.ui.viewmodel.AuditoriaViewModel
import com.example.gestiondereservas.ui.viewmodel.GestionReservasViewModel
import com.example.gestiondereservas.ui.views.LoginScreen
import com.example.gestiondereservas.ui.views.RegistroScreen
import com.example.gestiondereservas.ui.views.RelatorScreen
import com.example.gestiondereservas.ui.views.RolRegistroUsuario
import com.example.gestiondereservas.ui.views.RolUsuario
import com.example.gestiondereservas.ui.views.SupervisorScreen

@Composable
fun rememberAuditoriaViewModel(): AuditoriaViewModel {
    val contexto = LocalContext.current.applicationContext
    return viewModel(
        factory = viewModelFactory {
            initializer {
                val dao = AppDatabase.getInstance(contexto).auditoriaDao()
                val repository = AuditoriaRepository(dao)
                AuditoriaViewModel(repository)
            }
        }
    )
}
@Composable
fun AppNavigation(navController: NavHostController) {

    val auditoriaViewModel: AuditoriaViewModel = rememberAuditoriaViewModel()

    NavHost(
        navController = navController, startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                alNavegarARegistro = { navController.navigate("registro") },
                alNavegarARelator = { idUsuario ->
                    navController.navigate("baseRelator/$idUsuario") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                alNavegarASupervisor = { idUsuario ->
                    navController.navigate("baseSupervisor/$idUsuario") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("registro") {
            RegistroScreen(
                alRegistrarExitoso = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            "baseRelator/{idUsuario}",
            arguments = listOf(navArgument("idUsuario") { type = NavType.LongType})
            ) { backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getLong("idUsuario") ?: 0

            PantallaBaseRelator(
                navController,
                idUsuario,
                onRegistrarAuditoria = { evento ->
                    auditoriaViewModel.registrarReserva(evento)
                })
        }

        composable(
            "baseSupervisor/{idUsuario}",
            arguments = listOf(navArgument("idUsuario") { type = NavType.LongType})
            ){backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getLong("idUsuario") ?: 0
            PantallaBaseAdmin(
                navController,
                idUsuario,
                onRegistrarAuditoria = { evento ->
                    auditoriaViewModel.registrarReserva(evento)
                },
                auditoriaViewModel = auditoriaViewModel
            )
        }

    }
}