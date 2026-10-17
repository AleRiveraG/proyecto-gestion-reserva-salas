package com.example.gestiondereservas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.repository.HorarioRepository
import com.example.gestiondereservas.data.room.entidades.DiaSemana
import com.example.gestiondereservas.data.room.entidades.Horario
import com.example.gestiondereservas.data.room.entidades.Sala
import com.example.gestiondereservas.utils.ValidacionesHorario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DatosHorario(
    val salaId: Long = 0L,
    val dia: DiaSemana = DiaSemana.LUNES,
    val inicioMin: Int = 8 * 60 + 30,
    val finMin: Int = 10 * 60,
    val activo: Boolean = true
)

data class FormularioHorarioState(
    val cargando: Boolean = true,
    val esNuevo: Boolean = true,
    val datos: DatosHorario = DatosHorario(),
    val original: DatosHorario = DatosHorario(),
    val salas: List<Sala> = emptyList(),
    val errorSala: String? = null,
    val errorHorario: String? = null,
    val errorAcceso: String? = null,
    val errorGeneral: String? = null,
    val guardando: Boolean = false,
    val guardado: Boolean = false,
    val eliminado: Boolean = false
) {
    val hayCambios: Boolean
        get() = datos != original
}

class FormularioHorarioViewModel(
    private val repositorio: HorarioRepository,
    private val horarioId: Long,
    //datos con los que se abre un horario nuevo (desde la grilla) -1 = sin hora inicial
    private val salaInicialId: Long = 0L,
    private val diaInicial: DiaSemana? = null,
    private val inicioInicialMin: Int = -1
) : ViewModel() {

    private val _estado = MutableStateFlow(FormularioHorarioState())

    val estado: StateFlow<FormularioHorarioState> = _estado.asStateFlow()

    private var horarioCargado: Horario? = null

    init {
        cargar()
    }

    private fun cargar() {
        viewModelScope.launch {
            val salas = repositorio.salasDisponibles()

            if (horarioId == 0L) {
                val activas = salas.filter { sala -> sala.activa }
                val porDefecto = DatosHorario()
                val inicio = if (inicioInicialMin >= 0) {
                    inicioInicialMin.coerceIn(
                        ValidacionesHorario.HORA_MIN,
                        ValidacionesHorario.HORA_MAX - ValidacionesHorario.DURACION_MIN
                    )
                } else {
                    porDefecto.inicioMin
                }
                val fin = if (inicioInicialMin >= 0) {
                    (inicio + 60).coerceAtMost(ValidacionesHorario.HORA_MAX)
                } else {
                    porDefecto.finMin
                }

                _estado.update {
                    it.copy(
                        cargando = false,
                        esNuevo = true,
                        salas = activas,
                        // "original" queda con los valores por defecto así lo
                        // preseleccionado cuenta como cambio y se puede crear de inmediato
                        datos = DatosHorario(
                            salaId = activas.firstOrNull { sala -> sala.id == salaInicialId }?.id ?: 0L,
                            dia = diaInicial ?: porDefecto.dia,
                            inicioMin = inicio,
                            finMin = fin
                        )
                    )
                }
                return@launch
            }

            val item = repositorio.obtener(horarioId)

            if (item == null) {
                _estado.update {
                    it.copy(
                        cargando = false,
                        errorAcceso = "No se encontró el horario"
                    )
                }
                return@launch
            }

            horarioCargado = item.horario

            val datos = DatosHorario(
                salaId = item.horario.salaId,
                dia = item.horario.dia,
                inicioMin = item.horario.inicioMin,
                finMin = item.horario.finMin,
                activo = item.horario.activo
            )

            _estado.update {
                it.copy(
                    cargando = false,
                    esNuevo = false,
                    datos = datos,
                    original = datos,
                    // Una sala inactiva solo se ofrece si ya es la de este horario
                    salas = salas.filter { sala ->
                        sala.activa || sala.id == item.horario.salaId
                    }
                )
            }
        }
    }

    fun onSalaChange(salaId: Long) = _estado.update {
        it.copy(
            datos = it.datos.copy(salaId = salaId),
            errorSala = null,
            errorHorario = null
        )
    }

    fun onDiaChange(dia: DiaSemana) = _estado.update {
        it.copy(
            datos = it.datos.copy(dia = dia),
            errorHorario = null
        )
    }

    fun onActivoChange(activo: Boolean) = _estado.update {
        it.copy(datos = it.datos.copy(activo = activo))
    }

    fun cambiarInicio(delta: Int) = _estado.update {
        val nuevo = (it.datos.inicioMin + delta).coerceIn(
            ValidacionesHorario.HORA_MIN,
            ValidacionesHorario.HORA_MAX - ValidacionesHorario.DURACION_MIN
        )

        it.copy(
            datos = it.datos.copy(inicioMin = nuevo),
            errorHorario = null
        )
    }

    fun cambiarFin(delta: Int) = _estado.update {
        val nuevo = (it.datos.finMin + delta).coerceIn(
            ValidacionesHorario.HORA_MIN + ValidacionesHorario.DURACION_MIN,
            ValidacionesHorario.HORA_MAX
        )

        it.copy(
            datos = it.datos.copy(finMin = nuevo),
            errorHorario = null
        )
    }

    fun guardar() {
        val estadoActual = _estado.value
        if (estadoActual.guardando) return

        val datos = estadoActual.datos
        val errorSala = ValidacionesHorario.sala(datos.salaId)
        val errorHorario = ValidacionesHorario.rango(datos.inicioMin, datos.finMin)

        if (errorSala != null || errorHorario != null) {
            _estado.update {
                it.copy(errorSala = errorSala, errorHorario = errorHorario)
            }
            return
        }

        viewModelScope.launch {
            _estado.update {
                it.copy(guardando = true, errorGeneral = null)
            }

            try {
                val cruce = repositorio.hayCruce(
                    salaId = datos.salaId,
                    dia = datos.dia,
                    inicioMin = datos.inicioMin,
                    finMin = datos.finMin,
                    excluirId = horarioCargado?.id ?: 0L
                )

                if (cruce) {
                    _estado.update {
                        it.copy(
                            guardando = false,
                            errorHorario = "Se cruza con otro bloque de la sala o queda a menos de ${ValidacionesHorario.INTERVALO_MIN} minutos de él"
                        )
                    }
                    return@launch
                }

                val horario = Horario(
                    id = horarioCargado?.id ?: 0L,
                    salaId = datos.salaId,
                    dia = datos.dia,
                    inicioMin = datos.inicioMin,
                    finMin = datos.finMin,
                    activo = datos.activo
                )

                repositorio.guardar(horario)

                _estado.update {
                    it.copy(guardando = false, guardado = true)
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(
                        guardando = false,
                        errorGeneral = "No se pudo guardar el horario"
                    )
                }
            }
        }
    }

    fun eliminar() {
        val id = horarioCargado?.id ?: return
        if (_estado.value.guardando) return

        viewModelScope.launch {
            _estado.update {
                it.copy(guardando = true, errorGeneral = null)
            }

            try {
                repositorio.eliminar(id)
                _estado.update {
                    it.copy(guardando = false, eliminado = true)
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(
                        guardando = false,
                        errorGeneral = "No se pudo eliminar el horario"
                    )
                }
            }
        }
    }
}
