package com.example.gestiondereservas.ui.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gestiondereservas.R
import com.example.gestiondereservas.data.repository.SalaRepository
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import com.example.gestiondereservas.ui.components.AreaChip
import com.example.gestiondereservas.ui.viewmodel.SalasViewModel
import com.example.gestiondereservas.data.room.entidades.Area
import com.example.gestiondereservas.data.room.entidades.AuditoriaEntity
import com.example.gestiondereservas.data.room.entidades.TipoAuditoria
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun rememberSalasViewModel(areaCoordinador: Area): SalasViewModel {
    val contexto = LocalContext.current.applicationContext
    return viewModel(
        key = "salas-${areaCoordinador.name}",
        factory = viewModelFactory {
            initializer {
                val dao = AppDatabase.getInstance(contexto).salaDao()
                val repository = SalaRepository(dao)
                SalasViewModel(repository, areaCoordinador)
            }
        }
    )
}

@Composable
fun SalasScreen(
    areaCoordinador: Area = Area.INFORMATICA,
    onNuevaSala: () -> Unit = {},
    onEditarSala: (Long) -> Unit = {},
    onRegistrarAuditoria: (AuditoriaEntity) -> Unit,
    viewModel: SalasViewModel = rememberSalasViewModel(areaCoordinador)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    var salaEntityPorCambiar by remember { mutableStateOf<SalaEntity?>(null) }

    fun crearAuditoria(): AuditoriaEntity{
        return AuditoriaEntity(
            accion = "Se actualizaron los datos de la sala",
            detalle = "${salaEntityPorCambiar?.codigo} - ${salaEntityPorCambiar?.nombre} ",
            fecha = LocalDate.now().toString(),
            hora = LocalDateTime.now().toString(),
            tipo = TipoAuditoria.SALAS.name
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Salas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Gestiona los recintos de tu área",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF666666)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = estado.busqueda,
            onValueChange = viewModel::onBusquedaChange,
            placeholder = { Text("Buscar por código o nombre") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onNuevaSala,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+ Nueva sala", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (estado.salas.isEmpty()) {
            Text(
                text = "No hay salas que coincidan con tu búsqueda",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(estado.salas, key = { it.salaEntity.id }) { item ->
                    SalaCard(
                        salaEntity = item.salaEntity,
                        onClick = { onEditarSala(item.salaEntity.id) },
                        onToggle = { salaEntityPorCambiar = item.salaEntity }
                    )
                }
            }
        }
    }

    salaEntityPorCambiar?.let { sala ->
        val activar = !sala.activa
        AlertDialog(
            onDismissRequest = { salaEntityPorCambiar = null },
            title = {
                Text("¿${if (activar) "Activar" else "Desactivar"} la sala ${sala.codigo}?")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.alternarEstado(sala)
                    salaEntityPorCambiar = null
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { salaEntityPorCambiar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun SalaCard(
    salaEntity: SalaEntity,
    onClick: () -> Unit,
    onToggle: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (salaEntity.activa) 1f else 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEEEEEE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.door),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${salaEntity.codigo} · ${salaEntity.nombre}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Capacidad ${salaEntity.capacidad}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.height(6.dp))
                AreaChip(salaEntity.area)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (salaEntity.activa) "Activa" else "Inactiva",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (salaEntity.activa) Color(0xFF2E7D32) else Color(0xFF666666)
                )
                Switch(
                    checked = salaEntity.activa,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.tertiary,
                        checkedBorderColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            }
        }
    }
}
