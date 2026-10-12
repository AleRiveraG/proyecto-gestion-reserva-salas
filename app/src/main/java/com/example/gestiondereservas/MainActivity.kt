package com.example.gestiondereservas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gestiondereservas.ui.theme.GestionDeReservasTheme
import android.graphics.Color
import androidx.activity.SystemBarStyle
import com.example.gestiondereservas.navigation.GestionSalasNavigation

//este import es por para probar la vista de las distintas areas
import com.example.gestiondereservas.data.room.entidades.Area
import com.example.gestiondereservas.navigation.GestionSalasNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            GestionDeReservasTheme {
                GestionSalasNavigation()
                /*para simular la vista desde otra area poner esto = GestionSalasNavigation(areaCoordinador = Area.CONSTRUCCION)*/
            }
        }
    }
}