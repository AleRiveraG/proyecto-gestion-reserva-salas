package com.example.gestiondereservas.ui.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestiondereservas.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gestiondereservas.data.repository.ReservaRepository
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.AuditoriaEntity
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import com.example.gestiondereservas.data.room.entidades.TipoAuditoria
import com.example.gestiondereservas.viewmodel.ReservaViewModel
import com.example.gestiondereservas.utils.ValidacionesReserva
import com.example.gestiondereservas.utils.formatearFechaPerso
import com.example.gestiondereservas.utils.formatearHora
import com.example.gestiondereservas.utils.formatoFecha
import java.time.LocalDate
import java.util.Locale
import kotlin.collections.listOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.toggleable
import com.example.gestiondereservas.utils.AnimacionCarga
import com.example.gestiondereservas.utils.AnimacionReloj
import com.example.gestiondereservas.utils.formatoFechaPantalla
import com.example.gestiondereservas.viewmodel.UsuarioViewModel

@Composable
fun rememberReservaViewModel(): ReservaViewModel {
    val contexto = LocalContext.current.applicationContext
    return viewModel(
        factory = viewModelFactory {
            initializer {
                val dao = AppDatabase.getInstance(contexto).reservaDao()
                val daoSalas = AppDatabase.getInstance(contexto).salaDao()
                val repository = ReservaRepository(dao, daoSalas)
                ReservaViewModel(repository)
            }
        }
    )
}

