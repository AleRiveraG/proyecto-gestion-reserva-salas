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