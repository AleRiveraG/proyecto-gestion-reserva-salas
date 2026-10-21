package com.example.gestiondereservas.ui.views

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gestiondereservas.R
import com.example.gestiondereservas.data.repository.HorarioRepository
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.AuditoriaEntity
import com.example.gestiondereservas.data.room.entidades.DiaSemana
import com.example.gestiondereservas.data.room.entidades.TipoAuditoria
import com.example.gestiondereservas.ui.components.AreaChip
import com.example.gestiondereservas.ui.viewmodel.FormularioHorarioViewModel
import com.example.gestiondereservas.utils.FormatoHora
import com.example.gestiondereservas.utils.ValidacionesHorario
import com.example.gestiondereservas.utils.formatearHora
import java.time.LocalDate
import com.example.gestiondereservas.utils.ConfirmacionHorarioGuardado
import kotlinx.coroutines.delay

@Composable
fun rememberFormularioHorarioViewModel(
    horarioId: Long,
    salaInicialId: Long = 0L,
    diaInicial: String? = null,
    inicioInicialMin: Int = -1
): FormularioHorarioViewModel {
    val contexto = LocalContext.current.applicationContext
    return viewModel(
        key = "formulario-horario-$horarioId",
        factory = viewModelFactory {
            initializer {
                val horarioDao = AppDatabase.getInstance(contexto).horarioDao()
                val salaDao = AppDatabase.getInstance(contexto).salaDao()
                val reservaDao = AppDatabase.getInstance(contexto).reservaDao()
                val usoSalaDao = AppDatabase.getInstance(contexto).usoSalaDao()
                val repository = HorarioRepository(horarioDao, salaDao, reservaDao, usoSalaDao)
                FormularioHorarioViewModel(repository,
                    horarioId,
                    salaInicialId,
                    diaInicial?.let { nombre ->
                        runCatching { DiaSemana.valueOf(nombre) }.getOrNull()
                    },
                    inicioInicialMin
                )
            }
        }
    )
}

