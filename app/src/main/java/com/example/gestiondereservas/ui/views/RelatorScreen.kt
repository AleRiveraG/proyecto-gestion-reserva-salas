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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestiondereservas.R
import com.example.gestiondereservas.ui.viewmodel.ReservaViewModel
import com.example.gestiondereservas.utils.formatearFecha

@Composable
fun RelatorScreen(viewModel: ReservaViewModel = rememberReservaViewModel(),
                  onReservas: () -> Unit, onMiActividad: () -> Unit) {
    //FALTA BUSCAR RESERVA POR USUARIO ID EXTRAIDO DEL LOGIN Y USA EL VIEWMODEL PARA ESO
    Contenido(onReservas, onMiActividad)
}

@Composable
private fun Contenido(
    onReservas: () -> Unit,
    onMiActividad: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState())
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

        Text("¿Qué espacio necesitas hoy?",
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            color = Color(0xFF3B3B3B))

        Spacer(modifier = Modifier.height(12.dp))

        //infoReserva()
        noInfoReserva(onReservas)

        Spacer(modifier = Modifier.height(12.dp))

        accesosDirectos(onMiActividad)
    }
}

