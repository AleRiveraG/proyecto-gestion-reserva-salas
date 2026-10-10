package com.example.gestiondereservas.utils

import androidx.room.TypeConverter
import com.example.gestiondereservas.data.room.EstadoReserva

class Converters {

    @TypeConverter
    fun convertirEstadoAString(estado: EstadoReserva): String {
        return estado.name
    }

    @TypeConverter
    fun convertirStringAEstado(estado: String): EstadoReserva {
        return EstadoReserva.valueOf(estado)
    }
}