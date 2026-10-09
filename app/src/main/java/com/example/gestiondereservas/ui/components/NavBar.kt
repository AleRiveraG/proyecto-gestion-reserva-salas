
package com.example.gestiondereservas.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.gestiondereservas.ui.theme.Black

@Composable
fun NavBar() {

    var seleccion by remember { mutableIntStateOf(0) }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {

        NavigationBarItem(
            selected = seleccion == 0,
            onClick = { seleccion = 0 },
            icon = {

                /*
                Icon(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "Icono inicio"
                )
                */
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
            onClick = { seleccion = 1 },
            icon = {

                /*
                Icon(
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = "Icono calendario"
                )
                */
            },
            label = { Text("Calendario") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.secondary,
                selectedTextColor = MaterialTheme.colorScheme.tertiary,
                indicatorColor = MaterialTheme.colorScheme.tertiary
            )
        )

        NavigationBarItem(
            selected = seleccion == 2,
            onClick = { seleccion = 2 },
            icon = {
                /*
                Icon(
                    painter = painterResource(id = R.drawable.door),
                    contentDescription = "Icono puerta"
                )
                */
            },
            label = { Text("Salas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.secondary,
                selectedTextColor = MaterialTheme.colorScheme.tertiary,
                indicatorColor = MaterialTheme.colorScheme.tertiary
            )
        )
    }
}
