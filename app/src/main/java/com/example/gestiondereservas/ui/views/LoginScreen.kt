package com.example.gestiondereservas.ui.views

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border // >>> IMPORTACIÓN PARA BORDES IDÉNTICOS A OUTLINEDTEXTFIELD
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestiondereservas.R
import com.example.gestiondereservas.ui.theme.*
import com.example.gestiondereservas.ui.viewmodel.GestionReservasViewModel
import com.example.gestiondereservas.utils.AnimacionLogin
import com.example.gestiondereservas.utils.AnimacionLottie

//- - - - - - - - - - - - - - - - - - - - - - - - -
//Encabezado
//- - - - - - - - - - - - - - - - - - - - - - - - -


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTopBar() {
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
fun LoginNavBar() {
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
enum class RolUsuario(val nombreMostrar: String) {
    RELATOR("Relator / Trabajador"),
    COORDINADOR("Coordinador")
}

//- - - - - - - - - - - - - - - - - - - - - - - - -
//Cuerpo
//- - - - - - - - - - - - - - - - - - - - - - - - -

@Composable
fun LoginBody(
    modifier: Modifier = Modifier,
    estaCargando: Boolean = false,
    mensajeErrorGeneral: String? = null,
    usuariosRegistrados: Map<String, RolUsuario> = emptyMap(),
    passwordsRegistrados: Map<String, String> = emptyMap(),
    alHacerClicEnRegistrarse: () -> Unit = {},
    alIniciarSesionExitoso: (correo: String, password: String, rol: RolUsuario) -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var esPasswordVisible by remember { mutableStateOf(false) }
    var rolSeleccionado by remember { mutableStateOf(RolUsuario.RELATOR) }

    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorPassword by remember { mutableStateOf<String?>(null) }
    var errorMensajeRol by remember { mutableStateOf<String?>(null) }
    val administradorFoco = LocalFocusManager.current

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

    fun validarPassword(): Boolean {
        val correoLimpio = correo.trim().lowercase()
        val passEsperada = passwordsRegistrados[correoLimpio]

        return when {
            password.isBlank() -> {
                errorPassword = "La constraseña es obligatoria"
                false
            }
            password.length < 6 -> {
                errorPassword = "La contraseña debe tener al menos 6 caracteres"
                false
            }
            !password.any { it.isUpperCase() } -> {
                errorPassword = "Debe contener al menos una letra mayúscula"
                false
            }
            !password.any { it.isLowerCase() } -> {
                errorPassword = "Debe contener al menos una letra minúscula"
                false
            }
            !password.any { it.isDigit() } -> {
                errorPassword = "Debe contener al menos un número"
                false
            }
            passEsperada != null && passEsperada != password -> {
                errorPassword = "La contraseña no coincide con la registrada"
                false
            }
            else -> {
                errorPassword = null
                true
            }
        }
    }

    fun validarRolRegistrado(): Boolean {
        val correoLimpio = correo.trim().lowercase()
        val rolReal = usuariosRegistrados[correoLimpio]
        return if (rolReal != null && rolReal != rolSeleccionado) {
            errorMensajeRol = "Este correo está registrado como ${rolReal.nombreMostrar}. No puedes ingresar como ${rolSeleccionado.nombreMostrar}."
            false
        } else {
            errorMensajeRol = null
            true
        }
    }

    fun manejarEnvio() {
        val esCorreoValido = validarCorreo()
        val esPasswordValido = validarPassword()
        val esRolValido = validarRolRegistrado()

        if (esCorreoValido && esPasswordValido && esRolValido) {
            administradorFoco.clearFocus()
            alIniciarSesionExitoso(correo.trim(), password, rolSeleccionado)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        //BLOQUE SUPERIOR: Animación + título + descripción
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().widthIn(max = 440.dp)
        ) {
            AnimacionLogin(
                tamano = 65.dp
            )

            //Sombreado por palabra independiente ("Iniciar" y "sesión")
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(IntrinsicSize.Max)
                ) {
                    Text(
                        text = "Iniciar",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.01.dp)
                            .height(3.5.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFFFB800))
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(IntrinsicSize.Max)
                ) {
                    Text(
                        text = "sesión",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.01.dp)
                            .height(3.5.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFFFB800))
                    )
                }
            }

            Text(
                text = "Accede para gestionar y reservar infraestructura",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
            ) }


        //BLOQUE CENTRAL: Roles + campos de texto
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().widthIn(440.dp)
        ) {
            Text(
                text = "Seleccione su perfil:",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )

            // >>> CORRECCIÓN: Contenedores de perfiles idénticos a los OutlinedTextField
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RolUsuario.values().forEach { rol ->
                    val esSeleccionado = rolSeleccionado == rol
                    val formaRol = RoundedCornerShape(8.dp)

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Sombra amarilla (#FFB800) idéntica a los campos: offset (x = 3.dp, y = 3.dp)
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .offset(x = 3.dp, y = 3.dp)
                                .clip(formaRol)
                                .background(Color(0xFFFFB800))
                        )

                        // Contenedor frontal idéntico en límites: fondo blanco sólido y borde neutro
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(formaRol)
                                .background(
                                    if (esSeleccionado) Color(0xFFECE6F0) else Color.White
                                )
                                .border(
                                    BorderStroke(
                                        width = 1.dp,
                                        color = if (esSeleccionado) MaterialTheme.colorScheme.primary else Color(0xFF79747E)
                                    ),
                                    formaRol
                                )
                                .clickable {
                                    rolSeleccionado = rol
                                    if (errorMensajeRol != null) validarRolRegistrado()
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = rol.nombreMostrar,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (esSeleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (esSeleccionado) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
            // >>> FIN CORRECCIÓN ROLES

            AnimatedVisibility(
                visible = errorMensajeRol != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = errorMensajeRol.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            //Sombreado en marcador del correo institucional
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(x = 3.dp, y = 3.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFB800))
                )

                OutlinedTextField(
                    value = correo,
                    onValueChange = {
                        correo = it
                        if (errorCorreo != null) validarCorreo()
                        if (errorMensajeRol != null) validarRolRegistrado()
                        if (errorPassword != null) validarPassword()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(4.dp)),
                    label = { Text("Correo institucional") },
                    placeholder = { Text("usuario@duocuc.cl") },
                    singleLine = true,
                    maxLines = 1,
                    isError = errorCorreo != null || errorMensajeRol != null,
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
                        if (errorCorreo != null || errorMensajeRol != null) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error en correo",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }

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
                        .padding(start = 8.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            //Sombreado en marcador de contraseña
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(x = 3.dp, y = 3.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFB800))
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (errorPassword != null) validarPassword()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(4.dp)),
                    label = { Text("Contraseña") },
                    singleLine = true,
                    isError = errorPassword != null,
                    visualTransformation = if (esPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
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
            }

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
                        .padding(start = 8.dp, top = 2.dp)
                )
            }

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
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        //BLOQUE INFERIOR: Botones de acción (ingresar y registrarse)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().widthIn(max = 440.dp)
        ) {
            Button(
                onClick = { manejarEnvio() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                enabled = !estaCargando
            ) {
                if (estaCargando) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(22.dp)
                            .width(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Ingresar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            androidx.compose.material3.OutlinedButton(
                onClick = {
                    alHacerClicEnRegistrarse()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                enabled = !estaCargando
            ) {
                Text(
                    text = "Registrarse",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            } }

    }
}

val usuariosRegistradosMemoria = mutableMapOf<String, RolUsuario>()
val passwordsUsuariosMemoria = mutableMapOf<String, String>()

@Composable
fun LoginScreen(
    estaCargando: Boolean = false,
    mensajeErrorGeneral: String? = null,
    usuariosRegistrados: Map<String, RolUsuario> = usuariosRegistradosMemoria,
    passwordsRegistrados: Map<String, String> = passwordsUsuariosMemoria,
    alNavegarARegistro: () -> Unit = {},
    alNavegarARelator: () -> Unit = {},
    alNavegarASupervisor: () -> Unit = {},
    alIniciarSesionExitoso: (correo: String, password: String, rol: RolUsuario) -> Unit = {_, _, _ -> }
) {
    var pantallaDestinoInterna by remember { mutableStateOf<String?>(null) }

    when (pantallaDestinoInterna) {
        "relator" -> {
            RelatorScreen(
                onReservas = {},
                onBuscarSala = {},
                onCalendario = {},
                onActividad = {}
            )
            return
        }
        "supervisor" -> {
            val supervisorVm: GestionReservasViewModel = viewModel()
            SupervisorScreen(
                onHorarios = {},
                onGestionSalas = {},
                onAuditoria = {},
                onReportes = {},
                onReservas = {},
                viewModel = supervisorVm
            )
            return
        }
    }

    Scaffold(
        topBar = { LoginTopBar() },
        bottomBar = { LoginNavBar() }
    ) { paddingValues ->
        LoginBody(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            estaCargando = estaCargando,
            mensajeErrorGeneral = mensajeErrorGeneral,
            usuariosRegistrados = usuariosRegistrados,
            passwordsRegistrados = passwordsRegistrados,
            alHacerClicEnRegistrarse = alNavegarARegistro,
            alIniciarSesionExitoso = { correo, password, rol ->
                val correoLimpio = correo.trim().lowercase()

                if (!usuariosRegistradosMemoria.containsKey(correoLimpio)) {
                    usuariosRegistradosMemoria[correoLimpio] = rol
                }
                if (!passwordsUsuariosMemoria.containsKey(correoLimpio)) {
                    passwordsUsuariosMemoria[correoLimpio] = password
                }

                alIniciarSesionExitoso(correo, password, rol)

                when (rol) {
                    RolUsuario.RELATOR -> {
                        try {
                            alNavegarARelator()
                        } catch (e: Exception) {
                        }
                        pantallaDestinoInterna = "relator"
                    }
                    RolUsuario.COORDINADOR -> {
                        try {
                            alNavegarASupervisor()
                        } catch (e: Exception) {
                        }
                        pantallaDestinoInterna = "supervisor"
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginBodyPreview() {
    GestionDeReservasTheme {
        LoginScreen(
            alNavegarARelator = {},
            alNavegarASupervisor = {},
            alNavegarARegistro = {}
        )
    }
}