package com.example.gestiondereservas.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.gestiondereservas.R

@Composable
fun NavBarAdmin(
    seleccion: Int,
    onInicio: () -> Unit,
    onCalendario: () -> Unit,
    onSalas: () -> Unit,
    onReservas: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        NavigationBarItem(
            selected = seleccion == 0,
            onClick = onInicio,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "Inicio"
                )
            },
            label = { Text("Inicio") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.secondary,
                selectedTextColor = MaterialTheme.colorScheme.tertiary,
                indicatorColor = MaterialTheme.colorScheme.tertiary
            )
        )
        NavigationBarItem(
            selected = seleccion == 1,
            onClick = onCalendario,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = "Horarios"
                )
            },
            label = { Text("Horarios") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.secondary,
                selectedTextColor = MaterialTheme.colorScheme.tertiary,
                indicatorColor = MaterialTheme.colorScheme.tertiary
            )
        )
        NavigationBarItem(
            selected = seleccion == 2,
            onClick = onSalas,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.door),
                    contentDescription = "Salas"
                )
            },
            label = { Text("Salas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.secondary,
                selectedTextColor = MaterialTheme.colorScheme.tertiary,
                indicatorColor = MaterialTheme.colorScheme.tertiary
            )
        )
        NavigationBarItem(
            selected = seleccion == 3,
            onClick = onReservas,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.door),
                    contentDescription = "Reservas"
                )
            },
            label = { Text("Reservas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.secondary,
                selectedTextColor = MaterialTheme.colorScheme.tertiary,
                indicatorColor = MaterialTheme.colorScheme.tertiary
            )
        )
    }
}
