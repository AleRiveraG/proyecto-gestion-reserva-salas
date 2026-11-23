package com.example.gestiondereservas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.remote.dto.AuthResponseDto
import com.example.gestiondereservas.data.repository.AutenticacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AutenticacionUiState(
    val estaCargando: Boolean = false,
    val mensajeError: String? = null,
    val usuarioAutenticado: AuthResponseDto? = null,
    val registroExitoso: Boolean = false
)

class AutenticacionViewModel(
    private val repository: AutenticacionRepository = AutenticacionRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AutenticacionUiState())
    val uiState: StateFlow<AutenticacionUiState> = _uiState.asStateFlow()

    fun iniciarSesion(correo: String, password: String, rol: String) {
        viewModelScope.launch {
            _uiState.value = AutenticacionUiState(estaCargando = true)
            val resultado = repository.login(correo, password, rol)
            resultado.fold(
                onSuccess = { respuesta ->
                    _uiState.value = AutenticacionUiState(
                        estaCargando = false,
                        usuarioAutenticado = respuesta
                    )
                },
                onFailure = { error ->
                    _uiState.value = AutenticacionUiState(
                        estaCargando = false,
                        mensajeError = error.message ?: "No se pudo iniciar sesión"
                    )
                }
            )
        }
    }

    fun registrarUsuario(nombre: String, correo: String, password: String, rol: String) {
        viewModelScope.launch {
            _uiState.value = AutenticacionUiState(estaCargando = true)
            val resultado = repository.registrar(nombre, correo, password, rol)
            resultado.fold(
                onSuccess = {
                    _uiState.value = AutenticacionUiState(
                        estaCargando = false,
                        registroExitoso = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = AutenticacionUiState(
                        estaCargando = false,
                        mensajeError = error.message ?: "No se pudo completar el registro"
                    )
                }
            )
        }
    }

    fun limpiarEstado() {
        _uiState.value = AutenticacionUiState()
    }
}