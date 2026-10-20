package com.example.gestiondereservas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gestiondereservas.ui.theme.GestionDeReservasTheme
import android.graphics.Color
import androidx.activity.SystemBarStyle
import com.example.gestiondereservas.navigation.GestionHorariosNavigation
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
                //GestionSalasNavigation()
                //DESCOMENTEN LA LINEA DE ABAJO PARA PROBAR LA PANTALLA DE HORARIOS PARA ROL TRABAJADOR Y
                // PARA OCUPAR EL DE COORDINADOR SOLO PONGAN
            GestionHorariosNavigation()
                //GestionHorariosNavigation(puedeEditar = false)
            }
        }
    }
}



