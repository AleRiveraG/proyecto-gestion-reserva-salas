package com.example.gestiondereservas.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.example.gestiondereservas.R

@Composable
fun NavBar(inicial: Int = 0) {
    var seleccion by remember { mutableIntStateOf(inicial) }

    val colores = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.tertiary,
        indicatorColor = MaterialTheme.colorScheme.tertiary,
        unselectedIconColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
        unselectedTextColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
    )

    NavigationBar(containerColor = MaterialTheme.colorScheme.primary) {
        NavigationBarItem(
            selected = seleccion == 0,
            onClick = { seleccion = 0 },
            icon = { Icon(painterResource(R.drawable.home), "Icono inicio") },
            label = { Text("Inicio") },
            colors = colores
        )
        NavigationBarItem(
            selected = seleccion == 1,
            onClick = { seleccion = 1 },
            icon = { Icon(painterResource(R.drawable.calendar), "Icono calendario") },
            label = { Text("Calendario") },
            colors = colores
        )
        NavigationBarItem(
            selected = seleccion == 2,
            onClick = { seleccion = 2 },
            icon = { Icon(painterResource(R.drawable.door), "Icono puerta") },
            label = { Text("Salas") },
            colors = colores
        )
    }
}