package com.example.gestiondereservas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.CatalogoEquipamientos
import com.example.gestiondereservas.data.repository.SalaRepository
import com.example.gestiondereservas.data.room.entidades.Area
import com.example.gestiondereservas.data.room.entidades.Equipamiento
import com.example.gestiondereservas.data.room.entidades.Sala
import com.example.gestiondereservas.utils.ValidacionesSala
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EquipoForm(
    val nombre: String,
    val seleccionado: Boolean = false,
    val ocupado: Boolean = false
)

data class DatosSala(
    val codigo: String = "",
    val nombre: String = "",
    val capacidad: Int = 30,
    val area: Area = Area.INFORMATICA,
    val equipos: List<EquipoForm> = emptyList(),
    val imagenUri: String? = null
)

data class FormularioSalaState(
    val cargando: Boolean = true,
    val esNueva: Boolean = true,
    val datos: DatosSala = DatosSala(),
    val original: DatosSala = DatosSala(),
    val errorCodigo: String? = null,
    val errorNombre: String? = null,
    val errorCapacidad: String? = null,
    val errorAcceso: String? = null,
    val errorGeneral: String? = null,
    val guardando: Boolean = false,
    val guardada: Boolean = false
) {
    val hayCambios: Boolean
        get() = datos != original
}

class FormularioSalaViewModel(
    private val repositorio: SalaRepository,
    private val salaId: Long
) : ViewModel() {

    private val datosIniciales = DatosSala(
        area = Area.INFORMATICA,
        equipos = CatalogoEquipamientos.opciones.map { EquipoForm(it) }
    )

    private val _estado = MutableStateFlow(
        FormularioSalaState(
            datos = datosIniciales,
            original = datosIniciales
        )
    )

    val estado: StateFlow<FormularioSalaState> = _estado.asStateFlow()

    private var salaCargada: Sala? = null

    init {
        cargar()
    }

    private fun cargar() {
        if (salaId == 0L) {
            _estado.update {
                it.copy(cargando = false, esNueva = true)
            }
            return
        }

        viewModelScope.launch {
            val item = repositorio.obtener(salaId)

            if (item == null) {
                _estado.update {
                    it.copy(
                        cargando = false,
                        errorAcceso = "No se encontró la sala"
                    )
                }
                return@launch
            }

            salaCargada = item.sala

            val datos = DatosSala(
                codigo = item.sala.codigo,
                nombre = item.sala.nombre,
                capacidad = item.sala.capacidad,
                area = item.sala.area,
                equipos = equiposParaFormulario(item.equipamiento),
                imagenUri = item.sala.imagenUri
            )

            _estado.update {
                it.copy(
                    cargando = false,
                    esNueva = false,
                    datos = datos,
                    original = datos
                )
            }
        }
    }

    fun onCodigoChange(valor: String) = _estado.update {
        it.copy(
            datos = it.datos.copy(codigo = valor),
            errorCodigo = null
        )
    }

    fun onNombreChange(valor: String) = _estado.update {
        it.copy(
            datos = it.datos.copy(nombre = valor),
            errorNombre = null
        )
    }

    fun onAreaChange(area: Area) = _estado.update {
        it.copy(datos = it.datos.copy(area = area))
    }

    fun onImagenChange(uri: String?) = _estado.update {
        it.copy(datos = it.datos.copy(imagenUri = uri))
    }

    fun cambiarCapacidad(delta: Int) = _estado.update {
        val nueva = (it.datos.capacidad + delta)
            .coerceIn(
                ValidacionesSala.CAPACIDAD_MIN,
                ValidacionesSala.CAPACIDAD_MAX
            )

        it.copy(
            datos = it.datos.copy(capacidad = nueva),
            errorCapacidad = null
        )
    }

    fun alternarEquipo(nombre: String) = _estado.update { estadoActual ->
        estadoActual.copy(
            datos = estadoActual.datos.copy(
                equipos = estadoActual.datos.equipos.map { equipo ->
                    if (equipo.nombre == nombre) {
                        equipo.copy(
                            seleccionado = !equipo.seleccionado,
                            ocupado = false
                        )
                    } else {
                        equipo
                    }
                }
            )
        )
    }

    fun cambiarEstadoEquipo(nombre: String, ocupado: Boolean) =
        _estado.update { estadoActual ->
            estadoActual.copy(
                datos = estadoActual.datos.copy(
                    equipos = estadoActual.datos.equipos.map { equipo ->
                        if (equipo.nombre == nombre && equipo.seleccionado) {
                            equipo.copy(ocupado = ocupado)
                        } else {
                            equipo
                        }
                    }
                )
            )
        }

    fun guardar() {
        val estadoActual = _estado.value
        if (estadoActual.guardando) return

        val datos = estadoActual.datos
        val errorCodigo = ValidacionesSala.codigo(datos.codigo)
        val errorNombre = ValidacionesSala.nombre(datos.nombre)
        val errorCapacidad = ValidacionesSala.capacidad(datos.capacidad)

        if (
            listOf(errorCodigo, errorNombre, errorCapacidad)
                .any { it != null }
        ) {
            _estado.update {
                it.copy(
                    errorCodigo = errorCodigo,
                    errorNombre = errorNombre,
                    errorCapacidad = errorCapacidad
                )
            }
            return
        }

        viewModelScope.launch {
            _estado.update {
                it.copy(guardando = true, errorGeneral = null)
            }

            try {
                val codigo = datos.codigo.trim()
                val disponible = repositorio.codigoDisponible(
                    codigo,
                    salaCargada?.id ?: 0L
                )

                if (!disponible) {
                    _estado.update {
                        it.copy(
                            guardando = false,
                            errorCodigo = "Ya existe una sala con ese código"
                        )
                    }
                    return@launch
                }

                val sala = Sala(
                    id = salaCargada?.id ?: 0L,
                    codigo = codigo,
                    nombre = datos.nombre.trim(),
                    area = datos.area,
                    capacidad = datos.capacidad,
                    activa = salaCargada?.activa ?: true,
                    imagenUri = datos.imagenUri
                )

                val equipos = datos.equipos
                    .filter { it.seleccionado }
                    .map { equipo ->
                        Equipamiento(
                            salaId = sala.id,
                            nombre = equipo.nombre,
                            ocupado = equipo.ocupado
                        )
                    }

                repositorio.guardar(sala, equipos)

                _estado.update {
                    it.copy(guardando = false, guardada = true)
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(
                        guardando = false,
                        errorGeneral = "No se pudo guardar la sala"
                    )
                }
            }
        }
    }
}

private fun equiposParaFormulario(
    equipamientos: List<Equipamiento>
): List<EquipoForm> {
    val existentes = equipamientos.associateBy {
        it.nombre.trim().lowercase()
    }

    val nombres = (
            CatalogoEquipamientos.opciones +
                    equipamientos.map { it.nombre }
            ).distinctBy {
            it.trim().lowercase()
        }

    return nombres.map { nombre ->
        val existente = existentes[nombre.trim().lowercase()]

        EquipoForm(
            nombre = existente?.nombre ?: nombre,
            seleccionado = existente != null,
            ocupado = existente?.ocupado ?: false
        )
    }
}