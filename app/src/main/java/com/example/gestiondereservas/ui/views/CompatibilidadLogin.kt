package com.example.gestiondereservas.ui.views

import androidx.compose.runtime.Composable
import com.example.gestiondereservas.ui.viewmodel.GestionReservasViewModel

// Adaptación del login a la pantalla actual del relator
@Composable
fun RelatorScreen(
    onReservas: () -> Unit,
    onBuscarSala: () -> Unit,
    onCalendario: () -> Unit,
    onActividad: () -> Unit
) {
    RelatorScreen(
        onReservas = onReservas,
        onMiActividad = onActividad
    )
}

// Adaptación del login a la pantalla actual del supervisor
@Composable
fun SupervisorScreen(
    onHorarios: () -> Unit,
    onGestionSalas: () -> Unit,
    onAuditoria: () -> Unit,
    onReportes: () -> Unit,
    onReservas: () -> Unit,
    viewModel: GestionReservasViewModel
) {
    SupervisorScreen(
        onHorarios = onHorarios,
        onGestionSalas = onGestionSalas,
        onAuditoria = onAuditoria,
        onReportes = onReportes
    )
}
