package com.example.gestiondereservas.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

@Composable
fun AuditoriaScreen(onAtras: () -> Unit){

    contenido(onAtras)
}

@Composable
private fun contenido(onAtras: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onAtras() }) {
                Icon(
                    painter = painterResource(R.drawable.arrow_left),
                    contentDescription = "Icono atras",
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text("Auditoría",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold)
        }

        Text("Historial de acciones de la sede",
            color = Color(0xFF3B3B3B),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 56.dp))

        Spacer(modifier = Modifier.height(12.dp))

        auditoriaCard()

    }
}

@Composable
fun auditoriaCard(){
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        // if (tipo = "Reserva", "Salas", "Horario")

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF0F0F0)), //Cambiar color en base al tipo de reserva
                contentAlignment = Alignment.Center
            ){
                Icon(
                    painter = painterResource(R.drawable.task),
                    contentDescription = "Icono reserva"
                    //painter = painterResource(R.drawable.clock)
                    //painter = painterResource(R.drawable.door
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column() {
                Text("Acción realizada",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp)

                Text("Area",
                    color = Color(0xFF3B3B3B),
                    fontSize = 12.sp)

                Text("Fecha y hora que se realizo la acción",
                    color = Color(0xFF3B3B3B),
                    fontSize = 12.sp)
            }
        }
    }

}