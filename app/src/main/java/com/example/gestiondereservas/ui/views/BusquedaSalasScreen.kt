package com.example.gestiondereservas.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestiondereservas.R
import com.example.gestiondereservas.data.CatalogoEquipamientos
import com.example.gestiondereservas.utils.formatearFecha
import com.example.gestiondereservas.utils.formatearFechaPerso
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun BusquedaSalasScreen(onReservas: () -> Unit){
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var capacidad by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }



    var paso by remember { mutableIntStateOf(1) }

    when(paso) {
       1 ->  Contenido(fecha,
           { fecha = it },
           hora,
           { hora = it},
           capacidad,
           { capacidad = it},
           area,
           { area = it },
           onSiguiente = { paso = 2}
            )
        2 -> salasDisponibles(onAtras = { paso = 1}
            , onReservas)
    }
}

@Composable
private fun Contenido(fecha:String,
                      onFechaChange: (String) -> Unit,
                      hora: String,
                      onHoraChange: (String) -> Unit,
                      capacidad: String,
                      onCapacidadChange: (String) -> Unit,
                      area: String,
                      onAreaChange: (String) -> Unit,
                      onSiguiente: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Búsqueda de salas",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold)

        Text("Busca una sala que se ajuste a tus necesidades",
            color = Color(0xFF3B3B3B),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        barraBusqueda()

        Spacer(modifier = Modifier.height(12.dp))

        cajaForm(fecha, onFechaChange, hora, onHoraChange, capacidad, onCapacidadChange, area, onAreaChange )

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { onSiguiente() },
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
            ) {
            Row() {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = "Icono busqueda"
                )
                Text("Buscar salas",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun salasDisponibles(onAtras: () -> Unit,
                     onReservas: () -> Unit)
{
    val contador: String = "1"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        )
        {
            IconButton(onClick = { onAtras() }) {
                Icon(
                    painter = painterResource(R.drawable.arrow_left),
                    contentDescription = "Icono volver",
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                "Salas disponibles",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text("$contador resultados - ${formatearFecha()}",
            color = Color(0xFF3D5A73),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 60.dp))

        salaCard()

        Spacer(modifier = Modifier.height(12.dp))

        agenda()

        Spacer(modifier = Modifier.height(12.dp))

        salaSeleccionada(onReservas)
    }
}
@Composable
fun barraBusqueda() {
    var texto by remember { mutableStateOf("") }

    OutlinedTextField(
        value = texto,
        onValueChange = { texto = it },
        singleLine = true,
        placeholder = { Text("Búsqueda por código o nombre de sala", fontSize = 12.sp) },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = "Icono busqueda",
                tint = MaterialTheme.colorScheme.tertiary
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.secondary,
            unfocusedTextColor = MaterialTheme.colorScheme.secondary
        )
    )
}

@Composable
fun cajaForm(fecha: String,
             onFechaChange: (String) -> Unit,
             hora:String,
             onHoraChange: (String) -> Unit,
             capacidad: String,
             onCapacidadChange: (String) -> Unit,
             area: String,
             onAreaChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(0xFFE5E7EB),
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(24.dp)
    ) {
        Column{

            Text(
                "¿Cuándo la necesitas?",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    seleccionarFecha("Seleccione fecha", fecha, onFechaChange)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    seleccionarHora("Selecciona hora", hora, onHoraChange)

                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Características",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            menuCapacidad("Capacidad máxima", capacidad, onCapacidadChange)

            Spacer(modifier = Modifier.height(12.dp))

            menuEquipamientos("Equipamientos disponibles")

            Spacer(modifier = Modifier.height(12.dp))

            menuArea("Área de conocimiento", area, onAreaChange)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun menuCapacidad(titulo: String, opcion: String, onCapacidadChange: (String) -> Unit) {
    val opciones = listOf(
        "",
        "10 personas",
        "20 personas",
        "30 personas",
        "40 personas",
        "50 personas",
        "60 personas",
    )

    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = opcion,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = MaterialTheme.colorScheme.primary,
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
                            onCapacidadChange(seleccion)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun menuArea(titulo: String, opcion: String, onAreaChange: (String) -> Unit) {
    val opciones = listOf(
        "Todas las áreas",
        "Informática",
        "Fotografía",
        "Diseño",
        "Comunicaciones",
        "Salud",
        "Gastronomía",
        "Turismo"
    )

    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = opcion,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = MaterialTheme.colorScheme.primary,
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
                            onAreaChange(seleccion)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun menuEquipamientos(titulo: String) {
    var expanded by remember { mutableStateOf(false) }
    var seleccionados by remember { mutableStateOf(setOf<String>()) }

    val equipamientos = CatalogoEquipamientos.opciones

    val textoSeleccion = if (seleccionados.isEmpty()) {
        "Seleccionar equipamiento"

    } else {
        seleccionados.joinToString(", ")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(text = titulo)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = textoSeleccion,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = MaterialTheme.colorScheme.primary,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary
                ),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)},
                modifier = Modifier
                    .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                equipamientos.forEach { opcion ->
                    val isSelected = seleccionados.contains(opcion)

                    DropdownMenuItem(
                        text = {
                            Row() {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = null,
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.tertiary,
                                        checkmarkColor = MaterialTheme.colorScheme.primary
                                    )
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(text = opcion,
                                    color = MaterialTheme.colorScheme.primary)
                            }
                        },
                        onClick = {
                            seleccionados = if (isSelected) {
                                seleccionados - opcion
                            } else {
                                seleccionados + opcion
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                }
            }
        }
    }
}

@Composable
fun salaCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)

        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.door),
                    contentDescription = "Icono sala"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Codigo de sala",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
            Text(
                "Nombre de sala",
                color = Color(0xFF3D5A73),
                fontSize = 12.sp
            )
            Text(
                "Capacidad",
                color = Color(0xFF3D5A73),
                fontSize = 12.sp
            )
            Text(
                "Equipamiento",
                color = Color(0xFF3D5A73),
                fontSize = 12.sp
            )
            Text(
                "Area",
                color = Color(0xFF3D5A73),
                fontSize = 12.sp
            )
            Text(
                "Encargado/a",
                color = Color(0xFF3D5A73),
                fontSize = 12.sp
            )
        }
    }

}

@Composable
fun agenda() {
    Column() {
        Text("AGENDA",
            color = Color(0xFF3D5A73),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold)

        Text(text = formatearFecha(),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold)
    }
}

@Composable
fun salaSeleccionada(onReservas: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {
            Text("Sala seleccionada",
                color = Color(0xFF3D5A73),
                fontSize = 10.sp)

            Text("Codigo sala - Fecha - Hora",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp)
        }
        Button(
            onClick = { onReservas() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("Reservar",
                color = MaterialTheme.colorScheme.primary)
        }
    }
}