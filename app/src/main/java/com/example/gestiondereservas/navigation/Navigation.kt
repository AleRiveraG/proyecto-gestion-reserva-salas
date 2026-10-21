
package com.example.gestiondereservas.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestiondereservas.data.AppState
import com.example.gestiondereservas.data.repository.AuditoriaRepository
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.ui.components.PantallaBaseAdmin
import com.example.gestiondereservas.ui.components.PantallaBaseRelator
import com.example.gestiondereservas.ui.theme.Black
import com.example.gestiondereservas.ui.theme.White
import com.example.gestiondereservas.ui.theme.Yellow
import com.example.gestiondereservas.ui.viewmodel.AuditoriaViewModel
import com.example.gestiondereservas.ui.views.LoginScreen
import com.example.gestiondereservas.ui.views.NotificacionesScreen
import com.example.gestiondereservas.ui.views.RegistroScreen
import com.example.gestiondereservas.ui.views.RolUsuario
import com.example.gestiondereservas.ui.views.UsoSalaScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.gestiondereservas.ui.viewmodel.GestionReservasViewModel
import com.example.gestiondereservas.ui.views.LoginScreen
import com.example.gestiondereservas.ui.views.RegistroScreen
import com.example.gestiondereservas.ui.views.RelatorScreen
import com.example.gestiondereservas.ui.views.RolRegistroUsuario
import com.example.gestiondereservas.ui.views.SupervisorScreen
import com.example.gestiondereservas.data.repository.UsuarioRepository
import com.example.gestiondereservas.ui.viewmodel.RegistroViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType

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
                  appState: AppState
) {
    val auditoriaViewModel: AuditoriaViewModel = rememberAuditoriaViewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
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
                alRegistrarExitoso = { nombre, correo, password, rol ->
                    registroViewModel.registrarUsuario(
                        nombre = nombre,
                        correo = correo,
                        contrasena = password,
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
        ){backStackEntry ->
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
    }
}