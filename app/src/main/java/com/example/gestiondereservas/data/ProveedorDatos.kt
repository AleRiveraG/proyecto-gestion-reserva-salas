package com.example.gestiondereservas.data


import android.content.Context
import com.example.gestiondereservas.data.room.SalaDao
import com.example.gestiondereservas.data.simulacionbd.SalaDaoPrueba

object  ProveedorDatos {


    private val salaDaoSimulado by lazy { SalaDaoPrueba() }
    //con esto ya tengo la simulacion de la bd lista
    // despues cuando exista la AppDatabase central, reemplazar el cuerpo por
    //AppDatabase.obtener(context).salaDao()
    @Suppress("UNUSED_PARAMETER")
    fun salaDao(context: Context): SalaDao = salaDaoSimulado
}