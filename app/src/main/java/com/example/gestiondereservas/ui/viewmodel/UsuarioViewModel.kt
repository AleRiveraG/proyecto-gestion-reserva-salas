package com.example.gestiondereservas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.repository.UsuarioRepository
import com.example.gestiondereservas.data.room.entidades.UsuarioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsuarioViewModel(
    private val usuarioRepository: UsuarioRepository
): ViewModel() {

    private val _usuario = MutableStateFlow<UsuarioEntity?>(null)
    val usuario: StateFlow<UsuarioEntity?> = _usuario

    fun cargarUsuario(idUsuario: Long) {
        viewModelScope.launch {
            _usuario.value = usuarioRepository.obtenerPorId(idUsuario)
        }
    }
}