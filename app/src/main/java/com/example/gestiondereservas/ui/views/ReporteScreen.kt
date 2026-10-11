package com.example.gestiondereservas.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

//Modelo de datos representativo para un elemento del reporte consolidado (RF16)
data class ItemReporteSala (
    val codigoSala: String,
    val tipoEspacio: String,
    val totalReservas: Int,
    val horasOcupadas: Int,
    val porcentajeUso: Int
)

//Periodos académicos predefinidos para la auditoría y consolidación
enum class PeriodoAcademico (val etiqueta: String) {
    SEMANAL("Esta semana"),
    MENSUAL("Mes actual"),
    SEMESTRAL("Semestre 2026-1")
}

/*
Componente Body para ReporteScreen:
Diseñado bajo Material 3 con métricas jerárquicas, tarjetas informativas,
filtros dinámicos y opciones de exportación (RF16).

@param modifier --> Modificador de diseño.
@param estaExportando --> Estado que indica si se está generando el archivo de exportación.
@param alExportarPlanilla --> Callback disparado para generar o descargar el reporte consolidado.
*/

@Composable
fun ReporteBody (
    modifier: Modifier = Modifier,
    estaExportando: Boolean = false,
    alExportarPlanilla: (periodo: PeriodoAcademico) -> Unit = {}
) {
    //Estados de filtros locales
    var periodoSeleccionado by remember { mutableStateOf(PeriodoAcademico.SEMESTRAL) }

    //Datos simulados estructurados según las reglas de negocio de la institución
    val itemsReporte = remember(periodoSeleccionado) {
        listOf(
            ItemReporteSala("SALA-101", "Sala de Clases", 24, 72, 85),
            ItemReporteSala("LAB-COM-02", "Laboratorio de Computación", 18, 54, 70),
            ItemReporteSala("AUD-MAGNO", "Espacio Multipropósito", 12, 36, 60),
            ItemReporteSala("SALA-204", "Sala de Clases", 15, 45, 65)
            )
    }

    LazyColumn(
        modifier = Modifier
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
                            label = { Text(periodo.etiqueta) }
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
                TarjetaMetrica (
                    titulo = "Total Reservas",
                    valor = "62",
                    icono = Icons.Default.DateRange,
                    modifier = Modifier.weight(1f)
                )
                TarjetaMetrica (
                    titulo = "Horas de Uso",
                    valor = "207 hrs",
                    icono = Icons.Default.MeetingRoom,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        //4. Acción de exportación (RF16)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Exportación consolidada",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Genera una plantilla formal (.xlsx) con la programación académica y ocupación por sala.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = { alExportarPlanilla(periodoSeleccionado) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = !estaExportando
                    ) {
                        if (estaExportando) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .height(20.dp)
                                    .width(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Generando reporte...")
                        } else {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Descargar"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Descargar Planilla (.xlsx)")
                        }
                    }
                }
            }
        }

        //5. Encabezado de detalle por sala
        item {
            Text(
                text = "Detalle por recinto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        //6. Listado dinámico de salas
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

//Preview local
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReporteBodyPreview() {
    ReporteBody(
        estaExportando = false,
        alExportarPlanilla = {}
    )
}
