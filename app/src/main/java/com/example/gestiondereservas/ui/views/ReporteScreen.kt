package com.example.gestiondereservas.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gestiondereservas.data.remote.RetrofitClient
import com.example.gestiondereservas.data.remote.dto.ReporteResponse
import com.example.gestiondereservas.data.repository.ReservaRepository
import com.example.gestiondereservas.data.repository.SalaRepository
import com.example.gestiondereservas.ui.theme.GestionDeReservasTheme
import com.example.gestiondereservas.viewmodel.ReportesViewModel
import kotlin.collections.map

@Composable
fun rememberReportesViewModel(): ReportesViewModel {
    return viewModel(
        factory = viewModelFactory {
            initializer {
                val reservaRepository = ReservaRepository(RetrofitClient.apiSalas, RetrofitClient.api,
                    RetrofitClient.apiHorarios)
                val salaRepository = SalaRepository(RetrofitClient.apiSalas)
                ReportesViewModel(reservaRepository, salaRepository)
            }
        }
    )
}

//Modelo de datos por cada elemento de la lista de ocupación (RF16)
data class ItemReporteSala(
    val codigoSala: String,
    val tipoEspacio: String,
    val totalReservas: Long,
    val horasOcupadas: Long,
    val porcentajeUso: Long
)

//Periodos académicos predefinidos para la auditoría y consolidación
enum class PeriodoAcademico(val etiqueta: String) {
    SEMANAL("Esta semana"),
    MENSUAL("Mes actual"),
    SEMESTRAL("Ultimos 6 meses")
}

//- - - - - - - - - - - - - - - - - - - - - - - - -
//Cuerpo
//- - - - - - - - - - - - - - - - - - - - - - - - -

@Composable
fun ReporteBody(
    modifier: Modifier = Modifier,
    reportesViewModel: ReportesViewModel = rememberReportesViewModel()
) {
    val salas by reportesViewModel.salas.collectAsState()
    val reporte by reportesViewModel.reporte.collectAsState()
    val reportesSala by reportesViewModel.reportesPorSala.collectAsState()

    //Estados de filtros locales
    var periodoSeleccionado by remember { mutableStateOf(PeriodoAcademico.MENSUAL) }

    LaunchedEffect(periodoSeleccionado) {
        val tiempo = when (periodoSeleccionado) {
            PeriodoAcademico.SEMANAL -> "Semanal"
            PeriodoAcademico.MENSUAL -> "Mensual"
            PeriodoAcademico.SEMESTRAL -> "Semestral"
        }
        reportesViewModel.generarReporteGeneral(tiempo)
        reportesViewModel.generarReportes(salas, tiempo)
    }

    //Datos simulados estructurados según las reglas de negocio de la institución
    val itemsReporte = remember(salas, periodoSeleccionado, reportesSala) {
        salas.map { sala ->
            val metricasSala = reportesSala[sala.salaId] ?: ReporteResponse()
            val porcentaje = if(reporte.horas > 0) (metricasSala.horas * 100) / reporte.horas else 0
            ItemReporteSala(sala.codigo, sala.nombre, metricasSala.cantidad, metricasSala.horas, porcentaje)
        }

    }


    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        //1. Título de descripción jerárquica
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Reporte de Ocupación",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
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
                            onClick = { periodoSeleccionado = periodo },
                            label = { Text(periodo.etiqueta) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.tertiary.copy(0.6f)
                            )
                        )
                    }
                }
            }
        }

        //3. Tarjetas de métricas globales (KPIs)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaMetrica(
                    titulo = "Total de Reservas",
                    valor = "${reporte.cantidad}",
                    icono = Icons.Default.DateRange,
                    modifier = Modifier.weight(1f)
                )
                TarjetaMetrica(
                    titulo = "Horas de Uso",
                    valor = "${reporte.horas} hrs",
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
        items(itemsReporte) { item ->
            TarjetaDetalleSala(item = item)
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
            containerColor = MaterialTheme.colorScheme.primary.copy(0.1f)
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
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
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
                color = MaterialTheme.colorScheme.tertiary.copy(0.4f)
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