package com.example.gestiondereservas

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.navigation.AppNavigation
import com.example.gestiondereservas.ui.theme.GestionDeReservasTheme
import com.example.gestiondereservas.ui.views.ReservasScreen
import com.example.gestiondereservas.ui.views.SalasScreen
import com.example.gestiondereservas.ui.views.SupervisorScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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


        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
    }
}

@Composable
fun myApp() {
    val navController =  rememberNavController()

    MaterialTheme{
        AppNavigation(navController)
    }
}
