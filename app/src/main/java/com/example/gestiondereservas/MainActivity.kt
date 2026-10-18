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
import androidx.lifecycle.lifecycleScope

//este import es por para probar la vista de las distintas areas
import com.example.gestiondereservas.data.room.entidades.Area
import com.example.gestiondereservas.ui.views.SalasScreen
import com.example.gestiondereservas.utils.ObservadorNotificaciones
import com.example.gestiondereservas.utils.ObservadorRecordatorios
import kotlinx.coroutines.launch
import android.content.Intent
import androidx.compose.runtime.mutableStateOf

class MainActivity : ComponentActivity() {

    private val abrirNotificaciones = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        abrirNotificaciones.value =
            intent?.getBooleanExtra("abrirNotificaciones", false) ?: false

        lifecycleScope.launch {
            ObservadorRecordatorios.observar(applicationContext)
        }
        lifecycleScope.launch {
            ObservadorNotificaciones.observar(applicationContext)
        }
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
                myApp(
                    abrirNotificaciones = abrirNotificaciones.value
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        abrirNotificaciones.value =
            intent.getBooleanExtra("abrirNotificaciones", false)
    }
}

@Composable
fun myApp(abrirNotificaciones: Boolean = false) {

    val navController = rememberNavController()

    MaterialTheme {
        appNavigation(
            navController = navController,
            abrirNotificaciones = abrirNotificaciones
        )
    }
}