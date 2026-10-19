package com.example.gestiondereservas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.repository.UsuarioRepository
import com.example.gestiondereservas.data.room.entidades.UsuarioEntity
import com.example.gestiondereservas.ui.views.RolRegistroUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val usuarioRepository: UsuarioRepository
): ViewModel() {

    private val _estaCargando = MutableStateFlow(false)
    val estaCargando: StateFlow<Boolean> = _estaCargando

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    fun registrarUsuario(
        nombre: String,
        correo: String,
        contrasena: String,
        rol: String,
        onRegistroExitoso: () -> Unit
    ) {
        viewModelScope.launch {
            _estaCargando.value = true
            _mensajeError.value = null

            val nuevoUsuario = UsuarioEntity(
                correo = correo.trim().lowercase(),
                nombre = nombre,
                contrasena = contrasena,
                rol = rol
            )

            usuarioRepository.registrarUsuario(nuevoUsuario)

            onRegistroExitoso()
        }
    }
}