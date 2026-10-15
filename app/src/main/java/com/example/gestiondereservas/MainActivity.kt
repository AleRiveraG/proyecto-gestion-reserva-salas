package com.example.gestiondereservas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.gestiondereservas.data.AppState
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.navigation.appNavigation
import com.example.gestiondereservas.ui.theme.GestionDeReservasTheme
import com.example.gestiondereservas.ui.views.GestionReservasScreen
import com.example.gestiondereservas.ui.views.RelatorScreen
import com.example.gestiondereservas.ui.views.UsoSalaScreen
import com.example.gestiondereservas.ui.views.ReservasScreen
import com.example.gestiondereservas.ui.views.SupervisorScreen
import android.graphics.Color
import androidx.activity.SystemBarStyle

//este import es por para probar la vista de las distintas areas
import com.example.gestiondereservas.data.room.entidades.Area
import com.example.gestiondereservas.ui.views.SalasScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        enableEdgeToEdge()

        setContent {
            GestionDeReservasTheme {
                //ReservasScreen(appState)
                //GestionReservasScreen(appState)
                //RelatorScreen()
                //SupervisorScreen(appState)
                //SalasScreen()
                myApp()
            }
        }
    }
}

@Composable
fun myApp() {
    val navController =  rememberNavController()

    MaterialTheme{
        appNavigation(navController)
    }
}
