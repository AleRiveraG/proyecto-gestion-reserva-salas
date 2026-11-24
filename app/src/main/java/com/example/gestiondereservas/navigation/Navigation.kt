package com.example.gestiondereservas.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gestiondereservas.data.repository.AuditoriaRepository
import com.example.gestiondereservas.ui.components.PantallaBaseAdmin
import com.example.gestiondereservas.ui.components.PantallaBaseRelator
import com.example.gestiondereservas.viewmodel.AuditoriaViewModel
import com.example.gestiondereservas.ui.views.LoginScreen
import com.example.gestiondereservas.ui.views.RegistroScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import com.example.gestiondereservas.data.remote.RetrofitClient
import com.example.gestiondereservas.data.remote.SessionManager
import com.example.gestiondereservas.data.repository.ReservaRepository
import com.example.gestiondereservas.ui.viewmodel.AutenticacionViewModel
import com.example.gestiondereservas.ui.views.NotificacionesScreen
import com.example.gestiondereservas.ui.views.RolUsuario

@Composable
fun rememberAuditoriaViewModel(): AuditoriaViewModel {
    return viewModel(
        factory = viewModelFactory {
            initializer {
                val repository = AuditoriaRepository(RetrofitClient.apiAuditoria)
                AuditoriaViewModel(repository)
            }
        }
    )
}

@Composable
fun AppNavigation(navController: NavHostController,
                  abrirNotificaciones: Boolean = false
) {
    val auditoriaViewModel: AuditoriaViewModel = rememberAuditoriaViewModel()
    val autenticacionViewModel: AutenticacionViewModel = viewModel()
    var correoUsuario by remember { mutableStateOf<String?>(null) }
    var rolUsuario by remember { mutableStateOf<RolUsuario?>(null) }
    val esSupervisor = rolUsuario == RolUsuario.COORDINADOR

    val repository = remember { ReservaRepository(RetrofitClient.apiSalas, RetrofitClient.api,
        RetrofitClient.apiHorarios) }

    LaunchedEffect(abrirNotificaciones, correoUsuario) {
        if (abrirNotificaciones && correoUsuario != null) {
            navController.navigate(
                "notificaciones/$esSupervisor"
            ) {
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
            val authState by autenticacionViewModel.uiState.collectAsStateWithLifecycle()

            //Navega solo cuando el backend confirma el login (HTTP 200)
            LaunchedEffect(authState.usuarioAutenticado) {
                val usuario = authState.usuarioAutenticado ?: return@LaunchedEffect
                val idValido = usuario.idUsuario

                rolUsuario = if (usuario.rol.equals("RELATOR", ignoreCase = true)) {
                    RolUsuario.RELATOR
                } else {
                    RolUsuario.COORDINADOR
                }

                correoUsuario = usuario.correo

                val destino = if (rolUsuario == RolUsuario.RELATOR) {
                    "baseRelator/$idValido"
                } else {
                    "baseSupervisor/$idValido"
                }
                autenticacionViewModel.limpiarEstado()

                navController.navigate(destino) {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            }

            LoginScreen(
                estaCargando = authState.estaCargando,
                mensajeErrorGeneral = authState.mensajeError,

                alNavegarARegistro = {
                    autenticacionViewModel.limpiarEstado()
                    navController.navigate("registro")
                },
                alNavegarARelator = {},
                alNavegarASupervisor = {},
                alIniciarSesionExitoso = { correo, password, rol ->
                    autenticacionViewModel.iniciarSesion(correo, password, rol.name)
                }
            )
        }

        composable("registro") {
            val authState by autenticacionViewModel.uiState.collectAsState()

            //Vuelve al login cuando el backend confirma el registro (HTTP 201)
            LaunchedEffect(authState.registroExitoso) {
                if (authState.registroExitoso) {
                    autenticacionViewModel.limpiarEstado()
                    navController.popBackStack()
                }
            }

            RegistroScreen(
                estaCargando = authState.estaCargando,
                mensajeErrorGeneral = authState.mensajeError,
                alRegistrarExitoso = { nombre, correo, password, telefono, rol ->
                    autenticacionViewModel.registrarUsuario(
                        nombre = nombre,
                        correo = correo,
                        password = password,
                        rol = rol.name,
                        telefono = telefono
                    )
                }
            )
        }

        composable(
            "baseRelator/{idUsuario}",
            arguments = listOf(navArgument("idUsuario") { type = NavType.LongType})
        ) { backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getLong("idUsuario") ?: 0
            var correoUsuario by remember { mutableStateOf<String?>(SessionManager.correo) }  //Fallback desde SessionManager


            PantallaBaseRelator(
                idUsuario = idUsuario,
                onRegistrarAuditoria = { evento ->
                    auditoriaViewModel.registrarReserva(evento)
                },
                correoUsuario = correoUsuario,
                onCerrarSesion = {
                    SessionManager.cerrarSesion()  //Limpia el token JWT
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                reservaRepository = repository

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
                    SessionManager.cerrarSesion()  //Limpia el token JWT
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
                },
                reservaRepository =repository
            )
        }
    }
}