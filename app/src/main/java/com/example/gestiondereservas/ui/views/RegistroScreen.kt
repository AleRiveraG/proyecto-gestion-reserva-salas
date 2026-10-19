package com.example.gestiondereservas.ui.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gestiondereservas.R
import com.example.gestiondereservas.ui.theme.GestionDeReservasTheme
import com.example.gestiondereservas.utils.AnimacionRegistro

//- - - - - - - - - - - - - - - - - - - - - - - - -
//Encabezado
//- - - - - - - - - - - - - - - - - - - - - - - - -

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroTopBar() {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.secondary
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo Duoc Uc",
                    modifier = Modifier.size(100.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Reserva", color = MaterialTheme.colorScheme.secondary)
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
                        .clickable {},
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
                        .clickable {},
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

//- - - - - - - - - - - - - - - - - - - - - - - - -
//Pie de página
//- - - - - - - - - - - - - - - - - - - - - - - - -

@Composable
fun RegistroNavBar() {
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
            label = { Text("Inicio") },
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
                    contentDescription = "Icono calendario"
                )
            },
            label = { Text("Calendario") },
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
                    contentDescription = "Icono puerta"
                )
            },
            label = { Text("Salas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.secondary,
                selectedTextColor = MaterialTheme.colorScheme.tertiary,
                indicatorColor = MaterialTheme.colorScheme.tertiary
            )
        )
    }
}

//= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =

//Enumeración para los roles exigidos en las reglas de negocio (RF01)
enum class RolRegistroUsuario(val nombreMostrar: String) {
    RELATOR("Relator / Trabajador"),
    COORDINADOR("Coordinador")
}

//- - - - - - - - - - - - - - - - - - - - - - - - -
//Cuerpo
//- - - - - - - - - - - - - - - - - - - - - - - - -