@Composable
fun ReservasScreen(viewModel: ReservaViewModel = rememberReservaViewModel(),
                   usuarioViewModel: UsuarioViewModel = rememberUsuarioViewModel(),
                   fechaB: String? = null,
                   horaB: String? = null,
                   salaIdB: Long? = null,
                   idUsuario: Long,
                   onAtras: () -> Unit,
                   onEnviar: () -> Unit,
                   onRegistrarAuditoria: (AuditoriaEntity) -> Unit
) {
    var paso by remember{ mutableIntStateOf(1)}
    var mostrarCarga by remember { mutableStateOf(false) }
    var validacionReactivaPaso1 by remember { mutableStateOf(false) }
    var validacionReactivaPaso2 by remember { mutableStateOf(false) }

    var nombre by remember({mutableStateOf("")})
    var responsable by remember({mutableStateOf("")})
    var telefono by remember({mutableStateOf("")})
    var correo by remember({mutableStateOf("")})
    var descripcion by remember { mutableStateOf("") }
    var tipoActividad by remember { mutableStateOf("") }
    var tieneValidacion by remember { mutableStateOf("") }
    var organizador by remember { mutableStateOf("") }

    var fechaActividad by remember { mutableStateOf(fechaB ?: "") }
    var horaInicio by remember { mutableStateOf(horaB ?: "") }
    var horaTermino by remember { mutableStateOf("") }
    var fechaMontaje by remember { mutableStateOf("") }
    var horaMontaje by remember { mutableStateOf("") }
    var publicoSelecionado by remember { mutableStateOf<Set<String>>(emptySet()) }
    var requiereForm by remember { mutableStateOf("") }
    var salaId by remember { mutableLongStateOf(salaIdB ?: 0L) }
    var nombres by remember { mutableStateOf("") }

    var servicios by remember { mutableStateOf<Map<String, Set<String>>>(emptyMap()) }
    var complemento by remember { mutableStateOf("") }
    var proveedores by remember { mutableStateOf("")}

    val nombreError = validacionReactivaPaso1 && !ValidacionesReserva.validarTexto(nombre)
    val menuTipoActividadError = validacionReactivaPaso1 && !ValidacionesReserva.validarMenu(tipoActividad)
    val menuTieneValidacionError = validacionReactivaPaso1 && !ValidacionesReserva.validarMenu(tieneValidacion)
    val menuAreaError = validacionReactivaPaso1 && !ValidacionesReserva.validarMenu(organizador)
    val menuRecintoError = validacionReactivaPaso2 && !ValidacionesReserva.validarSala(salaId)
    val fechaActividadError = validacionReactivaPaso2 && !ValidacionesReserva.validarFechas(fechaActividad)
    val horaInicioError = validacionReactivaPaso2 && !ValidacionesReserva.validarHoras(horaInicio)
    val horaTerminoError = validacionReactivaPaso2 && !ValidacionesReserva.validarHoras(horaTermino)
    val fechaMontajeError = validacionReactivaPaso2 && (!ValidacionesReserva.validarFechas(fechaMontaje) || !ValidacionesReserva.validarMontaje(fechaMontaje, fechaActividad))
    val horaMontajeError = validacionReactivaPaso2 && (!ValidacionesReserva.validarHoras(horaMontaje) || !ValidacionesReserva.validarHoraMontaje(fechaMontaje, fechaActividad, horaMontaje, horaInicio))
    val publicoSeleccionadoError = validacionReactivaPaso2 && !ValidacionesReserva.validarMenuMultiple(publicoSelecionado)
    val requiereFormError = validacionReactivaPaso2 && !ValidacionesReserva.validarMenu(requiereForm)
    val nombresInvitadosError = validacionReactivaPaso2 && publicoSelecionado.contains("Externos") && !ValidacionesReserva.validarTexto(nombres)

    val scope = rememberCoroutineScope()
    var pdfUri by remember { mutableStateOf<Uri?>(null) }

    val selectorPdf = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if(uri != null) {
            pdfUri = uri
        }
    }

    val listaSalas by viewModel.listaSalas.collectAsState()
    val sala by viewModel.sala.collectAsState()
    val usuario by usuarioViewModel.usuario.collectAsState()

    LaunchedEffect(salaIdB, idUsuario) {
        if(salaIdB != null && salaIdB > 0L) {
            salaId = salaIdB
            viewModel.buscarSala(salaIdB)
        }

        usuarioViewModel.cargarUsuario(idUsuario)
    }

    fun crearReserva(): ReservaEntity {
        return ReservaEntity(
            idUsuario = idUsuario,
            nombreActividad = nombre,
            descripcion = descripcion,
            tipoActividad = tipoActividad,
            tieneValidacion = tieneValidacion == "Si",
            nombreResponsable = usuario!!.nombre,
            telefonoResponsable = usuario!!.telefono,
            correoResponsable = usuario!!.correo,
            organizador = organizador,

            salaId = salaId,
            fechaActividad = fechaActividad,
            horaInicio = horaInicio,
            horaTermino = horaTermino,
            fechaMontaje = fechaMontaje,
            horaMontaje = horaMontaje,
            publico = publicoSelecionado.joinToString (", "),
            requiereFormulario = requiereForm == "Si",
            nombreExternos = nombres,
            documentoPdf = pdfUri?.toString() ?: "",

            complementos = complemento,
            proveedores = proveedores,
            servicios = servicios.entries.joinToString("; ") { (categoria, opciones) ->
                "$categoria: ${opciones.joinToString(", ")}" },
        )
    }

    fun crearAuditoria(): AuditoriaEntity{
        return AuditoriaEntity(
            accion = "Se creo una solicitud de reserva",
            detalle = nombre,
            fecha = LocalDate.now().toString(),
            hora = formatearHora(),
            tipo = TipoAuditoria.RESERVA.name
        )
    }
    fun guardarReserva() {
        val reserva = crearReserva()
        viewModel.registrarReserva(reserva)

        val auditoria = crearAuditoria()
        onRegistrarAuditoria( auditoria )

        onEnviar()
    }


    when (paso) {
        1 -> ContentPaso1(
            nombre,
            { nombre = it },
            descripcion,
            { descripcion = it},
            tipoActividad,
            { tipoActividad = it},
            tieneValidacion,
            { tieneValidacion = it },
            organizador,
            { organizador = it },
            nombreError,
            menuAreaError,
            menuTipoActividadError,
            menuTieneValidacionError,
            onAtras = { onAtras() },
            onSiguiente = {
                validacionReactivaPaso1 = true
                if(ValidacionesReserva.validarTexto(nombre) &&
                    ValidacionesReserva.validarMenu(organizador) &&
                    ValidacionesReserva.validarMenu(tipoActividad) &&
                    ValidacionesReserva.validarMenu(tieneValidacion)
                    ) {
                    paso = 2
                }
            }
        )
        2 -> ContentPaso2(
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
            { id ->
                salaId = id
                viewModel.buscarSala(id)},
            salaId,
            listaSalas,
            nombres,
            { nombres = it },
            pdfUri,
            onSeleccionarPdf = { selectorPdf.launch(arrayOf("application/pdf"))},
            menuRecintoError,
            fechaActividadError,
            horaInicioError,
            horaTerminoError,
            fechaMontajeError,
            horaMontajeError,
            publicoSeleccionadoError,
            requiereFormError,
            nombresInvitadosError,
            onAtras = { paso = 1
                      validacionReactivaPaso1 = false},
            onSiguiente = {
                validacionReactivaPaso2 = true

                if(ValidacionesReserva.validarSala(salaId) &&
                    ValidacionesReserva.validarFechas(fechaActividad) &&
                    ValidacionesReserva.validarHoras(horaInicio) &&
                    ValidacionesReserva.validarHoras(horaTermino) &&
                    ValidacionesReserva.validarFechas(fechaMontaje) &&
                    ValidacionesReserva.validarHoras(horaMontaje) &&
                    ValidacionesReserva.validarMenuMultiple(publicoSelecionado) &&
                    ValidacionesReserva.validarMenu(requiereForm) &&
                    (ValidacionesReserva.validarTexto(nombres) || !publicoSelecionado.contains("Externos")) &&
                    ValidacionesReserva.validarMontaje(fechaMontaje, fechaActividad) &&
                    ValidacionesReserva.validarHoraMontaje(fechaMontaje, fechaActividad, horaMontaje, horaInicio)
                    ) {
                    paso = 3
                }
            }
        )
        3 -> ContentPaso3(
            complemento,
            { complemento = it },
            proveedores,
            { proveedores = it },
            servicios,
            { categoria, nuevosServicios -> servicios = servicios + ( categoria to nuevosServicios)},
            onAtras = { paso = 2
                      validacionReactivaPaso2 = false},
            onEnviar = {
                scope.launch {
                    mostrarCarga = true

                    delay(1500)

                    mostrarCarga = false
                    paso = 4
                } } )
        4 -> Final(onEnviar = { guardarReserva() },
            sala,
            fechaMontaje,
            horaMontaje,
            fechaActividad,
            horaInicio,
            horaTermino)
    }

    if(mostrarCarga) {
        AnimacionCarga()
    }
}

