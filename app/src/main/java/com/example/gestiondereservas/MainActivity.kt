package com.example.gestiondereservas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.gestiondereservas.data.AppState
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.ui.theme.GestionDeReservasTheme
import com.example.gestiondereservas.ui.views.GestionReservasScreen
import com.example.gestiondereservas.ui.views.UsoSalaScreen
import com.example.gestiondereservas.ui.views.ReservasScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val db = AppDatabase.getInstance(applicationContext)
        val appState = AppState(db)

        setContent {
            GestionDeReservasTheme {
                val navController = rememberNavController()
                ReservasScreen(navController, appState)
                GestionReservasScreen()
            }
        }
    }
}