@Composable
fun RegistroBody(
    modifier: Modifier = Modifier,
    estaCargando: Boolean = false,
    mensajeErrorGeneral: String? = null,
    alRegistrarExitoso: (nombre: String, correo: String, password: String, rol: RolRegistroUsuario) -> Unit
) {
    //Estados locales del formulario de registro
    var nombreCompleto by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var esPasswordVisible by remember { mutableStateOf(false) }
    var esConfirmarPasswordVisible by remember { mutableStateOf(false) }
    var rolSeleccionado by remember { mutableStateOf(RolRegistroUsuario.RELATOR) }

    //Estados de error para validación por campo
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorPassword by remember { mutableStateOf<String?>(null) }
    var errorConfirmarPassword by remember { mutableStateOf<String?>(null) }

    val administradorFoco = LocalFocusManager.current
    val estadoDesplazamiento = rememberScrollState()

    //Funciones de validación desacopladas
    fun validarNombre(): Boolean {
        return when {
            nombreCompleto.isBlank() -> {
                errorNombre = "El nombre completo es obligatorio"
                false
            }
            nombreCompleto.trim().length < 3 -> {
                errorNombre = "El nombre debe tener al menos 3 caracteres"
                false
            }
            else -> {
                errorNombre = null
                true
            }
        }
    }

    fun validarCorreo(): Boolean {
        return when {
            correo.isBlank() -> {
                errorCorreo = "El correo institucional es obligatorio"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() -> {
                errorCorreo = "Formato de correo no válido"
                false
            }
            !correo.trim().endsWith("@duocuc.cl") && !correo.trim().endsWith("@therionlabs.cl") -> {
                errorCorreo = "Debe ser un correo institucional (@duocuc.cl o corporativo)"
                false
            }
            else -> {
                errorCorreo = null
                true
            }
        }
    }

    //Validación estricta y sincronizada de la contraseña
    fun validarPassword(): Boolean {
        return when {
            password.isBlank() -> {
                errorPassword = "La contraseña es obligatoria"
                false
            }
            password.length < 6 -> {
                errorPassword = "La contraseña debe tener al menos 6 caracteres"
                false
            }
            !password.any { it.isUpperCase() } -> {  //Obliga a tener al menos 1 mayúscula
                errorPassword = "Debe contener al menos una letra mayúscula"
                false
            }
            !password.any { it.isLowerCase() } -> {  //Obliga a tener al menos 1 minúscula
                errorPassword = "Debe contener al menos una letra minúscula"
                false
            }
            !password.any { it.isDigit() } -> {  //Obliga a tener al menos 1 número
                errorPassword = "Debe contener al menos un número"
                false
            }
            else -> {
                errorPassword = null
                true
            }
        }
    }

    fun validarConfirmarPassword(): Boolean {
        return when {
            confirmarPassword.isBlank() -> {
                errorConfirmarPassword = "Debe confirmar su contraseña"
                false
            }
            confirmarPassword != password -> {
                errorConfirmarPassword = "Las contraseñas no coinciden"
                false
            }
            else -> {
                errorConfirmarPassword = null
                true
            }
        }
    }

    fun manejarEnvio() {
        val esNombreValido = validarNombre()
        val esCorreoValido = validarCorreo()
        val esPasswordValido = validarPassword() //Aplica las reglas reforzadas
        val esConfirmacionValida = validarConfirmarPassword()

        if (esNombreValido && esCorreoValido && esPasswordValido && esConfirmacionValida) {
            administradorFoco.clearFocus()
            alRegistrarExitoso(nombreCompleto.trim(), correo.trim(), password, rolSeleccionado)
        }
    }

    //Contenedor principal con scroll vertical
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(estadoDesplazamiento)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        AnimacionRegistro(
            tamano = 70.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        //Título de la sección
        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Registrate para solicitar y coordinar recintos",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        //RF01: Selector de rol-perfil de usuario
        Text(
            text = "Seleccione su perfil operativo:",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RolRegistroUsuario.values().forEach { rol ->
                FilterChip(
                    selected = rolSeleccionado == rol,
                    onClick = { rolSeleccionado = rol },
                    label = { Text(rol.nombreMostrar) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //Campo: Nombre Completo
        OutlinedTextField(
            value = nombreCompleto,
            onValueChange = {
                nombreCompleto = it
                if (errorNombre != null) validarNombre()
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nombre completo") },
            placeholder = { Text("Ej. José Merino") },
            singleLine = true,
            maxLines = 1,
            isError = errorNombre != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { administradorFoco.moveFocus(FocusDirection.Down) }
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Icono de nombre"
                )
            },
            trailingIcon = {
                if (errorNombre != null) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error en nombre",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        AnimatedVisibility(
            visible = errorNombre != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = errorNombre.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        //Campo: Correo institucional
        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                if (errorCorreo != null) validarCorreo()
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Correo institucional") },
            placeholder = { Text("usuario@duocuc.cl") },
            singleLine = true,
            isError = errorCorreo != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { administradorFoco.moveFocus(FocusDirection.Down) }
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Ícono de correo electrónico"
                )
            },
            trailingIcon = {
                if (errorCorreo != null) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error en correo",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        AnimatedVisibility(
            visible = errorCorreo != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = errorCorreo.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        //Campo: Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (errorPassword != null) validarPassword() //Revalida en tiempo real
                if (errorConfirmarPassword != null && confirmarPassword.isNotEmpty()) validarConfirmarPassword()
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Contraseña") },
            singleLine = true,
            isError = errorPassword != null,
            visualTransformation = if (esPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { administradorFoco.moveFocus(FocusDirection.Down) }
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Ícono de contraseña"
                )
            },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (errorPassword != null) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error en contraseña",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                    IconButton(onClick = { esPasswordVisible = !esPasswordVisible }) {
                        Icon(
                            imageVector = if (esPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (esPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            }
        )

        AnimatedVisibility(
            visible = errorPassword != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = errorPassword.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        //Campo: Confirmar Contraseña
        OutlinedTextField(
            value = confirmarPassword,
            onValueChange = {
                confirmarPassword = it
                if (errorConfirmarPassword != null) validarConfirmarPassword()
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Confirmar contraseña") },
            singleLine = true,
            isError = errorConfirmarPassword != null,
            visualTransformation = if (esConfirmarPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { manejarEnvio() }
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Ícono de confirmación de contraseña"
                )
            },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (errorConfirmarPassword != null) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error en confirmación de contraseña",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                    IconButton(onClick = { esConfirmarPasswordVisible = !esConfirmarPasswordVisible }) {
                        Icon(
                            imageVector = if (esConfirmarPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (esConfirmarPasswordVisible) "Ocultar confirmación de contraseña" else "Mostrar confirmación de contraseña"
                        )
                    }
                }
            }
        )

        AnimatedVisibility(
            visible = errorConfirmarPassword != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = errorConfirmarPassword.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 4.dp)
            )
        }

        //Mensaje de error general
        AnimatedVisibility(
            visible = mensajeErrorGeneral != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = mensajeErrorGeneral.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        //Botón de acción principal
        Button(
            onClick = { manejarEnvio() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !estaCargando
        ) {
            if (estaCargando) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(24.dp)
                        .width(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Registrarse",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

//Pantalla completa que une RegistroTopBar, RegistroNavBar y RegistroBody
@Composable
fun RegistroScreen(
    estaCargando: Boolean = false,
    mensajeErrorGeneral: String? = null,
    alRegistrarExitoso: (nombre: String, correo: String, password: String, rol: RolRegistroUsuario) -> Unit
) {
    Scaffold(
        topBar = { RegistroTopBar() },
        bottomBar = { RegistroNavBar() }
    ) { paddingValues ->
        RegistroBody(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            estaCargando = estaCargando,
            mensajeErrorGeneral = mensajeErrorGeneral,
            alRegistrarExitoso = alRegistrarExitoso
        )
    }
}

//Preview local con el tema de la aplicación aplicado
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegistroBodyPreview() {
    GestionDeReservasTheme {
        RegistroScreen(
            alRegistrarExitoso = { nombre, correo, password, rol ->
                //Acción de prueba local
            }
        )
    }
}
