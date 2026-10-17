package com.example.gestiondereservas.ui.views


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gestiondereservas.R
import com.example.gestiondereservas.data.Ocupacion
import com.example.gestiondereservas.data.ProveedorDatos
import com.example.gestiondereservas.data.ProveedorDatosHorarios
import com.example.gestiondereservas.data.TipoOcupacion
import com.example.gestiondereservas.data.repository.HorarioRepository
import com.example.gestiondereservas.data.room.entidades.DiaSemana
import com.example.gestiondereservas.data.room.entidades.Horario
import com.example.gestiondereservas.ui.viewmodel.HorariosViewModel
import com.example.gestiondereservas.utils.FechasSemana
import com.example.gestiondereservas.utils.FormatoHora
import com.example.gestiondereservas.utils.ValidacionesHorario
import java.util.Calendar

private val ColorLibre = Color.White
private val ColorBloqueado = Color(0xFFC9C9C9)
private val ColorReservado = Color(0xFFFFB800)
private val ColorClase = Color(0xFF307FE2)
private val ColorMontaje = Color(0xFFFFF3CC)
private val ColorBorde = Color(0xFFE5E5E5)
private val ColorTextoOscuro = Color(0xFF1F1F1F)

private val AltoCelda = 40.dp
private val AltoCabecera = 32.dp
private val AnchoCelda = 80.dp
private val AnchoHoras = 52.dp
private val SeparacionCeldas = 4.dp

@Composable
fun rememberHorariosViewModel(): HorariosViewModel {
    val contexto = LocalContext.current.applicationContext
    return viewModel(
        key = "horarios-coordinador-infraestructura",
        factory = viewModelFactory {
            initializer {
                HorariosViewModel(
                    HorarioRepository(
                        ProveedorDatosHorarios.horarioDao(contexto),
                        ProveedorDatos.salaDao(contexto)
                    ),
                    ProveedorDatosHorarios.ocupacionFuente(contexto)
                )
            }
        }
    )
}

