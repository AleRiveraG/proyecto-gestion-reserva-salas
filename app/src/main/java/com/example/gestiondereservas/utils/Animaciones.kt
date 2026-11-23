package com.example.gestiondereservas.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.gestiondereservas.R

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * Función base reutilizable para renderizar cualquier animación JSON desde res/raw.
 */
@Composable
fun AnimacionLottie(
    resId: Int,
    modifier: Modifier = Modifier,
    tamano: Dp = 150.dp,
    iteraciones: Int = LottieConstants.IterateForever
) {
    // Carga la composición del JSON
    val composicion by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))

    // Renderiza la animación
    LottieAnimation(
        composition = composicion,
        iterations = iteraciones,
        modifier = modifier.size(tamano)
    )
}

//Animación específica para Login (apunta a res/raw/login.json).

@Composable
fun AnimacionLogin(
    modifier: Modifier = Modifier,
    tamano: Dp = 140.dp
) {
    AnimacionLottie(
        resId = R.raw.login,
        modifier = modifier,
        tamano = tamano
    )
}

//Animación específica para Registro (apunta a res/raw/registro.json)

@Composable
fun AnimacionRegistro(
    modifier: Modifier = Modifier,
    tamano: Dp = 140.dp
) {
    AnimacionLottie(
        resId = R.raw.registro,
        modifier = modifier,
        tamano = tamano
    )
}

//Animación para cuando se crea una sala correctamente
@Composable
fun ConfirmacionSalaGuardada(esNueva: Boolean) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimacionLottie(
                    resId = R.raw.success_check,
                    tamano = 156.dp,
                    iteraciones = 1
                )

                Text(
                    text = if (esNueva) "Sala creada" else "Sala actualizada",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Los cambios se guardaron correctamente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
fun AnimacionHorario(
    modifier: Modifier = Modifier,
    tamano: Dp = 156.dp
) {
    AnimacionLottie(
        resId = R.raw.calendar_check,
        modifier = modifier,
        tamano = tamano,
        iteraciones = 1
    )
}
//Animacion para cuando se crea un bloque
@Composable
fun ConfirmacionHorarioGuardado(esNuevo: Boolean) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimacionHorario()

                Text(
                    text = if (esNuevo) {
                        "Horario creado"
                    } else {
                        "Horario actualizado"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "El bloque horario se guardó correctamente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

//Animación reloj de arena solicitud realizada

@Composable
fun AnimacionReloj(
    modifier: Modifier = Modifier,
    tamano: Dp = 240.dp
) {
    AnimacionLottie(
        resId = R.raw.clock,
        modifier = modifier,
        tamano = tamano,
        iteraciones = LottieConstants.IterateForever
    )
}

@Composable
fun AnimacionCarga(
    modifier: Modifier = Modifier,
    tamano: Dp = 200.dp
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary.copy(0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier.fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimacionLottie(
                resId = R.raw.loading,
                modifier = modifier,
                tamano = tamano,
                iteraciones = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Cargando...",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AnimacionError(
    modifier: Modifier = Modifier,
    tamano: Dp = 200.dp,
    onCerrar: () -> Unit,
    texto: String
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary.copy(0.5f)),
            contentAlignment = Alignment.Center
        ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.secondary,
            shadowElevation = 12.dp
        ) {
            Column (
                modifier = Modifier.fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                AnimacionLottie(
                    resId = R.raw.error,
                    modifier = modifier,
                    tamano = tamano,
                    iteraciones = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = texto,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onCerrar,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Entendido",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AnimacionNotificacionesVacias(
    modifier: Modifier = Modifier,
    tamano: Dp = 160.dp
) {
    AnimacionLottie(
        resId = R.raw.empty_notifications,
        modifier = modifier,
        tamano = tamano
    )
}