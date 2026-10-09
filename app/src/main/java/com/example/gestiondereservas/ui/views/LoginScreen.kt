package com.example.gestiondereservas.ui.views

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

//Enumeración para los roles exigidos en las reglas de negocio (RF01)
enum class RolUsuario(val nombreMostrar: String) {
    RELATOR("Relator / Trabajador"),
    COORDINADOR("Coordinador")
}

//Componente 'Body' para LoginScreen
@Composable
fun LoginBody(
    modifier: Modifier = Modifier,
    estaCargando: Boolean = false,
    mensajeErrorGeneral: String? = null,
    alIniciarSesionExitoso: (correo: String, password: String, rol: RolUsuario) -> Unit
) {
    //Estados locales del formulario
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var esPasswordVisible by remember { mutableStateOf(false) }
    var rolSeleccionado by remember { mutableStateOf(RolUsuario.RELATOR) }

    //Estados de error para validación por campo
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorPassword by remember { mutableStateOf<String?>(null) }
    val administradorFoco = LocalFocusManager.current
    val estadoDesplazamiento = rememberScrollState()

    //Funciones de validación desacopladas
    fun validarCorreo(): Boolean {
        return when {
            correo.isBlank() -> {
                errorCorreo = "El correo institucional es obligatorio"
                false
            }

            //Validación de formato de correo general
            !android.util.Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() -> {
                errorCorreo = "Formato de correo no válido"
                false
            }

            //RF01: Requisito de correo institucional
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
        return when {
            password.isBlank() -> {
                errorPassword = "La constraseña es obligatoria"
                false
            }
            password.length < 6 -> {
                errorPassword = "La contraseña debe tener al menos 6 caracteres"
                false
            }
            else -> {
                errorPassword = null
                true
            }
        }
    }

    fun manejarEnvio() {
        val esCorreoValido = validarCorreo()
        val esPasswordValido = validarPassword()

        if (esCorreoValido && esPasswordValido) {
            administradorFoco.clearFocus()
            alIniciarSesionExitoso(correo.trim(), password, rolSeleccionado)
        }
    }

    //Contenedor principal con scroll vertical
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(estadoDesplazamiento)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        //Título de la sección
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Accede para gestionar y reservar infraestructura",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        //RF01: Selector de rol-perfil de usuario
        Text(
            text = "Seleccione su perfil:",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RolUsuario.values().forEach { rol ->
                FilterChip(
                    selected = rolSeleccionado == rol,
                    onClick = { rolSeleccionado = rol },
                    label = { Text(rol.nombreMostrar) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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

        //Retroalimentación visual animada del error de correo
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

        Spacer(modifier = Modifier.height(16.dp))

        //Campo: Contraseña con toggle de visibilidad
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (errorPassword != null) validarPassword()
            },
            modifier = Modifier.fillMaxWidth(),
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

                    IconButton(onClick = { esPasswordVisible = !esPasswordVisible}) {
                        Icon(
                            imageVector = if (esPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (esPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            }
        )

        //Retroalimentación visual animada del error de contraseña
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

        //Mensaje de error general (autenticación fallida)
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
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

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
                    text = "Ingresar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginBodyPreview() {
    LoginBody(
        alIniciarSesionExitoso = { correo, contrasena, rol ->
            //Acción de prueba local
        }
    )
}
