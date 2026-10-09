package com.example.gestiondereservas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.secondary
        ),

        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {


                /*
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo Duoc Uc",
                    modifier = Modifier.size(100.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
                */

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    "Reserva",
                    color = MaterialTheme.colorScheme.secondary
                )

                Text(
                    "Salas",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        },

        actions = {
            Row {

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B3B3B))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {

                    /*
                    Icon(
                        painter = painterResource(id = R.drawable.bell),
                        contentDescription = "Icono notificaciones",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                    */
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B3B3B))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {


                    /*
                    Icon(
                        painter = painterResource(id = R.drawable.user_circle),
                        contentDescription = "Icono usuario",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                    */
                }

                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    )
}
