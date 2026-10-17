package com.example.gestiondereservas.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                alNavegarARegistro = { navController.navigate("registro") },
                alNavegarARelator = {
                    navController.navigate("baseRelator") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                alNavegarASupervisor = {
                    navController.navigate("baseSupervisor") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("registro") {
            RegistroScreen(
                alRegistrarExitoso = { nombre, correo, pass, rol ->
                    val correoLimpio = correo.trim().lowercase()
                    val rolLogin = if (rol == RolRegistroUsuario.RELATOR) RolUsuario.RELATOR else RolUsuario.COORDINADOR

                    // Guardar rol y contraseña en los almacenes de memoria
                    com.example.gestiondereservas.ui.views.usuariosRegistradosMemoria[correoLimpio] = rolLogin
                    com.example.gestiondereservas.ui.views.passwordsUsuariosMemoria[correoLimpio] = pass

                    navController.popBackStack()
                }
            )
        }

        composable("baseRelator") {
            PantallaBaseRelator(navController)
        }

        //Suplir parámetros requeridos por PantallaBaseAdmin
        composable("baseSupervisor") {
            val auditoriaVm: AuditoriaViewModel = viewModel()
            PantallaBaseAdmin(
                navController = navController,
                auditoriaViewModel = auditoriaVm,
                onRegistrarAuditoria = { _ -> } // <<< MODIFICACIÓN: Se cambió a un solo parámetro (AuditoriaEntity) -> Unit
            )
        }

        //Suplir parámetros requeridos por RelatorScreen
        composable("relator") {
            RelatorScreen(
                onReservas = { /* Acción al presionar reservas */ },
                onBuscarSala = { /* Acción al buscar sala */ },
                onCalendario = { /* Acción al abrir calendario */ },
                onActividad = { /* Acción al ver actividad */ }
            )
        }

        //Suplir parámetros requeridos por SupervisorScreen
        composable("supervisor") {
            val supervisorVm: GestionReservasViewModel = viewModel()
            SupervisorScreen(
                onHorarios = { /* Acción */ },
                onGestionSalas = { /* Acción */ },
                onAuditoria = { /* Acción */ },
                onReportes = { /* Acción hacia rama reportes */ },
                onReservas = { /* Acción reservas */ },
                viewModel = supervisorVm
            )
        }
    }
}