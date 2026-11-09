package com.example.gestiondereservas.ui.views

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestiondereservas.ui.theme.GestionDeReservasTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


//Modelo de datos por cada elemento de la lista de ocupación (RF16)
data class ItemReporteSala(
    val codigoSala: String,
    val tipoEspacio: String,
    val totalReservas: Int,
    val horasOcupadas: Int,
    val porcentajeUso: Int
)

//Periodos académicos predefinidos para la auditoría y consolidación
enum class PeriodoAcademico(val etiqueta: String) {
    SEMANAL("Esta semana"),
    MENSUAL("Mes actual"),
    SEMESTRAL("Semestre 2026-1")
}

//Interfaz sellada para Estados de UI (Cargando, Exito, Error)
sealed interface ReporteUiState {
    data object Cargando : ReporteUiState
    data class Exito(val datos: List<ItemReporteSala>) : ReporteUiState
    data class Error(val mensaje: String) : ReporteUiState
}

//ViewModel con StateFlow, Corrutinas y cancelación reactiva
class ReporteViewModel : ViewModel() {
    private val _estado = MutableStateFlow<ReporteUiState>(ReporteUiState.Cargando)
    val estado: StateFlow<ReporteUiState> = _estado.asStateFlow()

    private var periodoActual = PeriodoAcademico.MENSUAL

    init {
        cargarReportes(PeriodoAcademico.MENSUAL)
    }

    fun cambiarPeriodo(nuevoPeriodo: PeriodoAcademico) {
        periodoActual = nuevoPeriodo
        cargarReportes(nuevoPeriodo)
    }

    fun reintentar() {
        cargarReportes(periodoActual)
    }

    private fun cargarReportes(periodo: PeriodoAcademico) {
        _estado.value = ReporteUiState.Cargando
        kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {  //Simulación asíncrona mediante corrutina en hilo Dispatchers.IO (idéntico a llamar API/Room)
            try {
                delay(300) // Simula latencia de red/base de datos
                val datos = when (periodo) {
                    PeriodoAcademico.SEMANAL -> listOf(
                        ItemReporteSala("A105", "Sala de Conferencias", 6, 18, 23),
                        ItemReporteSala("C204", "Laboratorio Multimedia", 12, 48, 61),
                        ItemReporteSala("C205", "Laboratorio Desarrollo", 14, 56, 71),
                        ItemReporteSala("D302", "Taller de Diseño", 10, 40, 51),
                        ItemReporteSala("G104", "Aula Gastronómica", 8, 32, 41)
                    )
                    PeriodoAcademico.MENSUAL -> listOf(
                        ItemReporteSala("A105", "Sala de Conferencias", 22, 66, 21),
                        ItemReporteSala("C204", "Laboratorio Multimedia", 46, 184, 59),
                        ItemReporteSala("C205", "Laboratorio Desarrollo", 52, 208, 66),
                        ItemReporteSala("D302", "Taller de Diseño", 40, 160, 51),
                        ItemReporteSala("F101", "Estudio Fotográfico", 28, 112, 36),
                        ItemReporteSala("G104", "Aula Gastronómica", 34, 136, 43),
                        ItemReporteSala("S210", "Sala Clínica", 38, 152, 48),
                        ItemReporteSala("T305", "Sala Turismo", 30, 120, 38)
                    )
                    PeriodoAcademico.SEMESTRAL -> listOf(
                        ItemReporteSala("A105", "Sala de Conferencias", 95, 285, 20),
                        ItemReporteSala("C204", "Laboratorio Multimedia", 210, 840, 60),
                        ItemReporteSala("C205", "Laboratorio Desarrollo", 235, 940, 67),
                        ItemReporteSala("D302", "Taller de Diseño", 180, 720, 51),
                        ItemReporteSala("F101", "Estudio Fotográfico", 125, 500, 35),
                        ItemReporteSala("G104", "Aula Gastronómica", 155, 620, 44),
                        ItemReporteSala("S210", "Sala Clínica", 170, 680, 48),
                        ItemReporteSala("T305", "Sala Turismo", 130, 520, 37)
                    )
                }
                _estado.value = ReporteUiState.Exito(datos)
            } catch (e: Exception) {
                _estado.value = ReporteUiState.Error("No se pudieron cargar los datos de ocupación.")
            }
        }
    }
}

