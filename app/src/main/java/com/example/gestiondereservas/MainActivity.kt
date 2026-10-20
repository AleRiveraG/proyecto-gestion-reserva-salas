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
import com.example.gestiondereservas.utils.ObservadorRecordatorios
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            ObservadorRecordatorios.observar(applicationContext)
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        enableEdgeToEdge()

        setContent {
            GestionDeReservasTheme {
                //DESCOMENTEN LA LINEA DE ABAJO PARA PROBAR LA PANTALLA DE HORARIOS PARA ROL TRABAJADOR Y
                // PARA OCUPAR EL DE COORDINADOR SOLO PONGAN gestionHorariosNavigation()
                //GestionHorariosNavigation(puedeEditar = false)
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
    val contexto = LocalContext.current
    val appState = remember {
        AppState(AppDatabase.getInstance(contexto))
    }
    MaterialTheme{
        AppNavigation(navController, appState)
    }
}