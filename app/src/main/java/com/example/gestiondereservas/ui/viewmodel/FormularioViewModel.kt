package com.example.gestiondereservas.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.CatalogoEquipamientos
import com.example.gestiondereservas.data.repository.SalaRepository
import com.example.gestiondereservas.data.room.entidades.Area
import com.example.gestiondereservas.data.room.entidades.Equipamiento
import com.example.gestiondereservas.data.room.entidades.SalaEntity
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
    val encargado: String = Area.INFORMATICA.encargadoPorDefecto,
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
    val errorEncargado: String? = null,
    val errorCapacidad: String? = null,
    val errorAcceso: String? = null,
    val errorGeneral: String? = null,
    val guardando: Boolean = false,
    val guardada: Boolean = false
) {
    val hayCambios: Boolean get() = datos != original
}

class FormularioSalaViewModel(
    private val repositorio: SalaRepository,
    private val salaId: Long,
    private val areaCoordinador: Area
) : ViewModel() {

    private val datosIniciales = DatosSala(
        area = areaCoordinador,
        encargado = areaCoordinador.encargadoPorDefecto,
        equipos = CatalogoEquipamientos.opciones.map { EquipoForm(it) }
    )
    private val _estado = MutableStateFlow(
        FormularioSalaState(datos = datosIniciales, original = datosIniciales)
    )
    val estado: StateFlow<FormularioSalaState> = _estado.asStateFlow()

    private var salaEntityCargada: SalaEntity? = null

    init {
        cargar()
    }

    private fun cargar() {
        if (salaId == 0L) {
            _estado.update { it.copy(cargando = false, esNueva = true) }
            return
        }
        viewModelScope.launch {
            val item = repositorio.obtener(salaId)
            if (item == null) {
                _estado.update {
                    it.copy(cargando = false, errorAcceso = "No se encontró la sala")
                }
                return@launch
            }
            if (item.salaEntity.area != areaCoordinador) {
                _estado.update {
                    it.copy(
                        cargando = false,
                        errorAcceso = "Esta sala no pertenece a tu área asignada"
                    )
                }
                return@launch
            }
            salaEntityCargada = item.salaEntity
            val datos = DatosSala(
                codigo = item.salaEntity.codigo,
                nombre = item.salaEntity.nombre,
                capacidad = item.salaEntity.capacidad,
                area = item.salaEntity.area,
                encargado = item.salaEntity.encargado,
                equipos = equiposParaFormulario(item.equipamiento),
                imagenUri = item.salaEntity.imagenUri
            )
            _estado.update {
                it.copy(cargando = false, esNueva = false, datos = datos, original = datos)
            }
        }
    }

    fun onCodigoChange(valor: String) = _estado.update {
        it.copy(datos = it.datos.copy(codigo = valor), errorCodigo = null)
    }

    fun onNombreChange(valor: String) = _estado.update {
        it.copy(datos = it.datos.copy(nombre = valor), errorNombre = null)
    }

    fun onEncargadoChange(valor: String) = _estado.update {
        it.copy(datos = it.datos.copy(encargado = valor), errorEncargado = null)
    }

    fun onImagenChange(uri: String?) = _estado.update {
        it.copy(datos = it.datos.copy(imagenUri = uri))
    }

    fun cambiarCapacidad(delta: Int) = _estado.update {
        val nueva = (it.datos.capacidad + delta)
            .coerceIn(ValidacionesSala.CAPACIDAD_MIN, ValidacionesSala.CAPACIDAD_MAX)
        it.copy(datos = it.datos.copy(capacidad = nueva), errorCapacidad = null)
    }

    fun alternarEquipo(nombre: String) = _estado.update { e ->
        e.copy(
            datos = e.datos.copy(
                equipos = e.datos.equipos.map {
                    if (it.nombre == nombre) {
                        it.copy(seleccionado = !it.seleccionado, ocupado = false)
                    } else it
                }
            )
        )
    }

    fun cambiarEstadoEquipo(nombre: String, ocupado: Boolean) = _estado.update { e ->
        e.copy(
            datos = e.datos.copy(
                equipos = e.datos.equipos.map {
                    if (it.nombre == nombre && it.seleccionado) it.copy(ocupado = ocupado) else it
                }
            )
        )
    }

    fun guardar() {
        val actual = _estado.value
        if (actual.guardando) return
        val d = actual.datos

        val errCodigo = ValidacionesSala.codigo(d.codigo)
        val errNombre = ValidacionesSala.nombre(d.nombre)
        val errEncargado = ValidacionesSala.encargado(d.encargado)
        val errCapacidad = ValidacionesSala.capacidad(d.capacidad)

        if (listOf(errCodigo, errNombre, errEncargado, errCapacidad).any { it != null }) {
            _estado.update {
                it.copy(
                    errorCodigo = errCodigo,
                    errorNombre = errNombre,
                    errorEncargado = errEncargado,
                    errorCapacidad = errCapacidad
                )
            }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(guardando = true, errorGeneral = null) }
            try {
                val codigo = d.codigo.trim()
                val disponible = repositorio.codigoDisponible(codigo, salaEntityCargada?.id ?: 0L)
                if (!disponible) {
                    _estado.update {
                        it.copy(guardando = false, errorCodigo = "Ya existe una sala con ese código")
                    }
                    return@launch
                }
                val salaEntity = SalaEntity(
                    id = salaEntityCargada?.id ?: 0L,
                    codigo = codigo,
                    nombre = d.nombre.trim(),
                    area = areaCoordinador,
                    encargado = d.encargado.trim(),
                    capacidad = d.capacidad,
                    activa = salaEntityCargada?.activa ?: true,
                    imagenUri = d.imagenUri
                )
                val equipos = d.equipos.filter { it.seleccionado }.map {
                    Equipamiento(salaId = salaEntity.id, nombre = it.nombre, ocupado = it.ocupado)
                }
                repositorio.guardar(salaEntity, equipos)
                _estado.update { it.copy(guardando = false, guardada = true) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(guardando = false, errorGeneral = "No se pudo guardar la sala")
                }
            }
        }
    }
}

private fun equiposParaFormulario(equipamientos: List<Equipamiento>): List<EquipoForm> {
    val existentes = equipamientos.associateBy { it.nombre.trim().lowercase() }
    val nombres = (CatalogoEquipamientos.opciones + equipamientos.map { it.nombre })
        .distinctBy { it.trim().lowercase() }

    return nombres.map { nombre ->
        val existente = existentes[nombre.trim().lowercase()]
        EquipoForm(
            nombre = existente?.nombre ?: nombre,
            seleccionado = existente != null,
            ocupado = existente?.ocupado ?: false
        )
    }
}