@Composable
fun ContentPaso1(nombre: String,
                 onNombreChange: (String) -> Unit,
                 descripcion: String,
                 onDescripcionChange: (String) -> Unit,
                 actividad: String,
                 onActividadChange: (String) -> Unit,
                 tieneAutorizacion: String,
                 onTieneAutorizacionChange: (String) -> Unit,
                 organizador: String,
                 onOrganizadorChange: (String) -> Unit,
                 nombreError: Boolean,
                 menuAreaError: Boolean,
                 menuTipoActividadError: Boolean,
                 menuTieneValidacionError: Boolean,
                 onAtras: () -> Unit,
                 onSiguiente: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color(0xFFFFFFFF))
            .fillMaxSize()
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
                CampoTexto("Nombre de la actividad *",
                    nombre,
                    onNombreChange,
                    nombreError,
                    "Nombre de la actividad invalido. Ingrese nuevamente")

                Spacer(modifier = Modifier.height(16.dp))

                CampoDescripcion("Descripción de la actividad", descripcion, onDescripcionChange)

                Spacer(modifier = Modifier.height(16.dp))

                TipoActividad("Tipo de actividad *", actividad, onActividadChange, menuTipoActividadError)

                Spacer(modifier = Modifier.height(16.dp))

                TieneAutorizacion("¿Tu actividad tiene la validación de la dirección de la sede o de la subdirección de tu área?", tieneAutorizacion, onTieneAutorizacionChange, menuTieneValidacionError)

                Spacer(modifier = Modifier.height(16.dp))

                MenuEscuelas("Área o Escuela que organiza la actividad", organizador, onOrganizadorChange, menuAreaError)

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
fun ContentPaso2(fechaActividad:String,
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
                 onSalaIdChange: (Long) -> Unit,
                 salaId: Long,
                 listaSalas: List<SalaEntity>,
                 nombres: String,
                 onNombresChange: (String) -> Unit,
                 pdfUri: Uri?,
                 onSeleccionarPdf: () -> Unit,
                 menuRecintoError: Boolean,
                 fechaActividadError: Boolean,
                 horaInicioError: Boolean,
                 horaTerminoError: Boolean,
                 fechaMontajeError: Boolean,
                 horaMontajeError: Boolean,
                 publicoSeleccionadoError: Boolean,
                 requiereFormError: Boolean,
                 nombresInvitadosError: Boolean,
                 onAtras: () -> Unit,
                 onSiguiente: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color(0xFFFFFFFF))
            .fillMaxSize()
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
                Recinto("Recinto *", listaSalas, onSalaIdChange, menuRecintoError, salaId)

                Spacer(modifier = Modifier.height(12.dp))

                Text("Fecha y horario *", color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(12.dp))

                SeleccionarFecha("Fecha de la actividad",
                    fechaActividad,
                    onFechaChange,
                    fechaActividadError)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        SeleccionarHora("Hora de inicio",
                            horaInicio,
                            onHoraInicioChange,
                            horaInicioError)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        SeleccionarHora("Hora de termino",
                            horaTermino,
                            onHoraTerminoChange,
                            horaTerminoError)
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
                        SeleccionarFecha("Fecha",
                            fechaMontaje,
                            onFechaMontajeChange,
                            fechaMontajeError)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        SeleccionarHora("Hora",
                            horaMontaje,
                            onHoraMontajeChange,
                            horaMontajeError)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Publico("Público Invitado",
                    publicoSeleccionado,
                    onPublicoChange,
                    publicoSeleccionadoError)

                Spacer(modifier = Modifier.height(12.dp))

                NecesitaForm(requiereForm, onRequiereFormChange, requiereFormError )

                Spacer(modifier = Modifier.height(12.dp))

                Nombres(nombres, onNombresChange, nombresInvitadosError)

                Spacer(modifier = Modifier.height(12.dp))

                CampoArchivo("Adjunte el programa, pauta, libreto y/o layout de la" +
                        " actividad", pdfUri, onSeleccionarPdf)
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
fun ContentPaso3(complemento: String,
                 onComplementoChange: (String) -> Unit,
                 proveedores: String,
                 onProvedooresChange: (String) -> Unit,
                 servicios: Map<String, Set<String>>,
                 onServiciosChange: (String, Set<String>) -> Unit,
                 onAtras: () -> Unit,
                 onEnviar: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color(0xFFFFFFFF))
            .fillMaxSize()
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

        MenuOpciones("Área de Comunicaciones",
            "comunicaciones",
                listOf("Registro de Fotos & Videos",
                    "Difusión en RRSS(previa y posterior)"),
                    servicios["comunicaciones"] ?: emptySet(),
                    onServiciosChange)


        Spacer(modifier = Modifier.height(12.dp))

        MenuOpciones("Área de Extensión",
            "extension",
            listOf("Manteles",
                "Vaso de Agua",
                "Banderas"),
            servicios["extension"] ?: emptySet(),
            onServiciosChange)

        Spacer(modifier = Modifier.height(12.dp))

        MenuOpciones("Servicios Generales",
            "generales",
            listOf("Aseo y montaje",
                "Mesas Plegables",
                "Toldos"),
            servicios["generales"] ?: emptySet(),
            onServiciosChange)

        Spacer(modifier = Modifier.height(12.dp))

        MenuOpciones("Servicios Digitales y CTA",
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

                CampoTexto("Señale información que complemente los requerimientos y montaje de la actividad",
                    complemento,
                    onComplementoChange,
                    false,
                    "")

                Spacer(modifier = Modifier.height(12.dp))

                CampoTexto("Contratación de proveedores externos",
                    proveedores,
                    onProvedooresChange,
                    false,
                    "")
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
fun Final(onEnviar: () -> Unit,
          sala: SalaEntity?,
          fechaMontaje: String,
          horaMontaje: String ,
          fechaActividad: String,
          horaInicio: String,
          horaTermino: String
){
    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFFFFFFF))
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
                AnimacionReloj()
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

                    Text("Recinto: ${sala?.codigo}",
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

                    Text("Montaje: ${formatearFechaPerso(fechaMontaje)}, $horaMontaje",
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

                    Text("Fecha: ${formatearFechaPerso(fechaActividad)}, $horaInicio a $horaTermino",
                        color = MaterialTheme.colorScheme.primary)
                }
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
fun CampoTexto(titulo: String,
               variable: String,
               onValorChange: (String) -> Unit,
               error: Boolean,
               mensajeError: String
) {
    Text(text = titulo,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 14.sp)

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = variable,
        onValueChange = onValorChange ,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        isError = error,
        supportingText = {
            if(error) {
                Text(mensajeError, color = MaterialTheme.colorScheme.error)
            }
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5)
        )
    )
}

