package com.example.gestiondereservas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.repository.UsuarioRepository
import com.example.gestiondereservas.ui.views.RolUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val usuarioRepository: UsuarioRepository
): ViewModel() {

    private val _estaCargando = MutableStateFlow(false)
    val estaCargando: StateFlow<Boolean> = _estaCargando

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    fun iniciarSesion(correo: String,
                      contrasena: String,
                      rolUsuario: RolUsuario,
                      onIniciarSesion: () -> Unit) {
        viewModelScope.launch {
            _estaCargando.value = true
            _mensajeError.value = null

            val usuario = usuarioRepository.login(correo, contrasena)

            if (usuario == null) {
                _mensajeError.value = "Correo o contraseña incorrectos"
            } else if (usuario.rol != rolUsuario.nombreMostrar) {
                _mensajeError.value = "Este correo es ${usuario.rol}"
            } else {
                onIniciarSesion()
            }

            _estaCargando.value = false

        }
    }
}