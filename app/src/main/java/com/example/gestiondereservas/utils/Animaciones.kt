package com.example.gestiondereservas.utils

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.gestiondereservas.R

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
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

//Animación específica para Registro (apunta a res/raw/registro.json).

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