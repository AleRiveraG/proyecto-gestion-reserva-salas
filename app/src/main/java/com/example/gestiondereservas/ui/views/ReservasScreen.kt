package com.example.gestiondereservas.ui.views

import android.widget.Space
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.gestiondereservas.data.AppState
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.EstadoReserva
import com.example.gestiondereservas.data.room.ReservaEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.collections.listOf

@Composable
fun ReservasScreen(appState: AppState) {
    var paso by remember{ mutableIntStateOf(1)}

    var nombre by remember({mutableStateOf("")})
    var responsable by remember({mutableStateOf("")})
    var telefono by remember({mutableStateOf("")})
    var correo by remember({mutableStateOf("")})
    var descripcion by remember { mutableStateOf("") }
    var tipoActividad by remember { mutableStateOf("") }
    var tieneValidacion by remember { mutableStateOf("") }
    var organizador by remember { mutableStateOf("") }

    var fechaActividad by remember { mutableStateOf("") }
    var horaInicio by remember { mutableStateOf("") }
    var horaTermino by remember { mutableStateOf("") }
    var fechaMontaje by remember { mutableStateOf("") }
    var horaMontaje by remember { mutableStateOf("") }
    var publicoSelecionado by remember { mutableStateOf<Set<String>>(emptySet()) }
    var requiereForm by remember { mutableStateOf("") }
    var salaId by remember { mutableStateOf<Long?>(null) }
    var nombres by remember { mutableStateOf("") }

    var servicios by remember { mutableStateOf<Map<String, Set<String>>>(emptyMap()) }
    var complemento by remember { mutableStateOf("") }
    var proveedores by remember { mutableStateOf("")}

    val scope = rememberCoroutineScope()

    fun crearReserva(): ReservaEntity {
        return ReservaEntity(
            nombreActividad = nombre,
            descripcion = descripcion,
            tipoActividad = tipoActividad,
            tieneValidacion = tieneValidacion == "Si",
            nombreResponsable = responsable,
            telefonoResponsable = telefono,
            correoResponsable = correo,
            organizador = organizador,

            salaId = salaId?: 1L,
            fechaActividad = fechaActividad,
            horaInicio = horaInicio,
            horaTermino = horaTermino,
            fechaMontaje = fechaMontaje,
            horaMontaje = horaMontaje,
            publico = publicoSelecionado.joinToString {", "},
            requiereFormulario = requiereForm == "Si",
            nombreExternos = nombres,

            servicios = servicios.entries.joinToString("; ") { (categoria, opciones) ->
                "$categoria: ${opciones.joinToString(", ")}" },
        )
    }

    fun guardarReserva() {
        val reserva = crearReserva()

        scope.launch {
            appState.registrarReserva(reserva)
        }
    }



    Scaffold(
        topBar = { TopBar() },
        bottomBar = { NavBar() }
    ) { padding ->
        when (paso) {
            1 -> ContentPaso1(
                padding,
                nombre,
                { nombre = it },
                responsable,
                { responsable = it },
                telefono,
                { telefono = it},
                correo,
                { correo = it },
                descripcion,
                { descripcion = it},
                tipoActividad,
                { tipoActividad = it},
                tieneValidacion,
                { tieneValidacion = it },
                organizador,
                { organizador = it },
                onAtras = { },
                onSiguiente = { paso = 2})
            2 -> ContentPaso2(
                padding,
                fechaActividad,
                { fechaActividad = it },
                horaInicio,
                { horaInicio = it},
                horaTermino,
                { horaTermino = it},
                fechaMontaje,
                { fechaMontaje = it},
                horaMontaje,
                { horaMontaje = it},
                publicoSelecionado,
                { publicoSelecionado = it },
                requiereForm,
                { requiereForm = it},
                salaId,
                { salaId = it},
                nombres,
                { nombre = it },
                onAtras = { paso = 1},
                onSiguiente = { paso = 3}
                )
            3 -> ContentPaso3(padding,
                complemento,
                { complemento = it },
                proveedores,
                { proveedores = it },
                servicios,
                { categoria, nuevosServicios -> servicios = servicios + ( categoria to nuevosServicios)},
                onAtras = { paso = 2 },
                onEnviar = { paso = 4} )
            4 -> Final(padding, onEnviar = { guardarReserva() })
        }
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
fun ContentPaso1(padding: PaddingValues,
                 nombre: String,
                 onNombreChange: (String) -> Unit,
                 responsable: String,
                 onResponsableChange: (String) -> Unit,
                 telefono: String,
                 onTelefonoChange: (String) -> Unit,
                 correo: String,
                 onCorreoChange: (String) -> Unit,
                 descripcion: String,
                 onDescripcionChange: (String) -> Unit,
                 actividad: String,
                 onActividadChange: (String) -> Unit,
                 tieneAutorizacion: String,
                 onTieneAutorizacionChange: (String) -> Unit,
                 organizador: String,
                 onOrganizadorChange: (String) -> Unit,
                 onAtras: () -> Unit,
                 onSiguiente: () -> Unit ) {
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
            IconButton(onClick = { onAtras() }) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "Icono volver atras",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
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
            modifier = Modifier.padding(start = 56.dp)
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
            Column {
                campoTexto("Nombre de la actividad *", nombre, onNombreChange)

                Spacer(modifier = Modifier.height(16.dp))

                campoDescripcion("Descripción de la actividad", descripcion, onDescripcionChange)

                Spacer(modifier = Modifier.height(16.dp))

                tipoActividad("Tipo de actividad *", actividad, onActividadChange)

                Spacer(modifier = Modifier.height(16.dp))

                tieneAutorizacion("¿Tu actividad tiene la validación de la dirección de la sede o de la subdirección de tu área?", tieneAutorizacion, onTieneAutorizacionChange)

                Spacer(modifier = Modifier.height(16.dp))

                campoTexto("Responsable de la actividad *", responsable, onResponsableChange)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        campoTexto("Teléfono de contacto", telefono, onTelefonoChange)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        campoTexto("Correo electrónico", correo, onCorreoChange)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                menuEscuelas("Área o Escuela que organiza la actividad", organizador, onOrganizadorChange)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Button(
            onClick = { onSiguiente() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(60.dp)
        ) {
            Text(
                "Siguiente",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ContentPaso2(padding: PaddingValues,
                 fechaActividad:String,
                 onFechaChange: (String) -> Unit,
                 horaInicio:String,
                 onHoraInicioChange: (String) -> Unit,
                 horaTermino: String,
                 onHoraTerminoChange: (String) -> Unit,
                 fechaMontaje: String,
                 onFechaMontajeChange: (String) -> Unit,
                 horaMontaje: String,
                 onHoraMontajeChange: (String) -> Unit,
                 publicoSeleccionado: Set<String>,
                 onPublicoChange: (Set<String>) -> Unit,
                 requiereForm: String,
                 onRequiereFormChange: (String) -> Unit,
                 salaId: Long?,
                 onSalaIdChange: (Long) -> Unit,
                 nombres: String,
                 onNombresChange: (String) -> Unit,
                 onAtras: () -> Unit,
                 onSiguiente: () -> Unit)
{
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
            IconButton(onClick = { onAtras() } ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "Icono volver atras",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                "Reservar sala",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            "Paso 2 de 3",
            color = Color(0xFF3B3B3B),
            fontSize = 15.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(start = 56.dp)
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
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = "Icono Check",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        "Actividad",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                    )
                }

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
                            "2",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        "Salas",
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

        Spacer(modifier = Modifier.height(12.dp))

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
            Column {
                recinto("Recinto *", salaId, onSalaIdChange)

                Spacer(modifier = Modifier.height(12.dp))

                Text("Fecha y horario *", color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(12.dp))

                seleccionarFecha("Fecha de la actividad",
                    fechaActividad,
                    onFechaChange)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        seleccionarHora("Hora de inicio", horaInicio, onHoraInicioChange)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        seleccionarHora("Hora de termino", horaTermino, onHoraTerminoChange)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Montaje *",
                    color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        seleccionarFecha("Fecha", fechaMontaje, onFechaMontajeChange)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        seleccionarHora("Hora", horaMontaje, onHoraMontajeChange)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                publico("Público Invitado", publicoSeleccionado, onPublicoChange)

                Spacer(modifier = Modifier.height(12.dp))

                necesitaForm(requiereForm, onRequiereFormChange)

                Spacer(modifier = Modifier.height(12.dp))

                nombres(nombres, onNombresChange)

                Spacer(modifier = Modifier.height(12.dp))

                campoArchivo("Adjunte el programa, pauta, libreto y/o layout de la" +
                        " actividad")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { onAtras() },
                modifier = Modifier.fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .height(60.dp)
                    .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(48.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                )
            ) {
                Text("Atrás",
                    color = MaterialTheme.colorScheme.primary)
            }
            Button(onClick = { onSiguiente() },
                modifier = Modifier.fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("Siguiente",
                    color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun ContentPaso3(padding: PaddingValues,
                 complemento: String,
                 onComplementoChange: (String) -> Unit,
                 proveedores: String,
                 onProvedooresChange: (String) -> Unit,
                 servicios: Map<String, Set<String>>,
                 onServiciosChange: (String, Set<String>) -> Unit,
                 onAtras: () -> Unit,
                 onEnviar: () -> Unit) {

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
            IconButton(onClick = { onAtras() }) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "Icono volver atras",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                "Reservar sala",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            "Paso 3 de 3",
            color = Color(0xFF3B3B3B),
            fontSize = 15.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(start = 56.dp)
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
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = "Icono Check",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        "Actividad",
                        color = MaterialTheme.colorScheme.primary,
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
                            .background(MaterialTheme.colorScheme.tertiary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = "Icono Check",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        "Salas",
                        color = MaterialTheme.colorScheme.primary,
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
                            .background(MaterialTheme.colorScheme.tertiary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "3",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        "Requisitos",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        menuOpciones("Área de Comunicaciones",
            "comunicaciones",
                listOf("Registro de Fotos & Videos",
                    "Difusión en RRSS(previa y posterior)"),
                    servicios["comunicaciones"] ?: emptySet(),
                    onServiciosChange)


        Spacer(modifier = Modifier.height(12.dp))

        menuOpciones("Área de Extensión",
            "extension",
            listOf("Manteles",
                "Vaso de Agua",
                "Banderas"),
            servicios["extension"] ?: emptySet(),
            onServiciosChange)

        Spacer(modifier = Modifier.height(12.dp))

        menuOpciones("Servicios Generales",
            "generales",
            listOf("Aseo y montaje",
                "Mesas Plegables",
                "Toldos"),
            servicios["generales"] ?: emptySet(),
            onServiciosChange)

        Spacer(modifier = Modifier.height(12.dp))

        menuOpciones("Servicios Digitales y CTA",
            "digitales",
            listOf("Amplificación",
                "Microfonos",
                "Iluminación Parrilla",
                "Proyección",
                "Streaming",
                "Podium"),
            servicios["digitales"] ?: emptySet(),
            onServiciosChange)

        Spacer(modifier = Modifier.height(12.dp))

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
            Column{

                campoTexto("Señale información que complemente los requerimientos y montaje de la actividad", complemento, onComplementoChange)

                Spacer(modifier = Modifier.height(12.dp))

                campoTexto("Contratación de proveedores externos", proveedores, onProvedooresChange)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { onAtras() },
                modifier = Modifier.fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .height(60.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(48.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                )
            ) {
                Text("Atrás",
                    color = MaterialTheme.colorScheme.primary)
            }
            Button(onClick = { onEnviar() },
                modifier = Modifier.fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("Enviar",
                    color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun Final(padding: PaddingValues, onEnviar: () -> Unit){
    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .padding(padding)
            .padding( 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(160.dp)
                .background(Color(0xFFFFF9E6)),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier
                .clip(CircleShape)
                .size(120.dp)
                .background(Color(0xFFFFF9E6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.hourglass),
                    contentDescription = "Icono reloj de arena",
                    tint = MaterialTheme.colorScheme.tertiary,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("SOLICITUD ENVIADA",
            color = Color(0xFF424242),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(12.dp))

        Text("¡Ya casi está!",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(12.dp))

        Text("Tu solicitud quedó pendiente de validación por el " +
                "coordinador del área",
            color = Color(0xFF424242),
            fontSize = 16.sp,
            textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(12.dp))

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
                .padding(16.dp)
        ) {
            Column{
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.door),
                        contentDescription = "Icono puerta",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Recinto: ",
                        color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.calendar),
                        contentDescription = "Icono calendario",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Montaje: ",
                        color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.clock),
                        contentDescription = "Icono reloj",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Fecha: ",
                        color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Servicios solicitados",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold)
            }
        }

        Button(
            onClick = { onEnviar() },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("Volver al inicio",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp
            )
        }
    }
}
@Composable
fun campoTexto(titulo: String, variable: String, onValorChange: (String) -> Unit) {
    Text(text = titulo,
        color = MaterialTheme.colorScheme.primary)

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = variable,
        onValueChange = onValorChange ,
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
fun campoDescripcion(titulo: String, variable: String, onValorChange: (String) -> Unit) {
    Text(text = titulo,
        color = MaterialTheme.colorScheme.primary)

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = variable,
        onValueChange = onValorChange,
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
fun tipoActividad(titulo: String, opcion: String, onOpcionChange: (String) -> Unit) {
    val opciones = listOf(
        "",
        "Ceremonia Institucional",
        "Actividad Académica",
        "Actividad Extracurricular",
        "Duoc UC A Puertas Abiertas",
        "Actividad Sede",
        "Actividad de Externo (no organizada por Duoc UC)"
    )
    Column {
        Text(text = titulo,
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
                onClick = { onOpcionChange(seleccion) },
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
fun tieneAutorizacion(titulo: String, opcion: String, onTieneAutorizacionChange: (String) -> Unit) {
    val opciones = listOf(
        "",
        "Si",
        "No"
    )

    Column{
        Text(
            text = titulo,
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
                onClick = { onTieneAutorizacionChange(seleccion) },
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
fun menuEscuelas(titulo: String, opcion: String, onOrganizadorChange: (String) -> Unit) {
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
                            onOrganizadorChange(seleccion)
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
fun recinto(titulo: String, salaId: Long?, onRecintoChange: (Long) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var opcion by remember { mutableStateOf("") }

    Column {
        Text(text = titulo,
            color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(12.dp))

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
                DropdownMenuItem(
                    text = { Text("Salas", color = MaterialTheme.colorScheme.primary) },
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun seleccionarFecha(titulo: String, fecha:String, onFechaChange: (String) -> Unit) {
    var mostrar by remember { mutableStateOf(false) }

    Column {
        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp
        )

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = fecha,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { mostrar = !mostrar }) {
                        Icon(
                            painter = painterResource(R.drawable.calendar),
                            contentDescription = "Icono calendario",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (mostrar) {
                val datePickerState = rememberDatePickerState()

                DatePickerDialog(
                    onDismissRequest = { mostrar = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                mostrar = false
                                datePickerState.selectedDateMillis?.let { millis ->
                                    onFechaChange(formatoFecha(millis))
                                }
                            }
                        ) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { mostrar = false }
                        ) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }
}

fun formatoFecha(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun seleccionarHora(titulo: String, hora: String, onHoraChange: (String) -> Unit) {
    var mostrar by remember { mutableStateOf(false) }

    Column{
        Text(text = titulo,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp)

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = hora,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { mostrar = !mostrar }) {
                        Icon(
                            painter = painterResource(R.drawable.clock),
                            contentDescription = "Icono calendario",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (mostrar) {
            val timePickerState = rememberTimePickerState(
                initialHour = 12,
                initialMinute = 0,
                is24Hour = true
            )
            AlertDialog(
                onDismissRequest = { mostrar = false },
                title = { Text("Ingresar hora") },
                text = {
                    TimeInput(state = timePickerState)
                },
                confirmButton = {
                    TextButton(onClick = {
                        mostrar = false
                        onHoraChange(
                            String.format(
                                Locale.getDefault(),
                                "%02d:%02d",
                                timePickerState.hour,
                                timePickerState.minute
                            )
                        )
                    }
                    ) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrar = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun publico(titulo: String, publicoSeleccionado: Set<String>, onPublicoChange: (Set<String>) -> Unit) {
    val opciones = listOf(
        "",
        "Estudiantes",
        "Docentes",
        "Administrativos",
        "Externos"
    )

    Column {
        Text(text = titulo  ,
            color = MaterialTheme.colorScheme.primary)
    }

    Spacer(modifier = Modifier.height(8.dp))

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        opciones.drop(1).forEach { seleccion ->
            val isSelected = seleccion in publicoSeleccionado
            FilterChip(
                selected = isSelected,
                onClick = {
                    onPublicoChange(
                        if (seleccion in publicoSeleccionado) {
                            publicoSeleccionado - seleccion
                        } else {
                            publicoSeleccionado + seleccion
                        }
                    )
                },
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
fun necesitaForm(opcion: String, onOpcionChange: (String) -> Unit){
    val opciones = listOf(
        "",
        "Si",
        "No"
    )

    Column{
        Text(
            "¿Requiere formulario de inscripción de público asistente?",
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
                onClick = { onOpcionChange(seleccion) },
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
fun nombres(nombres: String, onNombresChange: (String) -> Unit){
    Text("Especifique nombres de expositores, charlistas, y autoridades invitadas, internas y externas",
        color = MaterialTheme.colorScheme.primary)

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = nombres,
        onValueChange = onNombresChange,
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
fun campoArchivo(titulo: String){
    Column {
        Text(text = titulo,
            color = MaterialTheme.colorScheme.primary)

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = { },
                readOnly = true,
                label = { Text("Seleccionar archivos - máx 5 MB, hasta 20",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp) },
                trailingIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(R.drawable.paperclip),
                            contentDescription = "Icono adjuntar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5)
                )
            )
        }
    }
}

@Composable
fun menuOpciones(titulo: String,
                 categoria: String,
                 opciones: List<String>,
                 servicios: Set<String>,
                 onServiciosChange: (String, Set<String>) -> Unit
){

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
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = titulo, color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(8.dp))

            opciones.forEach { opcion ->
                val estaSeleccionado = opcion in servicios
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                        .background(
                            color = if (estaSeleccionado) Color(0xFFFFF9E6) else Color(
                                0xFFF5F5F5
                            )
                        )
                ) {
                    Checkbox(
                        checked = estaSeleccionado,
                        onCheckedChange = { isChecked ->
                            val nuevosServicios = if (isChecked) {
                                servicios + opcion
                            } else {
                                servicios - opcion
                            }

                            onServiciosChange(categoria, nuevosServicios)
                        },
                        colors = CheckboxDefaults.colors(
                            uncheckedColor = MaterialTheme.colorScheme.tertiary,
                            checkedColor = Color(0xFFFFF9E6),
                            checkmarkColor = MaterialTheme.colorScheme.primary,
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = opcion,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
