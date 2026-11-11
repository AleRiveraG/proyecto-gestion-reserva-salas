package com.example.gestiondereservas.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import com.example.gestiondereservas.R

data class NavigationItem(
    val nombre: String,
    val icono: Int,
    val onClick: () -> Unit
)
@Composable
fun NavBar(
    inicial: Int = 0,
    onInicio: () -> Unit,
    onCalendario: () -> Unit,
    onSalas: () -> Unit
) {
    var seleccion by remember { mutableIntStateOf(inicial) }

    val colores = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.tertiary,
        indicatorColor = MaterialTheme.colorScheme.tertiary,
        unselectedIconColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
        unselectedTextColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
    )

    val items = listOf(
        NavigationItem("Inicio", R.drawable.home, onInicio),
        NavigationItem("Calendario", R.drawable.calendar, onCalendario),
        NavigationItem("Salas", R.drawable.door, onSalas),
    )
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        
        items.forEachIndexed { index, item ->
            val seleccionado = seleccion == index

            val interaccion = remember { MutableInteractionSource() }
            val presionado by interaccion.collectIsPressedAsState()

            val escala = when {
                presionado -> 0.9f
                seleccionado -> 1.1f
                else -> 1.0f
            }

            val scale by animateFloatAsState(
                targetValue = escala,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )

            NavigationBarItem(
                selected = seleccionado,
                onClick = {
                    seleccion = index
                    item.onClick()
                },
                interactionSource = interaccion,
                icon = {
                    Icon(
                        painter = painterResource(item.icono),
                        contentDescription = item.nombre,
                        modifier = Modifier.scale(scale)
                    )
                },
                label = { Text(item.nombre) },
                colors = colores
            )
        }
    }
}