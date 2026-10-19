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
fun appNavigation(navController: NavHostController) {

    val auditoriaViewModel: AuditoriaViewModel = rememberAuditoriaViewModel()

    NavHost(
        navController = navController, startDestination = "baseRelator" //"baseRelator" //"login"
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
            PantallaBaseRelator(navController,
                onRegistrarAuditoria = { evento ->
                    auditoriaViewModel.registrarReserva(evento)
                })
        }

        composable("baseSupervisor"){
            PantallaBaseAdmin(navController,
                onRegistrarAuditoria = { evento ->
                    auditoriaViewModel.registrarReserva(evento)
                },
                auditoriaViewModel = auditoriaViewModel
            )
        }

    }
}