@Composable
fun HorariosScreen(
    onNuevoHorario: (salaId: Long, dia: DiaSemana?, inicioMin: Int) -> Unit = { _, _, _ -> },
    onEditarHorario: (Long) -> Unit = {},
    onVolver: (() -> Unit)? = null,
    puedeEditar: Boolean = true,
    // El trabajador toca un bloque libre entrega sala, fecha ("dd/MM") y horas ("HH:mm")
    // del bloque para abrir el formulario de reserva ya rellenado.
    onSolicitarBloque: (salaId: Long, fecha: String, horaInicio: String, horaTermino: String) -> Unit =
        { _, _, _, _ -> },
    viewModel: HorariosViewModel = rememberHorariosViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    var desplegarSalas by remember { mutableStateOf(false) }
    var detalle by remember { mutableStateOf<Pair<Ocupacion, DiaSemana>?>(null) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var solicitud by remember { mutableStateOf<SolicitudPendiente?>(null) }
    val sala = estado.sala
    val dias = remember(estado.semana) { FechasSemana.dias(estado.semana) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onVolver != null) {
                IconButton(onClick = onVolver) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_left),
                        contentDescription = "Volver"
                    )
                }
            }
            Column {
                Text(
                    text = "Horarios",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Disponibilidad semanal de salas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(0.8f)) {
                Text(
                    text = "Sala",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Box {
                    OutlinedButton(
                        onClick = { desplegarSalas = true },
                        enabled = estado.salas.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = sala?.codigo ?: "Sin salas",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.down),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = desplegarSalas,
                        onDismissRequest = { desplegarSalas = false }
                    ) {
                        estado.salas.forEach { opcion ->
                            val sufijo = if (opcion.activa) "" else " (inactiva)"
                            DropdownMenuItem(
                                text = { Text("${opcion.codigo} · ${opcion.nombre}$sufijo") },
                                onClick = {
                                    viewModel.onSalaChange(opcion.id)
                                    desplegarSalas = false
                                }
                            )
                        }
                    }
                }
            }

            if (puedeEditar) {
                OutlinedButton(
                    onClick = { if (sala != null) onNuevoHorario(sala.id, null, -1) },
                    enabled = sala != null,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Text(
                        text = "Definir horario de atención",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        SelectorSemana(
            rango = FechasSemana.rango(dias),
            esActual = estado.semana == 0,
            onAnterior = { viewModel.cambiarSemana(-1) },
            onSiguiente = { viewModel.cambiarSemana(1) },
            onHoy = viewModel::irAHoy
        )
        Spacer(modifier = Modifier.height(12.dp))

        Leyenda()
        Spacer(modifier = Modifier.height(12.dp))

        if (sala == null) {
            Text(
                text = "Aún no hay salas. Créalas primero en la sección Salas.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666)
            )
        } else {
            GrillaSemanal(
                horarios = estado.horarios,
                ocupaciones = estado.ocupaciones,
                dias = dias,
                onEditar = { id -> if (puedeEditar) onEditarHorario(id) },
                onNuevo = { dia, inicioMin ->
                    if (puedeEditar) onNuevoHorario(sala.id, dia, inicioMin)
                },
                onOcupacion = { ocupacion, dia -> detalle = ocupacion to dia },
                puedeEditar = puedeEditar,
                onSolicitar = { bloque, indiceDia ->
                    val diaElegido = dias[indiceDia]
                    val fecha = FechasSemana.ddMM(diaElegido)
                    val bloqueOcupado = estado.ocupaciones.any {
                        it.fecha == fecha &&
                                it.inicioMin < bloque.finMin &&
                                it.finMin > bloque.inicioMin
                    }
                    when {
                        FechasSemana.esPasado(diaElegido) ->
                            mensaje = "Ese día ya pasó. Elige una fecha desde hoy."
                        bloqueOcupado ->
                            mensaje = "Este bloque ya tiene una actividad. Elige otro."
                        else -> solicitud = SolicitudPendiente(
                            salaId = sala.id,
                            codigoSala = sala.codigo,
                            nombreSala = sala.nombre,
                            fecha = fecha,
                            dia = DiaSemana.entries[indiceDia],
                            inicioMin = bloque.inicioMin,
                            finMin = bloque.finMin
                        )
                    }
                }
            )
            Text(
                text = if (puedeEditar) {
                    "Toca una celda libre para editar su bloque, una bloqueada para definir uno nuevo o una ocupada para ver el detalle."
                } else {
                    "Toca una celda en blanco para solicitar ese bloque, o una ocupada para ver el detalle."
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666),
                modifier = Modifier.padding(top = 12.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

    solicitud?.let { pendiente ->
        AlertDialog(
            onDismissRequest = { solicitud = null },
            title = { Text("¿Solicitar este bloque?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Sala: ${pendiente.codigoSala} · ${pendiente.nombreSala}")
                    Text("Fecha: ${pendiente.dia.nombre} ${pendiente.fecha}")
                    Text("Horario: ${FormatoHora.rango(pendiente.inicioMin, pendiente.finMin)}")
                    Text(
                        text = "En el siguiente paso completarás los datos de la actividad.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    solicitud = null
                    onSolicitarBloque(
                        pendiente.salaId,
                        pendiente.fecha,
                        FormatoHora.minutos(pendiente.inicioMin),
                        FormatoHora.minutos(pendiente.finMin)
                    )
                }) { Text("Continuar") }
            },
            dismissButton = {
                TextButton(onClick = { solicitud = null }) { Text("Cancelar") }
            }
        )
    }

    mensaje?.let { texto ->
        AlertDialog(
            onDismissRequest = { mensaje = null },
            text = { Text(texto) },
            confirmButton = {
                TextButton(onClick = { mensaje = null }) { Text("Entendido") }
            }
        )
    }

    detalle?.let { (ocupacion, dia) ->
        AlertDialog(
            onDismissRequest = { detalle = null },
            title = { Text(ocupacion.titulo) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Tipo: ${nombreTipo(ocupacion.tipo)}")
                    Text("Fecha: ${dia.nombre} ${ocupacion.fecha}")
                    Text("Horario: ${FormatoHora.rango(ocupacion.inicioMin, ocupacion.finMin)}")
                    if (ocupacion.responsable.isNotBlank()) {
                        Text("Responsable: ${ocupacion.responsable}")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { detalle = null }) { Text("Cerrar") }
            }
        )
    }
}

//bloque que el trabajador toca y que espera su confirmación antes de abrir el formulario
private data class SolicitudPendiente(
    val salaId: Long,
    val codigoSala: String,
    val nombreSala: String,
    val fecha: String,
    val dia: DiaSemana,
    val inicioMin: Int,
    val finMin: Int
)

private fun nombreTipo(tipo: TipoOcupacion): String = when (tipo) {
    TipoOcupacion.RESERVADO -> "Reservado"
    TipoOcupacion.CLASE -> "Clase"
    TipoOcupacion.MONTAJE -> "Montaje"
}

@Composable
private fun SelectorSemana(
    rango: String,
    esActual: Boolean,
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit,
    onHoy: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onAnterior,
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(40.dp)
        ) {
            Text("‹", fontWeight = FontWeight.Bold)
        }
        Text(
            text = rango,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        OutlinedButton(
            onClick = onSiguiente,
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(40.dp)
        ) {
            Text("›", fontWeight = FontWeight.Bold)
        }
        TextButton(onClick = onHoy, enabled = !esActual) {
            Text("Hoy")
        }
    }
}

@Composable
private fun Leyenda() {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemLeyenda(ColorLibre, "Libre")
        ItemLeyenda(ColorReservado, "Reservado")
        ItemLeyenda(ColorClase, "Clase")
        ItemLeyenda(ColorMontaje, "Montaje")
        ItemLeyenda(ColorBloqueado, "Bloqueado")
    }
}

@Composable
private fun ItemLeyenda(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
                .border(1.dp, ColorBorde, RoundedCornerShape(3.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun GrillaSemanal(
    horarios: List<Horario>,
    ocupaciones: List<Ocupacion>,
    dias: List<Calendar>,
    onEditar: (Long) -> Unit,
    onNuevo: (DiaSemana, Int) -> Unit,
    onOcupacion: (Ocupacion, DiaSemana) -> Unit,
    puedeEditar: Boolean,
    onSolicitar: (Horario, Int) -> Unit
) {
    //una fila por hora, desde la hora mínima hasta la máxima que permite el formulario
    val horas = (ValidacionesHorario.HORA_MIN / 60) until (ValidacionesHorario.HORA_MAX / 60)

    Row(modifier = Modifier.fillMaxWidth()) {
        //columna fija con las horas
        Column(
            modifier = Modifier.width(AnchoHoras),
            verticalArrangement = Arrangement.spacedBy(SeparacionCeldas)
        ) {
            Spacer(modifier = Modifier.height(AltoCabecera))
            horas.forEach { hora ->
                Box(
                    modifier = Modifier.height(AltoCelda),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = FormatoHora.minutos(hora * 60),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF666666)
                    )
                }
            }
        }

        //días con desplazamiento horizontal
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(SeparacionCeldas)
        ) {
            DiaSemana.entries.forEachIndexed { indice, dia ->
                val fecha = FechasSemana.ddMM(dias[indice])

                Column(verticalArrangement = Arrangement.spacedBy(SeparacionCeldas)) {
                    Box(
                        modifier = Modifier
                            .width(AnchoCelda)
                            .height(AltoCabecera),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${dia.abreviatura} ${FechasSemana.numero(dias[indice])}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    horas.forEach { hora ->
                        val inicio = hora * 60
                        val fin = inicio + 60
                        //cualquier bloque de atención que toque esta hora activo o no
                        val bloque = horarios.firstOrNull {
                            it.dia == dia && it.inicioMin < fin && it.finMin > inicio
                        }
                        //franjas ocupadas de ese día que tocan esta hora
                        val ocupadas = ocupaciones
                            .filter {
                                it.fecha == fecha && it.inicioMin < fin && it.finMin > inicio
                            }
                            .sortedByDescending {
                                minOf(it.finMin, fin) - maxOf(it.inicioMin, inicio)
                            }
                        CeldaHorario(
                            bloque = bloque,
                            ocupadas = ocupadas,
                            inicioHora = inicio,
                            onClick = {
                                val principal = ocupadas.firstOrNull()
                                when {
                                    principal != null -> onOcupacion(principal, dia)
                                    puedeEditar && bloque != null -> onEditar(bloque.id)
                                    puedeEditar -> onNuevo(dia, inicio)
                                    bloque != null && bloque.activo -> onSolicitar(bloque, indice)
                                    else -> Unit
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CeldaHorario(
    bloque: Horario?,
    ocupadas: List<Ocupacion>,
    inicioHora: Int,
    onClick: () -> Unit
) {
    val principal = ocupadas.firstOrNull()
    //cruce real dos franjas que se pisan en el tiempo no basta con compartir la hora
    val hayCruce = ocupadas.any { a ->
        ocupadas.any { b -> a !== b && a.inicioMin < b.finMin && b.inicioMin < a.finMin }
    }
    val libre = bloque?.activo == true
    val forma = RoundedCornerShape(8.dp)

    val fondo = when (principal?.tipo) {
        TipoOcupacion.RESERVADO -> ColorReservado
        TipoOcupacion.CLASE -> ColorClase
        TipoOcupacion.MONTAJE -> ColorMontaje
        null -> if (libre) ColorLibre else ColorBloqueado
    }
    val colorTexto = if (principal?.tipo == TipoOcupacion.CLASE) Color.White else ColorTextoOscuro

    Box(
        modifier = Modifier
            .width(AnchoCelda)
            .height(AltoCelda)
            .clip(forma)
            .background(fondo)
            .border(
                1.dp,
                if (hayCruce) MaterialTheme.colorScheme.error else ColorBorde,
                forma
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (principal != null) {
            // El título se repite en cada celda de la franja, así ninguna queda sin identificar
            val texto = if (hayCruce) "Cruce" else principal.titulo
            if (texto.isNotEmpty()) {
                Text(
                    text = texto,
                    fontSize = 10.sp,
                    lineHeight = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = if (hayCruce) MaterialTheme.colorScheme.error else colorTexto,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        } else if (bloque != null && !bloque.activo) {
            Text(
                text = "Inactivo",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF666666)
            )
        } else if (libre) {
            Text(
                text = "Libre",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9E9E9E)
            )
        }
    }
}
