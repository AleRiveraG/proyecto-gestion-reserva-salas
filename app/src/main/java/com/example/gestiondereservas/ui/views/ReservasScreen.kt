package com.example.gestiondereservas.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestiondereservas.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@Composable
fun ReservasScreen() {
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { NavBar() }
    ) { padding ->
        content(padding)
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

    var seleccion by remember { mutableIntStateOf(0) }

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
    }
}

@Composable
fun content(padding: PaddingValues) {

    Column(
        modifier = Modifier
            .background(Color(0xFFFFFFFF))
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left),
                contentDescription = "Icono volver atras",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                "Reservar sala",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            "Paso 1 de 3",
            color = Color(0xFF3B3B3B),
            fontSize = 15.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(start = 48.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp)
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp),
                color = Color(0xFFEEEEEE),
                thickness = 2.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "1",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        "Actividad",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEEEEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "2",
                            color = Color(0xFFAAAAAA),
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        "Salas",
                        color = Color(0xFFAAAAAA),
                        fontSize = 12.sp
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEEEEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "3",
                            color = Color(0xFFAAAAAA),
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        "Requisitos",
                        color = Color(0xFFAAAAAA),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(24.dp)
        ) {
            Column() {

                campoNombre()

                Spacer(modifier = Modifier.height(16.dp))

                campoDescripcion()

                Spacer(modifier = Modifier.height(16.dp))

                tipoActividad()

                Spacer(modifier = Modifier.height(16.dp))

                tieneAutorizacion()

                Spacer(modifier = Modifier.height(16.dp))

                campoResponsable()

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        campoTelefono()
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        campoCorreo()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                menuEscuelas()

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(60.dp)


        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Siguiente",
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    painter = painterResource(R.drawable.arrow_right),
                    contentDescription = "Icono Siguiente"
                )
            }
        }

    }
}


@Composable
fun campoNombre() {
    var nombre by remember { mutableStateOf("") }
    Text("Nombre de la actividad *",
        color = MaterialTheme.colorScheme.primary)

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = nombre,
        onValueChange = { nombre = it },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5)
        )
    )
}

@Composable
fun campoDescripcion() {
    var descripcion by remember { mutableStateOf("") }
    Text("Descripción de la actividad *",
        color = MaterialTheme.colorScheme.primary)

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = descripcion,
        onValueChange = { descripcion = it },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5)
        )
    )
}

@Composable
fun campoResponsable() {
    var responsable by remember { mutableStateOf("") }

    Text("Responsable de la actividad *",
        color = MaterialTheme.colorScheme.primary)

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = responsable,
        onValueChange = { responsable = it },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5)
        )
    )
}

@Composable
fun campoTelefono() {
    var telefono by remember { mutableStateOf("") }
    Text(
        "Teléfono de contacto *",
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = telefono,
        onValueChange = { telefono = it },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5)
        )
    )
}

@Composable
fun campoCorreo() {
    var correo by remember { mutableStateOf("") }
    Text(
        "Correo electrónico *",
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = correo,
        onValueChange = { correo = it },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5)
        )
    )
}

@Composable
fun tipoActividad() {
    val opciones = listOf(
        "",
        "Ceremonia Institucional",
        "Actividad Académica",
        "Actividad Extracurricular",
        "Duoc UC A Puertas Abiertas",
        "Actividad Sede",
        "Actividad de Externo (no organizada por Duoc UC)"
    )

    var opcion by remember{ mutableStateOf(opciones[0])}

    Column {
        Text("Tipo de actividad",
            color = MaterialTheme.colorScheme.primary)
    }

    Spacer(modifier = Modifier.height(8.dp))

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        opciones.drop(1).forEach { seleccion ->
            val isSelected = seleccion == opcion

            FilterChip(
                selected = isSelected,
                onClick = { opcion = seleccion},
                label = { Text(seleccion)},
                leadingIcon = if(isSelected) {
                    {
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = "Icono Check"
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFFFF9E6),
                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    labelColor = Color(0xFF4B5563)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = Color(0xFFE5E7EB),
                    selectedBorderColor = MaterialTheme.colorScheme.tertiary
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun tieneAutorizacion() {
    val opciones = listOf(
        "",
        "Si",
        "No"
    )

    var opcion by remember { mutableStateOf(opciones[0]) }

    Column{
        Text(
            "¿Tu actividad tiene la validación de la dirección de la sede o de la " +
                    "subdirección de tu área?",
            color = MaterialTheme.colorScheme.primary
        )
    }

    Spacer(modifier = Modifier.height(4.dp))

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        opciones.drop(1).forEach { seleccion ->
            var isSelected = seleccion == opcion
            FilterChip(
                selected = isSelected,
                onClick = { opcion = seleccion},
                label = { Text(seleccion)},
                leadingIcon = if(isSelected) {
                    {
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = "Icono Check"
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFFFF9E6),
                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    labelColor = Color(0xFF4B5563)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = Color(0xFFE5E7EB),
                    selectedBorderColor = MaterialTheme.colorScheme.tertiary
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun menuEscuelas() {
    val opciones = listOf(
        "",
        "Administración y Negocios",
        "Comunicación",
        "Construcción",
        "Diseño",
        "Gastronomia",
        "Informática y Telecomunicaciones",
        "Ingeniería y Recursos Naturales",
        "Salud y Bienestar",
        "Turismo y Hospitalidad"
    )

    var expanded by remember { mutableStateOf(false) }
    var opcion by remember { mutableStateOf(opciones[0]) }


    Column () {
        Text(
            "Área o Escuela que organiza la actividad",
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = opcion,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.secondary)
            ) {
                opciones.drop(1).forEach { seleccion ->
                    DropdownMenuItem(
                        text = { Text(seleccion,
                               color = MaterialTheme.colorScheme.primary
                        )},
                        onClick = {
                            opcion = seleccion
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

