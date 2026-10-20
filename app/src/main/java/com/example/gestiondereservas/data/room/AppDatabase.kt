
package com.example.gestiondereservas.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gestiondereservas.data.room.entidades.AuditoriaEntity
import com.example.gestiondereservas.data.room.entidades.Equipamiento
import com.example.gestiondereservas.data.room.entidades.HorarioEntity
import com.example.gestiondereservas.data.room.entidades.NotificacionEntity
import com.example.gestiondereservas.data.room.entidades.ReporteEntity
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import com.example.gestiondereservas.data.room.entidades.UsuarioEntity
import com.example.gestiondereservas.data.room.entidades.UsoSalaEntity
import com.example.gestiondereservas.utils.Converters
import com.example.gestiondereservas.data.room.entidades.EvaluacionSalaEntity
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@TypeConverters(Converters::class)
@Database(
    entities = [ReservaEntity::class, SalaEntity::class,
        Equipamiento::class, ReporteEntity::class,
        NotificacionEntity::class, UsoSalaEntity::class,
        HorarioEntity::class, AuditoriaEntity::class,
        EvaluacionSalaEntity::class, UsuarioEntity::class],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reservaDao(): ReservaDao
    abstract fun salaDao(): SalaDao
    abstract fun horarioDao(): HorarioDao
    abstract fun auditoriaDao(): AuditoriaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun reporteDao(): ReporteDao
    abstract fun notificacionDao(): NotificacionDao
    abstract fun usoSalaDao(): UsoSalaDao
    abstract fun evaluacionSalaDao(): EvaluacionSalaDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(content: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                Room.databaseBuilder(
                                content.applicationContext,
                                AppDatabase::class.java,
                                "gestion_reservas_db"
                            ).fallbackToDestructiveMigration(true)
                    .build().also { INSTANCE = it }

            }
        }
    }
}