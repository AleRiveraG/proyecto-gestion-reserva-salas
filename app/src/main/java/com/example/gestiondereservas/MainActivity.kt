
package com.example.gestiondereservas

import android.graphics.Color
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.navigation.AppNavigation
import com.example.gestiondereservas.ui.theme.GestionDeReservasTheme
import com.example.gestiondereservas.ui.views.GestionReservasScreen
import com.example.gestiondereservas.ui.views.RelatorScreen
import com.example.gestiondereservas.ui.views.UsoSalaScreen
import com.example.gestiondereservas.ui.views.ReservasScreen
import com.example.gestiondereservas.ui.views.SupervisorScreen
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.example.gestiondereservas.data.AppState

//este import es por para probar la vista de las distintas areas
import com.example.gestiondereservas.data.room.entidades.Area
import com.example.gestiondereservas.ui.views.SalasScreen
import com.example.gestiondereservas.ui.views.SupervisorScreen
import com.example.gestiondereservas.utils.ObservadorNotificaciones
import com.example.gestiondereservas.utils.ObservadorRecordatorios
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import com.example.gestiondereservas.data.DatosPruebaUsuariosReservas

class MainActivity : ComponentActivity() {

    private val abrirNotificaciones = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        abrirNotificaciones.value =
            intent?.getBooleanExtra("abrirNotificaciones", false) ?: false


        lifecycleScope.launch {
            try {
                ObservadorRecordatorios.observar(applicationContext)
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e

                android.util.Log.e(
                    "Recordatorios",
                    "Error en observador de recordatorios",
                    e
                )
            }
        }

        lifecycleScope.launch {
            try {
                ObservadorNotificaciones.observar(applicationContext)
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e

                android.util.Log.e(
                    "Notificaciones",
                    "Error en observador de notificaciones",
                    e
                )
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            DatosPruebaUsuariosReservas.sembrar(AppDatabase.getInstance(applicationContext))
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        setContent {
            GestionDeReservasTheme {
                MyApp(
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
fun MyApp(abrirNotificaciones: Boolean = false) {

    val navController = rememberNavController()
    val contexto = LocalContext.current

    val appState = remember {
        AppState(AppDatabase.getInstance(contexto))
    }

    MaterialTheme {
        AppNavigation(
            navController = navController,
            appState = appState,
        )
    }
}
