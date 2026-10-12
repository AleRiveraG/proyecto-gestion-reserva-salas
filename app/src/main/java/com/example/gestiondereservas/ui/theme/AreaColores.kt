package com.example.gestiondereservas.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.gestiondereservas.data.room.entidades.Area

val Area.color: Color
    get() = when (this) {
        Area.INFORMATICA -> Color(0xFF307FE2)
        Area.FOTOGRAFIA -> Color(0xFFD50032)
        Area.DISENO -> Color(0xFF849000)
        Area.COMUNICACIONES -> Color(0xFFD50032)
        Area.SALUD -> Color(0xFF087CA5)
        Area.GASTRONOMIA -> Color(0xFFD63036)
        Area.TURISMO -> Color(0xFF007C74)
        Area.CONSTRUCCION -> Color(0xFFB64F00)
    }