package com.example.gestiondereservas.data


import com.example.gestiondereservas.data.repository.HorarioRepository
import com.example.gestiondereservas.data.repository.SalaRepository
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.EstadoReserva
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.data.room.entidades.UsuarioEntity
import kotlinx.coroutines.flow.first

// Datos de prueba 2 usuarios 1 de cada rol y 3 reservas
// Los roles deben ser EXACTAMENTE iguales a RolUsuario.nombreMostrar, porque
// a tener en cuent aLoginViewModel compara usuario.rol != rolUsuario.nombreMostrar.
object DatosPruebaUsuariosReservas {

    const val ROL_TRABAJADOR = "Relator / Trabajador"
    const val ROL_COORDINADOR = "Coordinador"

    val trabajador = UsuarioEntity(
        nombre = "Carlos Muñoz Soto",
        correo = "carlos.munoz@duocuc.cl",
        contrasena = "Trabajo123",
        rol = ROL_TRABAJADOR
    )

    val coordinador = UsuarioEntity(
        nombre = "Daniela Rojas Pérez",
        correo = "daniela.rojas@duocuc.cl",
        contrasena = "Coord1234",
        rol = ROL_COORDINADOR
    )

    private data class SemillaReserva(
        val codigoSala: String,
        val estado: EstadoReserva,
        val nombreActividad: String,
        val descripcion: String,
        val tipoActividad: String,
        val organizador: String,
        val fecha: String,
        val horaInicio: String,
        val horaTermino: String,
        val fechaMontaje: String,
        val horaMontaje: String,
        val publico: String,
        val servicios: String,
        val complementos: String
    )

    private val semillas = listOf(
        // APROBADA aparece pintada en la sala A105
        SemillaReserva(
            codigoSala = "A105",
            estado = EstadoReserva.APROBADA,
            nombreActividad = "Charla de Innovación Tecnológica",
            descripcion = "Charla abierta con invitados de la industria sobre innovación y emprendimiento.",
            tipoActividad = "Actividad Extracurricular",
            organizador = "Comunicación",
            fecha = "22/10",
            horaInicio = "15:00",
            horaTermino = "17:00",
            fechaMontaje = "22/10",
            horaMontaje = "14:00",
            publico = "Estudiantes, Docentes",
            servicios = "comunicaciones: Registro de Fotos & Videos; digitales: Microfonos, Streaming",
            complementos = "Mesa para expositores"
        ),

    )

    // de aqui abajo en adelante es el encargado de insertar los datos si aun no existen
    //se complementa con el mainactivity
    //es seguro dejarla asi porque se asegura en cada arranque de la app que esten los datos y no los duplica
    suspend fun sembrar(db: AppDatabase) {
        //salas y horarios, los horarios dependen de lsa salas
        SalaRepository(db.salaDao()).sembrarSiVacio()
        HorarioRepository(
            db.horarioDao(), db.salaDao(), db.reservaDao(), db.usoSalaDao()
        ).sembrarSiVacio()

        //si el trabajador ya existe se asume que todo cargo bien porque priero carga horarios y salas
        val usuarioDao = db.usuarioDao()
        if (usuarioDao.login(trabajador.correo, trabajador.contrasena) != null) return

        usuarioDao.registrarUsuario(trabajador)
        if (usuarioDao.login(coordinador.correo, coordinador.contrasena) == null) {
            usuarioDao.registrarUsuario(coordinador)
        }
        val idTrabajador = usuarioDao.login(trabajador.correo, trabajador.contrasena)!!.idUsuario

        //aqui busca el id real de la sala mediante su codigo
        val salasPorCodigo = db.salaDao().observarTodas("").first()
            .map { it.salaEntity }
            .associateBy { it.codigo.lowercase() }

        semillas.forEach { s ->
            val sala = salasPorCodigo[s.codigoSala.lowercase()] ?: return@forEach
            db.reservaDao().registrar(
                ReservaEntity(
                    idUsuario = idTrabajador,
                    nombreActividad = s.nombreActividad,
                    descripcion = s.descripcion,
                    tipoActividad = s.tipoActividad,
                    tieneValidacion = false,
                    nombreResponsable = "Carlos Muñoz Soto",
                    telefonoResponsable = "912345678",
                    correoResponsable = trabajador.correo,
                    organizador = s.organizador,
                    salaId = sala.id,
                    fechaActividad = s.fecha,
                    horaInicio = s.horaInicio,
                    horaTermino = s.horaTermino,
                    fechaMontaje = s.fechaMontaje,
                    horaMontaje = s.horaMontaje,
                    publico = s.publico,
                    requiereFormulario = false,
                    nombreExternos = "",
                    servicios = s.servicios,
                    complementos = s.complementos,
                    proveedores = "",
                    estado = s.estado
                )
            )
        }
    }
}