@Composable
fun FormularioHorarioScreen(
    horarioId: Long,
    onVolver: () -> Unit,
    salaInicialId: Long = 0L,
    diaInicial: String? = null,
    inicioInicialMin: Int = -1,
    viewModel: FormularioHorarioViewModel = rememberFormularioHorarioViewModel(
        horarioId,
        salaInicialId,
        diaInicial,
        inicioInicialMin
    ),
    onRegistrarAuditoria: (AuditoriaEntity) -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val contexto = LocalContext.current
    var confirmarDescarte by remember { mutableStateOf(false) }
    var confirmarEliminar by remember { mutableStateOf(false) }
    var desplegarSalas by remember { mutableStateOf(false) }
    val d = estado.datos
    val salaSeleccionada = estado.salas.firstOrNull { it.id == d.salaId }

    fun intentarVolver() {
        if (estado.hayCambios) confirmarDescarte = true else onVolver()
    }

    fun crearAuditoria(accion: String): AuditoriaEntity{
        return AuditoriaEntity(
            accion = accion,
            detalle = "${d.dia} - ${d.salaId} " ,
            fecha = LocalDate.now().toString(),
            hora = formatearHora(),
            tipo = TipoAuditoria.HORARIOS.name
        )
    }

    BackHandler { intentarVolver() }

    LaunchedEffect(estado.guardado) {
        if (estado.guardado) {
            onRegistrarAuditoria(crearAuditoria("Se creo un horario"))
            delay(2400)
            onVolver()
        }
    }

    if (estado.guardado) {
        ConfirmacionHorarioGuardado(esNuevo = estado.esNuevo)
    }

    LaunchedEffect(estado.eliminado) {
        if (estado.eliminado) {
            Toast.makeText(contexto, "Horario eliminado", Toast.LENGTH_SHORT).show()
            val auditoria = crearAuditoria("Se elimino un horario")
            onRegistrarAuditoria(auditoria)
            onVolver()
        }
    }


    if (estado.cargando) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.tertiary)
        }
        return
    }

    estado.errorAcceso?.let { error ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(error, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onVolver) {
                Text("Volver a horarios")
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (estado.esNuevo) "Nuevo horario" else "Modificar horario",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (estado.esNuevo) "Define un nuevo bloque horario para una sala"
            else "Actualiza el día o la hora del bloque",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF666666)
        )
        Spacer(Modifier.height(16.dp))

        //encabezado oscuro con el resumen del bloque
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF3B3B3B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.clock),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${d.dia.nombre} · ${FormatoHora.rango(d.inicioMin, d.finMin)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    if (salaSeleccionada != null) {
                        Text(
                            text = "Sala ${salaSeleccionada.codigo}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(4.dp))
                        AreaChip(salaSeleccionada.area)
                    } else {
                        Text(
                            text = "Sin sala seleccionada",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // Sala
        Etiqueta("Sala")
        if (estado.salas.isEmpty()) {
            Text(
                text = "No hay salas activas. Crea o activa una sala primero.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        } else {
            Box {
                OutlinedButton(
                    onClick = { desplegarSalas = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = salaSeleccionada?.let { "${it.codigo} · ${it.nombre}" }
                            ?: "Selecciona una sala",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text("Cambiar")
                }

                DropdownMenu(
                    expanded = desplegarSalas,
                    onDismissRequest = { desplegarSalas = false }
                ) {
                    estado.salas.forEach { sala ->
                        DropdownMenuItem(
                            text = { Text("${sala.codigo} · ${sala.nombre}") },
                            onClick = {
                                viewModel.onSalaChange(sala.id)
                                desplegarSalas = false
                            }
                        )
                    }
                }
            }
        }
        estado.errorSala?.let { MensajeError(it) }
        Spacer(Modifier.height(16.dp))

        // Día
        Etiqueta("Día de la semana")
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DiaSemana.entries.forEach { dia ->
                DiaOpcion(
                    texto = dia.nombre,
                    seleccionado = d.dia == dia,
                    onClick = { viewModel.onDiaChange(dia) }
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        // Horas
        SelectorHora(
            etiqueta = "Hora de inicio",
            minutos = d.inicioMin,
            onCambiar = viewModel::cambiarInicio
        )
        Spacer(Modifier.height(12.dp))
        SelectorHora(
            etiqueta = "Hora de término",
            minutos = d.finMin,
            onCambiar = viewModel::cambiarFin
        )
        estado.errorHorario?.let { MensajeError(it) }
        Text(
            text = "Los bloques de una misma sala no pueden cruzarse y deben dejar al menos ${ValidacionesHorario.INTERVALO_MIN} minutos entre uno y otro.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF666666),
            modifier = Modifier.padding(top = 8.dp)
        )
        if (!estado.esNuevo) {
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bloque activo",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Si lo desactivas, la sala queda bloqueada en ese horario.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                }
                Switch(
                    checked = d.activo,
                    onCheckedChange = viewModel::onActivoChange
                )
            }
        }
        Spacer(Modifier.height(24.dp))

        estado.errorGeneral?.let {
            MensajeError(it)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = viewModel::guardar,
            enabled = estado.hayCambios && !estado.guardando,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = Color(0xFFEEEEEE),
                disabledContentColor = Color(0xFF666666)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = if (estado.esNuevo) "Crear horario" else "Modificar",
                fontWeight = FontWeight.Bold
            )
        }
        if (estado.hayCambios) {
            TextButton(
                onClick = { intentarVolver() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
        if (!estado.esNuevo) {
            TextButton(
                onClick = { confirmarEliminar = true },
                enabled = !estado.guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Eliminar horario", color = MaterialTheme.colorScheme.error)
            }
        }
        Spacer(Modifier.height(24.dp))
    }

    if (confirmarDescarte) {
        AlertDialog(
            onDismissRequest = { confirmarDescarte = false },
            title = { Text("¿Descartar cambios?") },
            text = { Text("Tienes cambios sin guardar.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarDescarte = false
                    onVolver()
                }) {
                    Text("Descartar")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarDescarte = false }) {
                    Text("Seguir editando")
                }
            }
        )
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("¿Eliminar este horario?") },
            text = { Text("El bloque dejará de estar disponible para reservas.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    viewModel.eliminar()
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun Etiqueta(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun MensajeError(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun DiaOpcion(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (seleccionado) Color(0xFFFFF3CC)
        else MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            1.dp,
            if (seleccionado) MaterialTheme.colorScheme.tertiary
            else Color(0xFFDDDDDD)
        )
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun BotonPaso(texto: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.size(44.dp)
    ) {
        Text(texto, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SelectorHora(
    etiqueta: String,
    minutos: Int,
    onCambiar: (Int) -> Unit
) {
    Column {
        Etiqueta(etiqueta)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BotonPaso("−1h") { onCambiar(-60) }
            BotonPaso("−${ValidacionesHorario.PASO}") { onCambiar(-ValidacionesHorario.PASO) }
            Text(
                text = FormatoHora.minutos(minutos),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(64.dp)
            )
            BotonPaso("+${ValidacionesHorario.PASO}") { onCambiar(ValidacionesHorario.PASO) }
            BotonPaso("+1h") { onCambiar(60) }
        }
    }
}