//Utilidades para 'Intents' de compartir y enviar por correo (Intent.ACTION_SENDTO)
private fun enviarReportePorEmail(context: Context, periodo: PeriodoAcademico, totalReservas: Int, horasUso: Int) {
    val asunto = "Reporte de Ocupación - Sede Viña del Mar (${periodo.etiqueta})"
    val cuerpo = """
        Estimado/a Coordinador/a:
        
        Se adjunta el consolidado de infraestructura para el período: ${periodo.etiqueta}.
        - Total de Reservas Agendadas: $totalReservas
        - Horas Totales de Uso: $horasUso hrs
        
        Sistema de Gestión de Reservas Duoc UC.
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // Exige exclusivamente clientes de correo
        putExtra(Intent.EXTRA_SUBJECT, asunto)
        putExtra(Intent.EXTRA_TEXT, cuerpo)
    }
    context.startActivity(Intent.createChooser(intent, "Enviar reporte vía email"))
}

private fun compartirReporteGeneral(context: Context, periodo: PeriodoAcademico, totalReservas: Int, horasUso: Int) {
    val texto = "Reporte de Ocupación (${periodo.etiqueta}): $totalReservas reservas y $horasUso horas de uso registradas."
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, texto)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir resumen con..."))
}


//- - - - - - - - - - - - - - - - - - - - - - - - -
//Cuerpo
//- - - - - - - - - - - - - - - - - - - - - - - - -

@Composable
fun ReporteBody(
    modifier: Modifier = Modifier,
    viewModel: ReporteViewModel = viewModel()  //Inyección del ViewModel
) {
    val context = LocalContext.current
    var periodoSeleccionado by remember { mutableStateOf(PeriodoAcademico.MENSUAL) }

    //Observación del flujo reactivo de estados (StateFlow)
    var estadoUi by remember { mutableStateOf<ReporteUiState>(ReporteUiState.Cargando) }

    LaunchedEffect(viewModel) {
        viewModel.estado.collect { nuevoEstado ->
            estadoUi = nuevoEstado
        }
    }

    //Extrae los datos si el estado es Exito, o lista vacía mientras carga o hay error
    val itemsReporte = when (val estado = estadoUi) {
        is ReporteUiState.Exito -> estado.datos
        else -> emptyList()
    }

    val totalReservasCalculado = itemsReporte.sumOf { it.totalReservas }
    val horasUsoCalculadas = itemsReporte.sumOf { it.horasOcupadas }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        //1. Título de descripción jerárquica con acciones de la Unidad 3
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reporte de Ocupación",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    //Botones de acción (Compartir y Enviar por Correo)
                    Row {
                        IconButton(onClick = {
                            compartirReporteGeneral(context, periodoSeleccionado, totalReservasCalculado, horasUsoCalculadas)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Compartir reporte",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = {
                            enviarReportePorEmail(context, periodoSeleccionado, totalReservasCalculado, horasUsoCalculadas)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Enviar por correo",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Text(
                    text = "Consolidado de infraestructura, horas utilizadas y planificación.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        //2. Filtros de periodo (RF16)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtro",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = "Periodo de consulta:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PeriodoAcademico.values().forEach { periodo ->
                        FilterChip(
                            selected = periodoSeleccionado == periodo,
                            onClick = {
                                periodoSeleccionado = periodo
                                viewModel.cambiarPeriodo(periodo) // >>> U3: Notificación al ViewModel
                            },
                            label = { Text(periodo.etiqueta) }
                        )
                    }
                }
            }
        }

        //Renderizado condicional según UiState (Cargando / Error / Exito)
        when (val estado = estadoUi) {
            is ReporteUiState.Cargando -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            is ReporteUiState.Error -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = estado.mensaje,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            OutlinedButton(onClick = { viewModel.reintentar() }) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
            is ReporteUiState.Exito -> {
                //3. Tarjetas de métricas globales (KPIs)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TarjetaMetrica(
                            titulo = "Total de Reservas",
                            valor = "$totalReservasCalculado",
                            icono = Icons.Default.DateRange,
                            modifier = Modifier.weight(1f)
                        )
                        TarjetaMetrica(
                            titulo = "Horas de Uso",
                            valor = "$horasUsoCalculadas hrs",
                            icono = Icons.Default.MeetingRoom,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                //4. Encabezado de detalle por sala
                item {
                    Text(
                        text = "Detalle por recinto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                //5. Listado dinámico de salas
                items(
                    items = itemsReporte,
                    key = { it.codigoSala }  //Key para evitar recomposiciones innecesarias
                ) { item ->
                    TarjetaDetalleSala(item = item)
                }
            }
        }
    }
}

//Componente reutilizable para visualizar tarjetas de métricas cuantitativas clave
@Composable
fun TarjetaMetrica(
    titulo: String,
    valor: String,
    icono: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

//Tarjeta individual para mostrar el resumen de rendimiento y uso de una sala específica
@Composable
fun TarjetaDetalleSala(
    item: ItemReporteSala,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.codigoSala,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.tipoEspacio,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${item.totalReservas} reservas agendadas (${item.horasOcupadas} hrs)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = "Uso: ${item.porcentajeUso}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

//Pantalla completa que une TopBar, NavBar y ReporteBody
@Composable
fun ReporteScreen() {
    ReporteBody(
        modifier = Modifier.fillMaxSize()
    )
}

//Preview local
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReporteBodyPreview() {
    GestionDeReservasTheme {
        ReporteScreen()
    }
}