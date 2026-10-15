package com.example.gestiondereservas.ui.views

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gestiondereservas.R
import com.example.gestiondereservas.data.ProveedorDatos
import com.example.gestiondereservas.data.repository.SalaRepository
import com.example.gestiondereservas.data.room.entidades.Area
import com.example.gestiondereservas.ui.components.AreaChip
import com.example.gestiondereservas.ui.viewmodel.EquipoForm
import com.example.gestiondereservas.ui.viewmodel.FormularioSalaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun rememberFormularioSalaViewModel(
    salaId: Long
): FormularioSalaViewModel {
    val contexto = LocalContext.current.applicationContext
    return viewModel(
        key = "formulario-sala-$salaId",
        factory = viewModelFactory {
            initializer {
                FormularioSalaViewModel(
                    SalaRepository(ProveedorDatos.salaDao(contexto)),
                    salaId
                )
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FormularioSalaScreen(
    salaId: Long,
    onVolver: () -> Unit,
    viewModel: FormularioSalaViewModel = rememberFormularioSalaViewModel(
        salaId
    )
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val contexto = LocalContext.current
    var confirmarDescarte by remember { mutableStateOf(false) }
    var desplegarAreas by remember { mutableStateOf(false) }
    val d = estado.datos

    val selectorImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                contexto.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Algunos proveedores entregan permisos temporales que siguen siendo válidos.
            }
            viewModel.onImagenChange(uri.toString())
        }
    }

    fun intentarVolver() {
        if (estado.hayCambios) confirmarDescarte = true else onVolver()
    }

    BackHandler { intentarVolver() }

    LaunchedEffect(estado.guardada) {
        if (estado.guardada) {
            Toast.makeText(
                contexto,
                if (estado.esNueva) "Sala creada" else "Sala actualizada",
                Toast.LENGTH_SHORT
            ).show()
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
                Text("Volver a salas")
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
            text = if (estado.esNueva) "Nueva sala" else "Modificar Sala",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (estado.esNueva) "Completa los datos del nuevo recinto"
            else "Actualiza la información y disponibilidad",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF666666)
        )
        Spacer(Modifier.height(16.dp))

        // Encabezado oscuro con el resumen de la sala
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
                        .size(72.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF3B3B3B))
                        .clickable { selectorImagen.launch(arrayOf("image/*")) },
                    contentAlignment = Alignment.Center
                ) {
                    ImagenSala(uri = d.imagenUri)
                    Icon(
                        painter = painterResource(R.drawable.camera),
                        contentDescription = "Cambiar foto de la sala",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(4.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Sala ${d.codigo.ifBlank { "nueva" }}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    AreaChip(d.area)
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        CampoTexto(
            "Código de sala",
            d.codigo,
            viewModel::onCodigoChange,
            estado.errorCodigo
        )
        Spacer(Modifier.height(12.dp))
        CampoTexto(
            "Nombre de la sala",
            d.nombre,
            viewModel::onNombreChange,
            estado.errorNombre
        )
        Spacer(Modifier.height(12.dp))

        // Capacidad máxima
        Etiqueta("Capacidad máxima")
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(
                onClick = { viewModel.cambiarCapacidad(-1) },
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(44.dp)
            ) {
                Text("−", fontWeight = FontWeight.Bold)
            }
            Text(
                text = "${d.capacidad} personas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            OutlinedButton(
                onClick = { viewModel.cambiarCapacidad(1) },
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(44.dp)
            ) {
                Text("+", fontWeight = FontWeight.Bold)
            }
        }
        estado.errorCapacidad?.let { MensajeError(it) }
        Spacer(Modifier.height(12.dp))

        // Área de conocimiento
        Etiqueta("Área de conocimiento")
        Box {
            OutlinedButton(
                onClick = { desplegarAreas = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AreaChip(d.area)
                Spacer(Modifier.weight(1f))
                Text("Cambiar")
            }

            DropdownMenu(
                expanded = desplegarAreas,
                onDismissRequest = { desplegarAreas = false }
            ) {
                Area.values().forEach { area ->
                    DropdownMenuItem(
                        text = { Text(area.nombre) },
                        onClick = {
                            viewModel.onAreaChange(area)
                            desplegarAreas = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // Equipamiento
        Etiqueta("Equipamiento disponible")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            d.equipos.forEach { equipo ->
                EquipoOpcion(
                    equipo = equipo,
                    onAlternarSeleccion = {
                        viewModel.alternarEquipo(equipo.nombre)
                    },
                    onOcupadoChange = { ocupado ->
                        viewModel.cambiarEstadoEquipo(equipo.nombre, ocupado)
                    }
                )
            }
        }
        Text(
            text = "Toca un equipo para indicar si la sala cuenta con él. Marca Ocupado si no está disponible temporalmente.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF666666),
            modifier = Modifier.padding(top = 8.dp)
        )
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
                text = if (estado.esNueva) "Crear sala" else "Modificar",
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
}

@Composable
private fun ImagenSala(uri: String?) {
    val contexto = LocalContext.current
    val bitmap by produceState<Bitmap?>(initialValue = null, uri) {
        value = if (uri.isNullOrBlank()) null else withContext(Dispatchers.IO) {
            runCatching {
                val imagenUri = Uri.parse(uri)
                val opciones = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                contexto.contentResolver.openInputStream(imagenUri)?.use {
                    BitmapFactory.decodeStream(it, null, opciones)
                }
                var muestra = 1
                while (
                    opciones.outWidth / muestra > 1024 ||
                    opciones.outHeight / muestra > 1024
                ) {
                    muestra *= 2
                }
                val opcionesFinales = BitmapFactory.Options().apply {
                    inSampleSize = muestra
                }
                contexto.contentResolver.openInputStream(imagenUri)?.use {
                    BitmapFactory.decodeStream(it, null, opcionesFinales)
                }
            }.getOrNull()
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "Foto de la sala",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Icon(
            painter = painterResource(R.drawable.door),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
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
private fun CampoTexto(
    etiqueta: String,
    valor: String,
    onChange: (String) -> Unit,
    error: String?
) {
    Column {
        Etiqueta(etiqueta)
        OutlinedTextField(
            value = valor,
            onValueChange = onChange,
            singleLine = true,
            isError = error != null,
            supportingText = error?.let { { Text(it) } },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EquipoOpcion(
    equipo: EquipoForm,
    onAlternarSeleccion: () -> Unit,
    onOcupadoChange: (Boolean) -> Unit
) {
    Column(modifier = Modifier.width(156.dp)) {
        Surface(
            onClick = onAlternarSeleccion,
            shape = RoundedCornerShape(12.dp),
            color = if (equipo.seleccionado) Color(0xFFFFF3CC)
            else MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            border = BorderStroke(
                1.dp,
                if (equipo.seleccionado) MaterialTheme.colorScheme.tertiary
                else Color(0xFFDDDDDD)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(
                        if (equipo.seleccionado) R.drawable.check else R.drawable.door
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = equipo.nombre,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (equipo.seleccionado) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ocupado",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = equipo.ocupado,
                    onCheckedChange = onOcupadoChange
                )
            }
        }
    }
}
