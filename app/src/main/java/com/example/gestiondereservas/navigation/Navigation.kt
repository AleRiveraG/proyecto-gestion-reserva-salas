package com.example.gestiondereservas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestiondereservas.ui.components.PantallaBaseAdmin
import com.example.gestiondereservas.ui.components.PantallaBaseRelator
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

                    //Guardar rol y contraseña en los almacenes de memoria
                    com.example.gestiondereservas.ui.views.usuariosRegistradosMemoria[correoLimpio] = rolLogin
                    com.example.gestiondereservas.ui.views.passwordsUsuariosMemoria[correoLimpio] = pass // <<< Guardar contraseña registrada

                    navController.popBackStack()
                }
            )
        }

        composable("baseRelator") {
            PantallaBaseRelator(navController)
        }

        composable("baseSupervisor") {
            PantallaBaseAdmin(navController)
        }

        composable("relator") {
            RelatorScreen(
                onReservas = { /* Acción al presionar reservas */ }
            )
        }

        composable("supervisor") {
            SupervisorScreen(
                onHorarios = { /* Acción */ },
                onGestionSalas = { /* Acción */ },
                onAuditoria = { /* Acción */ },
                onReportes = { /* Acción hacia rama reportes */ }
            )
        }
    }
}
