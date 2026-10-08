package com.example.gestiondereservas.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UsoSalaScreen() {

    var actividadIniciada by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Text(
            text = "Uso de sala",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Título de la actividad")

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (actividadIniciada) "Actividad en curso"
            else "Actividad pendiente"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("00:00")

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                actividadIniciada = !actividadIniciada
            }
        ) {
            Text(
                if (actividadIniciada) "Finalizar actividad"
                else "Inicio de actividad"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(onClick = {}) {
            Text(
                if (actividadIniciada) "Denunciar inconveniente"
                else "Cancelar reserva"
            )
        }
    }
}