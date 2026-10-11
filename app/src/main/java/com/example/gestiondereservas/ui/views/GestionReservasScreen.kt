package com.example.gestiondereservas.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestiondereservas.R

@Composable
fun GestionReservasScreen() {
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { NavBar() }
    ) { padding ->
        Contenido(padding)
    }
}

@Composable
fun Contenido(padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Gestión de Reservas",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Valida las solicitudes de reservas",
            color = Color(0xFF3B3B3B),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        contador("2", "8", "1")

        Spacer(modifier = Modifier.height(12.dp))

        reserva()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.secondary
        ),
        title = {
            Row (verticalAlignment = Alignment.CenterVertically){

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo Duoc Uc",
                    modifier = Modifier.size(100.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Reserva" , color = MaterialTheme.colorScheme.secondary)
                Text("Salas", color = MaterialTheme.colorScheme.tertiary)
            }
        },
        actions = {
            Row() {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B3B3B))
                        .clickable{ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.bell),
                        contentDescription = "Icono notificaciones",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B3B3B))
                        .clickable{ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.user_circle),
                        contentDescription = "Icono usuario",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    )
}

@Composable
fun NavBar() {
    var seleccion by remember { mutableIntStateOf(3) }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        NavigationBarItem(
            selected = seleccion == 0,
            onClick = { seleccion = 0 },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "Icono inicio"
                )
            },
            label = { Text("Inicio")},
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
                Icon(
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = "Icono calendario",
                )
            },
            label = { Text("Calendario")},
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
                Icon(
                    painter = painterResource(id = R.drawable.door),
                    contentDescription = "Icono puerta",
                )
            },
            label = { Text("Salas")},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.secondary,
                selectedTextColor = MaterialTheme.colorScheme.tertiary,
                indicatorColor = MaterialTheme.colorScheme.tertiary
            )
        )
        NavigationBarItem(
            selected = seleccion == 3,
            onClick = { seleccion = 3 },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.task),
                    contentDescription = "Icono reserva",
                )
            },
            label = { Text("Reservas")},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.secondary,
                selectedTextColor = MaterialTheme.colorScheme.tertiary,
                indicatorColor = MaterialTheme.colorScheme.tertiary
            )
        )
    }
}

@Composable
fun contador(pendientes: String, aprobadas: String, rechazadas: String) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)){
                Text(text = pendientes,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 24.sp)

                Spacer(modifier = Modifier.height(4.dp))

                Text("Pendientes",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp)
            }

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)){
                Text(text = aprobadas,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 24.sp)

                Spacer(modifier = Modifier.height(4.dp))

                Text("Aprobadas",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp)
            }

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)){
                Text(text = rechazadas,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 24.sp)

                Spacer(modifier = Modifier.height(4.dp))

                Text("Rechazadas",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun reserva() {
    var mostrarModal by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation =6.dp
        ),
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                etiquetaOrganizador("Informática y Telecomunicaciones")
                etiquetaEstado("PENDIENTE")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Nombre de la actividad",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                etiquetaTipoActividad("Actividad Academica")

                Spacer(modifier = Modifier.width(8.dp))

                etiquetaTieneAutorizacion("No")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Responsable: nombre -",
                    color = Color(0xFF3B3B3B),
                    fontSize = 12.sp)

                Spacer(modifier = Modifier.width(8.dp))

                Text("+59 1234 5678",
                    color = Color(0xFF3B3B3B),
                    fontSize = 12.sp)
            }

            Text("Correo responsable",
                color = Color(0xFF3B3B3B),
                fontSize = 12.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Icon(
                        painter = painterResource(R.drawable.door),
                        contentDescription = "Icono Puerta",
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text("Recinto",
                            color = Color(0xFF63788C),
                            fontSize = 14.sp)
                        Text("LC12",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Icon(
                        painter = painterResource(R.drawable.clock),
                        contentDescription = "Icono Hora",
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text("Inicio y término",
                            color = Color(0xFF63788C),
                            fontSize = 14.sp)
                        Text("10:00 - 11:00",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Icon(
                        painter = painterResource(R.drawable.spanner),
                        contentDescription = "Icono Montaje",
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text("Montaje",
                            color = Color(0xFF63788C),
                            fontSize = 14.sp)
                        Text("09:30",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Icon(
                        painter = painterResource(R.drawable.community),
                        contentDescription = "Icono Publico",
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text("Público",
                            color = Color(0xFF63788C),
                            fontSize = 14.sp)
                        Text("Estudiantes",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp,
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary),
                onClick = { mostrarModal = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Ver detalle de la solicitud",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
    if (mostrarModal) {
        ModalBottomSheet(
            onDismissRequest = { mostrarModal = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            detalleSolicitud(onCerrar = { mostrarModal = false })
        }
    }
}

@Composable
fun etiquetaTipoActividad(tipo: String){
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(32.dp))
            .border(width = 1.dp,
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.tertiary)
            .background(Color(0xFFFFF9E6))
    ){
        Text(text = tipo,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
    }
}

@Composable
fun etiquetaTieneAutorizacion(tieneAuth: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(32.dp))
            .background(color = if (tieneAuth == "Si") Color(0xFFE5F2E1) else Color(0xFFFFF9E6))
    ){
        Text(text = "Dirección: $tieneAuth",
            color = if (tieneAuth == "Si") Color(0xFF005C35) else Color(0xFF6B5403),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
    }
}

@Composable
fun etiquetaOrganizador(organizador: String){
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color =
            if (organizador == "Informática y Telecomunicaciones") Color(0xFF8BB8E8)
            else if (organizador == "Administración y Negocios") Color(0xFFC98BDB)
            else if (organizador == "Comunicación") Color(0xFFDF4661)
            else if (organizador == "Construcción") Color(0xFFECA154)
            else if (organizador == "Diseño") Color(0xFFDBE442)
            else if (organizador == "Gastronomía") Color(0xFFFF808B)
            else if (organizador == "Ingeniería y Recursos Naturales") Color(0xFFA1D884)
            else if (organizador == "Salud y Bienestar") Color(0xFF99D6EA)
            else Color(0xFF2AD2C9)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("•",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp),
                fontWeight = FontWeight.Bold)
            Text(
                text = organizador,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, end = 8.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun etiquetaEstado(estado: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color =
            if (estado == "PENDIENTE") Color(0xFFFFF9E6)
            else if (estado == "APROBADO") Color(0xFFE5F2E1)
            else Color(0xFFFCE7EC)
            )
    ) {
        Text(text =
            if (estado == "PENDIENTE") "Pendiente"
            else if (estado == "APROBADO") "Aprobado"
            else "Rechazado",
            color = Color(0xFF6B5403),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp))
    }
}

@Composable
fun detalleSolicitud(onCerrar: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("DETALLE DE LA SOLICITUD",
                color = Color(0xFF3B3B3B),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            IconButton(
                onClick = { onCerrar() }    ) {
                Icon(
                    painter = painterResource(R.drawable.x),
                    contentDescription = "Icono Cerrar",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Taller de programación",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(12.dp))

        Row{
            Text("Recinto ",
                color = Color(0xFF3B3B3B),
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Fecha",
                color = Color(0xFF3B3B3B),
                fontSize = 16.sp
                )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier
                    .weight(1f),
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD00236)
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.x),
                    contentDescription = "Icono Cerrar",
                    tint = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    "Denegar",
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                modifier = Modifier
                    .weight(1f),
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3DB83B)
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.check),
                    contentDescription = "Icono Aprobar",
                    tint = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    "Aprobar",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
