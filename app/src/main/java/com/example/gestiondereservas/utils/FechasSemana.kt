package com.example.gestiondereservas.utils

import java.util.Calendar
import java.util.Locale

object FechasSemana {
    private val meses = listOf(
        "ene", "feb", "mar", "abr", "may", "jun",
        "jul", "ago", "sep", "oct", "nov", "dic"
    )

    //dias devuelve los 7 dias de la semana actual cooridos hacia adelante o atras con 0 esta semana
    // y la siguiente 1 y -1 la anterior
    fun dias(offsetSemanas: Int): List<Calendar> {
        val lunes = Calendar.getInstance()
        // DAY_OF_WEEK: domingo = 1 ... sábado = 7  ->  días transcurridos desde el lunes
        val desdeLunes = (lunes.get(Calendar.DAY_OF_WEEK) + 5) % 7
        lunes.add(Calendar.DAY_OF_YEAR, -desdeLunes + 7 * offsetSemanas)

        return List(7) { i ->
            (lunes.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, i) }
        }
    }

    //devuelve la fecha en el mismo formato que esta en reservas para que no haya sorpresas
    fun ddMM(dia: Calendar): String =
        String.format(
            Locale.US,
            "%02d/%02d",
            dia.get(Calendar.DAY_OF_MONTH),
            dia.get(Calendar.MONTH) + 1
        )

    //devuelve solo el numero del dia
    fun numero(dia: Calendar): Int = dia.get(Calendar.DAY_OF_MONTH)


    //dice si el dia es anterior a hoy se usa para avisar por ejemplo cuando un trabajador toca un bloque que
    // ya paso por lo tanto no puede reservar
    fun esPasado(dia: Calendar): Boolean {
        val hoy = Calendar.getInstance()
        val a = dia.get(Calendar.YEAR) * 1000 + dia.get(Calendar.DAY_OF_YEAR)
        val b = hoy.get(Calendar.YEAR) * 1000 + hoy.get(Calendar.DAY_OF_YEAR)
        return a < b
    }

    //arma el texto de arriba ejmplo 12 octubre - 18 pct 2026 tomando el primer y ultimo dia de la semana
    fun rango(dias: List<Calendar>): String {
        val primero = dias.first()
        val ultimo = dias.last()
        return "${numero(primero)} ${meses[primero.get(Calendar.MONTH)]} — " +
                "${numero(ultimo)} ${meses[ultimo.get(Calendar.MONTH)]} " +
                "${ultimo.get(Calendar.YEAR)}"
    }
}