@Composable
fun CampoDescripcion(titulo: String,
                     variable: String,
                     onValorChange: (String) -> Unit
) {
    Text(text = titulo,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 14.sp)

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
fun TipoActividad(titulo: String,
                  opcion: String,
                  onOpcionChange: (String) -> Unit,
                  menuTipoActividadError: Boolean
) {
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
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp)
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
                    labelColor =  if(menuTipoActividadError) MaterialTheme.colorScheme.error else Color(0xFF4B5563)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if(menuTipoActividadError) MaterialTheme.colorScheme.error else Color(0xFFE5E7EB),
                    selectedBorderColor = MaterialTheme.colorScheme.tertiary
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }

    if(menuTipoActividadError) {
        Spacer(modifier = Modifier.height(4.dp))

        Text("Seleccione una opción",
            color = MaterialTheme.colorScheme.error,
            fontSize = 10.sp
        )
    }
}

@Composable
fun TieneAutorizacion(titulo: String,
                      opcion: String,
                      onTieneAutorizacionChange: (String) -> Unit,
                      menuTieneValidacionError: Boolean
) {
    val opciones = listOf(
        "",
        "Si",
        "No"
    )

    Column{
        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp
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
                    labelColor = if(menuTieneValidacionError) MaterialTheme.colorScheme.error else Color(0xFF4B5563)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if(menuTieneValidacionError) MaterialTheme.colorScheme.error else Color(0xFFE5E7EB),
                    selectedBorderColor = MaterialTheme.colorScheme.tertiary
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }

    if(menuTieneValidacionError) {
        Spacer(modifier = Modifier.height(4.dp))

        Text("Seleccione una opción",
            color = MaterialTheme.colorScheme.error,
            fontSize = 10.sp
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuEscuelas(titulo: String,
                 opcion: String,
                 onOrganizadorChange: (String) -> Unit,
                 menuAreaError: Boolean
) {
    val opciones = listOf(
        "",
        "Administración y Negocios",
        "Comunicación",
        "Construcción",
        "Diseño",
        "Gastronomía",
        "Informática y Telecomunicaciones",
        "Ingeniería y Recursos Naturales",
        "Salud y Bienestar",
        "Turismo y Hospitalidad"
    )

    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp
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
                isError = menuAreaError,
                supportingText = {
                    if(menuAreaError) {
                        Text("Seleccione una opción",
                            color = MaterialTheme.colorScheme.error)
                    }
                },
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
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedContainerColor = Color(0xFFF5F5F5)
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

@SuppressLint("InvalidColorHexValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Recinto(titulo: String,
            listaSalas: List<SalaEntity>,
            onRecintoChange: (Long) -> Unit,
            menuRecintoError: Boolean,
            salaId: Long
) {
    var expanded by remember { mutableStateOf(false) }
    var opcion by remember { mutableStateOf("") }

    LaunchedEffect(salaId, listaSalas) {
        opcion = listaSalas.find { it.id == salaId }?.nombre ?: ""
    }

    Column {
        Text(text = titulo,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = opcion,
                onValueChange = {},
                readOnly = true,
                isError = menuRecintoError,
                supportingText = {
                    if(menuRecintoError) {
                        Text("Sala ingresada invalida. Seleccione nuevamente", color = MaterialTheme.colorScheme.error)
                    }
                },
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
                listaSalas.forEach { sala ->
                    DropdownMenuItem(
                        onClick = { opcion = sala.nombre
                                  onRecintoChange(sala.id)
                                  expanded = false},
                        text = {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.door),
                                    contentDescription = "Icono sala"
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Column() {
                                    Text("${sala.codigo} - ${sala.nombre}",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 16.sp)
                                    Text("Cap.${sala.capacidad} - ${sala.area}",
                                        color = Color(0xFF557A99))
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SeleccionarFecha(titulo: String,
                     fecha:String,
                     onFechaChange: (String) -> Unit,
                     fechaError: Boolean
) {
    var mostrar by remember { mutableStateOf(false) }

    Column {
        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp
        )

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = formatoFechaPantalla(fecha) ?: "",
                onValueChange = { },
                readOnly = true,
                isError = fechaError,
                supportingText = {
                    if(fechaError) {
                        Text("Fecha seleccionada invalida. Ingrese nuevamente", color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { mostrar = !mostrar }) {
                        Icon(
                            painter = painterResource(R.drawable.calendar),
                            contentDescription = "Icono calendario",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = MaterialTheme.colorScheme.primary
                )
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
                            Text("Aceptar",
                                color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { mostrar = false }
                        ) {
                            Text("Cancelar",
                                color = MaterialTheme.colorScheme.primary)
                        }
                    },
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionarHora(titulo: String,
                    hora: String,
                    onHoraChange: (String) -> Unit,
                    horaError: Boolean
) {
    var mostrar by remember { mutableStateOf(false) }

    Column{
        Text(text = titulo,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp)

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = hora,
                onValueChange = { },
                readOnly = true,
                isError = horaError,
                supportingText = {
                    if(horaError) {
                        Text("Hora seleccionada invalida. Ingrese nuevamente", color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { mostrar = !mostrar }) {
                        Icon(
                            painter = painterResource(R.drawable.clock),
                            contentDescription = "Icono calendario",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = MaterialTheme.colorScheme.primary
                )
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
                        Text("Aceptar",
                            color = MaterialTheme.colorScheme.primary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrar = false }) {
                        Text("Cancelar",
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    }
}

@Composable
fun Publico(titulo: String,
            publicoSeleccionado: Set<String>,
            onPublicoChange: (Set<String>) -> Unit,
            publicoError: Boolean
) {
    val opciones = listOf(
        "",
        "Estudiantes",
        "Docentes",
        "Administrativos",
        "Externos"
    )

    Column {
        Text(text = titulo,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp)
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
                    labelColor = if(publicoError) MaterialTheme.colorScheme.error else Color(0xFF4B5563)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if(publicoError) MaterialTheme.colorScheme.error else Color(0xFFE5E7EB),
                    selectedBorderColor = MaterialTheme.colorScheme.tertiary
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun NecesitaForm(opcion: String,
                 onOpcionChange: (String) -> Unit,
                 formError: Boolean
) {
    val opciones = listOf(
        "",
        "Si",
        "No"
    )

    Column{
        Text(
            "¿Requiere formulario de inscripción de público asistente?",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp
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
                    labelColor = if(formError) MaterialTheme.colorScheme.error else Color(0xFF4B5563)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if(formError) MaterialTheme.colorScheme.error else Color(0xFFE5E7EB),
                    selectedBorderColor = MaterialTheme.colorScheme.tertiary
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun Nombres(nombres: String,
            onNombresChange: (String) -> Unit,
            nombresError: Boolean = false
) {
    Text("Especifique nombres de expositores, charlistas, y autoridades invitadas, internas y externas",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 14.sp)

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = nombres,
        onValueChange = onNombresChange,
        isError = nombresError,
        supportingText = {
            if(nombresError) {
                Text("Debe indicar los nombres del público externo", color = MaterialTheme.colorScheme.error)
            }
        },
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
fun CampoArchivo(titulo: String,
                 uri: Uri?,
                 onSeleccionarPdf: () -> Unit
) {
    val contexto = LocalContext.current
    val nombreArchivo = remember(uri) {
        if(uri == null) "" else {
            var nombre = "documento.pdf"
            contexto.contentResolver.query(uri,
                null,
                null,
                null,
                null)?.use { cursor ->
                    val nombreIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && nombreIndex != -1 ) {
                        nombre = cursor.getString(nombreIndex)
                    }
            }
            nombre
        }
    }

    Column {
        Text(text = titulo,
            color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
                .clickable(onClick = { onSeleccionarPdf() })
        ) {
            OutlinedTextField(
                value = nombreArchivo,
                onValueChange = { },
                readOnly = true,
                enabled = false,
                placeholder = { Text("Seleccionar archivos - máx 5 MB, hasta 20",
                    color = MaterialTheme.colorScheme.primary.copy(0.6f),
                    fontSize = 12.sp) },
                trailingIcon = {
                    IconButton(onClick = { onSeleccionarPdf() }) {
                        Icon(
                            painter = painterResource(R.drawable.paperclip),
                            contentDescription = "Icono adjuntar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
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
    }
}

@Composable
fun MenuOpciones(titulo: String,
                 categoria: String,
                 opciones: List<String>,
                 servicios: Set<String>,
                 onServiciosChange: (String, Set<String>) -> Unit
) {

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
            Text(text = titulo,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(4.dp))

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
                        .toggleable(
                            value = estaSeleccionado,
                            onValueChange = { isChecked ->
                            val nuevosServicios = if (isChecked) {
                                servicios + opcion
                            } else {
                                servicios - opcion
                            }
                            onServiciosChange(categoria, nuevosServicios)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = estaSeleccionado,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(
                            uncheckedColor = MaterialTheme.colorScheme.tertiary,
                            checkedColor = Color(0xFFFFF9E6),
                            checkmarkColor = MaterialTheme.colorScheme.primary,
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = opcion,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
