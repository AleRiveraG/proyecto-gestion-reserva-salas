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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestiondereservas.R
import com.example.gestiondereservas.data.CatalogoEquipamientos
import com.example.gestiondereservas.data.room.entidades.SalaConEquipamiento
import com.example.gestiondereservas.ui.viewmodel.FiltrosAvanzados
import com.example.gestiondereservas.ui.viewmodel.SalasUiState
import com.example.gestiondereservas.ui.viewmodel.SalasViewModel
import com.example.gestiondereservas.utils.formatearFecha
import com.example.gestiondereservas.utils.formatearFechaPerso
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun BusquedaSalasScreen(onReservas: () -> Unit,
                        viewModel: SalasViewModel = rememberSalasViewModel()
){
    val uiState by viewModel.uiState.collectAsState()
    var paso by remember { mutableIntStateOf(1) }

    when(paso) {
       1 ->  Contenido(
           uiState = uiState,
           onBusquedaChange = { viewModel.onBusquedaChange(it) },
           onFechaChange = { viewModel.actualizarFiltros(fecha = it) },
           onHoraChange = { viewModel.actualizarFiltros(hora = it) },
           onCapacidadChange = { viewModel.actualizarFiltros(capacidad = it) },
           onAreaChange = { viewModel.actualizarFiltros(area = it) },
           onEquipamientosChange = { viewModel.actualizarFiltros(equipamientos = it) },
           onSiguiente = { paso = 2 }
       )
        2 -> salasDisponibles(
            salas = uiState.salas,
            onAtras = { paso = 1 },
            onReservas = onReservas)
    }
}

@Composable
private fun Contenido(uiState: SalasUiState,
                      onBusquedaChange: (String) -> Unit,
                      onFechaChange: (String) -> Unit,
                      onHoraChange: (String) -> Unit,
                      onCapacidadChange: (String) -> Unit,
                      onAreaChange: (String) -> Unit,
                      onEquipamientosChange: (Set<String>) -> Unit,
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

        barraBusqueda(uiState.busqueda, onBusquedaChange)

        Spacer(modifier = Modifier.height(12.dp))

        cajaForm(uiState.filtros,
            onFechaChange,
            onHoraChange,
            onCapacidadChange,
            onAreaChange,
            onEquipamientosChange)

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = "Icono busqueda",
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Buscar salas",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun salasDisponibles(salas: List<SalaConEquipamiento>,
                     onAtras: () -> Unit,
                     onReservas: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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

        Text("${salas.size} resultados - ${formatearFecha()}",
            color = Color(0xFF3D5A73),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 60.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(salas) { sala ->
                    salaCard(sala)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        agenda()

        Spacer(modifier = Modifier.height(12.dp))

        salaSeleccionada(onReservas)
    }
}
@Composable
fun barraBusqueda(texto: String,
                  onTextoChange: (String) -> Unit
) {
    OutlinedTextField(
        value = texto,
        onValueChange = onTextoChange,
        singleLine = true,
        placeholder = { Text("Búsqueda por código o nombre de sala",
            color = MaterialTheme.colorScheme.secondary.copy(0.75f),
            fontSize = 12.sp) },
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
fun cajaForm(filtros: FiltrosAvanzados,
             onFechaChange: (String) -> Unit,
             onHoraChange: (String) -> Unit,
             onCapacidadChange: (String) -> Unit,
             onAreaChange: (String) -> Unit,
             onEquipamientosChange: (Set<String>) -> Unit
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
                    seleccionarFecha("Seleccione fecha", filtros.fecha, onFechaChange)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    seleccionarHora("Selecciona hora", filtros.hora, onHoraChange)

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

            menuCapacidad("Capacidad máxima", filtros.capacidad, onCapacidadChange)

            Spacer(modifier = Modifier.height(12.dp))

            menuEquipamientos("Equipamientos disponibles", filtros.equipamientos, onEquipamientosChange)

            Spacer(modifier = Modifier.height(12.dp))

            menuArea("Área de conocimiento", filtros.area, onAreaChange)
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
fun menuEquipamientos(titulo: String,
                      seleccionados: Set<String>,
                      onSeleccionChange: (Set<String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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
                            val nuevosSeleccionados = if (isSelected) {
                                seleccionados - opcion
                            } else {
                                seleccionados + opcion
                            }
                            onSeleccionChange(nuevosSeleccionados)
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
fun salaCard(sala: SalaConEquipamiento) {

    val salaCompleta = sala.salaEntity
    val equipamientosTexto = if (sala.equipamiento.isEmpty()) {
        "Sin equipamiento"
    } else {
        sala.equipamiento.joinToString(", ") { it.nombre }
    }
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
                salaCompleta.codigo,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
            Text(
                salaCompleta.nombre,
                color = Color(0xFF3D5A73),
                fontSize = 12.sp
            )
            Text(
                "Capacidad ${salaCompleta.capacidad}",
                color = Color(0xFF3D5A73),
                fontSize = 12.sp
            )
            Text(
                equipamientosTexto,
                color = Color(0xFF3D5A73),
                fontSize = 12.sp
            )
            etiquetaOrganizador(salaCompleta.area.nombre)
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