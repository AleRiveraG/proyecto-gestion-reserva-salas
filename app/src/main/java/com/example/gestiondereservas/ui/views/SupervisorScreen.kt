package com.example.gestiondereservas.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestiondereservas.R
import com.example.gestiondereservas.data.AppState
import com.example.gestiondereservas.utils.formatearFecha

@Composable
fun SupervisorScreen(appState: AppState) {
    Scaffold(
        topBar = { TopBarAdmin() },
        bottomBar = { NavBarAdmin() }
    ) { padding ->
        Contenido(padding)
    }
}

@Composable
private fun Contenido(padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(padding)
            .padding(16.dp)
    ) {
        Text(text = formatearFecha(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3B3B3B),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Hola, usuario",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary)

        /*Spacer(modifier = Modifier.height(12.dp))

        reservasPendientes()*/

        Spacer(modifier = Modifier.height(12.dp))

        accesosDirectos()
    }
}

@Composable
fun reservasPendientes(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Reservas por confirmar",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold)

        TextButton(onClick = {}) {
            Text("Ver todas",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun accesosDirectos() {
    Text("Accesos directos",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold)

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = {},
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)
                    )) {
                Icon(
                    painter = painterResource(R.drawable.clock),
                    contentDescription = "Icono reloj",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Horarios",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp)
        }

        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = {},
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)
                    )) {
                Icon(
                    painter = painterResource(R.drawable.history),
                    contentDescription = "Icono auditoria",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Auditoria",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp)
        }

        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = {},
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)
                    )) {
                Icon(
                    painter = painterResource(R.drawable.chart_bar),
                    contentDescription = "Icono grafico",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Reportes",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp)
        }

        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = {},
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)
                    )) {
                Icon(
                    painter = painterResource(R.drawable.door),
                    contentDescription = "Icono sala",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Salas",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp)
        }
    }

}