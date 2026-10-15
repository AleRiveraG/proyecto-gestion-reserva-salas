package com.example.gestiondereservas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gestiondereservas.data.repository.AuditoriaRepository
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.ui.components.PantallaBaseAdmin
import com.example.gestiondereservas.ui.components.PantallaBaseRelator
import com.example.gestiondereservas.ui.viewmodel.AuditoriaViewModel

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
        composable ("login"){ }

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
                }) }

    }
}

