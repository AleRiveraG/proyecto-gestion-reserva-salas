package com.example.gestiondereservas.data

import android.content.Context
import com.example.gestiondereservas.data.room.HorarioDao
import com.example.gestiondereservas.data.simulacionbd.HorarioDaoPrueba
import com.example.gestiondereservas.data.simulacionbd.OcupacionFuentePrueba

// Tanto los horarios simulados como las ocupaciones leen las salas del mismo SalaDao
// simulado de ProveedorDatos
object ProveedorDatosHorarios {
    private var horarioDaoSimulado: HorarioDao? = null
    private var ocupacionSimulada: OcupacionFuente? = null

    fun horarioDao(context: Context): HorarioDao =
        horarioDaoSimulado
            ?: HorarioDaoPrueba(ProveedorDatos.salaDao(context))
                .also { horarioDaoSimulado = it }
    fun ocupacionFuente(context: Context): OcupacionFuente =
        ocupacionSimulada
            ?: OcupacionFuentePrueba(ProveedorDatos.salaDao(context))
                .also { ocupacionSimulada = it }
}
