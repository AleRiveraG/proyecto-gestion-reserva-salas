
package com.example.gestiondereservas.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gestiondereservas.data.AppState
import com.example.gestiondereservas.data.repository.AuditoriaRepository
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.ui.components.PantallaBaseAdmin
import com.example.gestiondereservas.ui.components.PantallaBaseRelator
import com.example.gestiondereservas.viewmodel.AuditoriaViewModel
import com.example.gestiondereservas.ui.views.LoginScreen
import com.example.gestiondereservas.ui.views.RegistroScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.gestiondereservas.data.repository.UsuarioRepository
import com.example.gestiondereservas.viewmodel.RegistroViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import com.example.gestiondereservas.ui.views.NotificacionesScreen
import com.example.gestiondereservas.ui.views.RolUsuario

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
fun rememberRegistroViewModel(): RegistroViewModel {
    val contexto = LocalContext.current.applicationContext
    return viewModel(
        factory = viewModelFactory {
            initializer {
                val dao = AppDatabase.getInstance(contexto).usuarioDao()
                val repository = UsuarioRepository(dao)
                RegistroViewModel(repository)
            }
        }
    )
}

@Composable
fun AppNavigation(navController: NavHostController,
                  appState: AppState,
                  abrirNotificaciones: Boolean = false
) {
    val auditoriaViewModel: AuditoriaViewModel = rememberAuditoriaViewModel()
    var correoUsuario by remember { mutableStateOf<String?>(null) }
    var rolUsuario by remember { mutableStateOf<RolUsuario?>(null) }
    val esSupervisor = rolUsuario == RolUsuario.COORDINADOR

    LaunchedEffect(abrirNotificaciones, correoUsuario, rolUsuario) {
        if (abrirNotificaciones && correoUsuario != null) {
            navController.navigate("notificaciones/$esSupervisor") {
                launchSingleTop = true
            }
        }
    }
    NavHost(
        navController = navController,
        startDestination = "login",
        enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { 1000 }) },
        exitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { -1000 }) },
        popEnterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { -1000 }) },
        popExitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { 1000 }) }
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
            val registroViewModel: RegistroViewModel = rememberRegistroViewModel()
            val estaCargando by registroViewModel.estaCargando.collectAsState()
            val mensajeError by registroViewModel.mensajeError.collectAsState()

            RegistroScreen(
                estaCargando = estaCargando,
                mensajeErrorGeneral = mensajeError,
                alRegistrarExitoso = { nombre, correo, password, telefono, rol ->
                    registroViewModel.registrarUsuario(
                        nombre = nombre,
                        correo = correo,
                        contrasena = password,
                        telefono = telefono,
                        rol = rol.nombreMostrar,
                        onRegistroExitoso = {
                            navController.popBackStack()
                        }
                    )
                }
            )
        }

        composable(
            "baseRelator/{idUsuario}",
            arguments = listOf(navArgument("idUsuario") { type = NavType.LongType})
        ) { backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getLong("idUsuario") ?: 0
            val contexto = LocalContext.current
            var correoUsuario by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(idUsuario) {
                withContext(Dispatchers.IO) {
                    val usuario = AppDatabase.getInstance(contexto)
                        .usuarioDao()
                        .obtenerUsuarioPorId(idUsuario)
                    correoUsuario = usuario?.correo
                }
            }
            PantallaBaseRelator(
                navController = navController,
                idUsuario = idUsuario,
                appState = appState,
                onRegistrarAuditoria = { evento ->
                    auditoriaViewModel.registrarReserva(evento)
                },
                correoUsuario = correoUsuario,
                onCerrarSesion = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            "baseSupervisor/{idUsuario}",
            arguments = listOf(navArgument("idUsuario") { type = NavType.LongType})
        ){ backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getLong("idUsuario") ?: 0
            PantallaBaseAdmin(
                navController,
                idUsuario,
                onRegistrarAuditoria = { evento ->
                    auditoriaViewModel.registrarReserva(evento)
                },
                onCerrarSesion = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                auditoriaViewModel = auditoriaViewModel
            )
        }

        composable("notificaciones/{esSupervisor}") { entrada ->
            val rolDeRuta = entrada.arguments
                ?.getString("esSupervisor")
                ?.toBoolean() ?: false

            NotificacionesScreen(
                navController = navController,
                esSupervisor = rolDeRuta,
                correoUsuario = correoUsuario,
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}