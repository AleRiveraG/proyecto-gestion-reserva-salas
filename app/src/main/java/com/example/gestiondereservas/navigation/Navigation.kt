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
import com.example.gestiondereservas.ui.views.RolRegistroUsuario
import com.example.gestiondereservas.ui.views.RolUsuario

//Almacén de credenciales y perfiles centralizado en navegación
val usuariosRegistradosGlobal = mutableMapOf<String, RolUsuario>()
val passwordsRegistradosGlobal = mutableMapOf<String, String>()

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        //Ruta oficial de LoginScreen
        composable("login") { _ ->
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

        //Ruta de RegistroScreen con almacenamiento seguro
        composable("registro") { _ ->
            RegistroScreen(
                alRegistrarExitoso = { _, correo, pass, rol ->
                    val correoLimpio = correo.trim().lowercase()
                    val rolLogin = if (rol == RolRegistroUsuario.RELATOR) RolUsuario.RELATOR else RolUsuario.COORDINADOR

                    usuariosRegistradosGlobal[correoLimpio] = rolLogin
                    passwordsRegistradosGlobal[correoLimpio] = pass

                    navController.popBackStack()
                }
            )
        }

        //Llamadas nativas a las bases de relator y administrador
        composable("baseRelator") { _ ->
            PantallaBaseRelator(navController = navController)
        }

        composable("baseSupervisor") { _ ->
            PantallaBaseAdmin(navController = navController)
        }
    }
}