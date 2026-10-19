package com.example.gestiondereservas.ui.views

import androidx.compose.foundation.background
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestiondereservas.R
import com.example.gestiondereservas.data.room.entidades.AuditoriaEntity
import com.example.gestiondereservas.data.room.entidades.TipoAuditoria
import com.example.gestiondereservas.ui.viewmodel.AuditoriaViewModel

@Composable
fun AuditoriaScreen(onAtras: () -> Unit,
                    viewModel: AuditoriaViewModel
){
    val listaAuditorias by viewModel.totalAuditorias.collectAsState()

    contenido(onAtras,
        listaAuditorias)


}

@Composable
private fun contenido(onAtras: () -> Unit,
                      listaAuditorias: List<AuditoriaEntity>) {
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

        listaAuditorias.forEach { auditoria ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                auditoriaCard(auditoria)
            }
        }

    }
}

@Composable
fun auditoriaCard(auditoria: AuditoriaEntity){
    val (icono, colorFondo) = when (auditoria.tipo) {
        TipoAuditoria.RESERVA.name -> Pair(R.drawable.task, Color(0xFFFFF0D4))
        TipoAuditoria.SALAS.name -> Pair(R.drawable.door, Color(0xFFE9F0FA))
        TipoAuditoria.HORARIOS.name -> Pair(R.drawable.clock, Color(0xFFEFEFEF))
        TipoAuditoria.REPORTES.name -> Pair(R.drawable.chart_bar, Color(0xFFC4FFCB))
        else -> Pair(R.drawable.history, Color(0xFFFFEAC4))
    }

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


        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorFondo),
                contentAlignment = Alignment.Center
            ){
                Icon(
                    painter = painterResource(icono),
                    contentDescription = "Icono auditoria"
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column() {
                Text(
                    auditoria.accion,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp)

                Text(
                    auditoria.detalle,
                    color = Color(0xFF3B3B3B),
                    fontSize = 12.sp)

                Text("${auditoria.fecha} - ${auditoria.hora}",
                    color = Color(0xFF3B3B3B),
                    fontSize = 12.sp)
            }
        }
    }

}