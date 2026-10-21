
package com.example.gestiondereservas.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gestiondereservas.data.room.entidades.AuditoriaEntity
import com.example.gestiondereservas.data.room.entidades.Equipamiento
import com.example.gestiondereservas.data.room.entidades.EvaluacionSalaEntity
import com.example.gestiondereservas.data.room.entidades.HorarioEntity
import com.example.gestiondereservas.data.room.entidades.NotificacionEntity
import com.example.gestiondereservas.data.room.entidades.ReporteEntity
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import com.example.gestiondereservas.data.room.entidades.UsuarioEntity
import com.example.gestiondereservas.data.room.entidades.UsoSalaEntity
import com.example.gestiondereservas.utils.Converters

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

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS reporte (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        reservaId INTEGER NOT NULL,
                        salaId INTEGER NOT NULL,
                        descripcion TEXT NOT NULL,
                        fechaReporte TEXT NOT NULL,
                        horaReporte TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS notificacion (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        titulo TEXT NOT NULL,
                        descripcion TEXT NOT NULL,
                        tipo TEXT NOT NULL,
                        esSupervisor INTEGER NOT NULL,
                        leida INTEGER NOT NULL,
                        reservaId INTEGER
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    ALTER TABLE notificacion
                    ADD COLUMN correoDestinatario TEXT
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS evaluacion_sala (
                        reservaId INTEGER NOT NULL,
                        salaId INTEGER NOT NULL,
                        calificacion INTEGER NOT NULL,
                        observaciones TEXT NOT NULL,
                        fechaEvaluacion TEXT NOT NULL,
                        PRIMARY KEY(reservaId)
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gestion_reservas_db"
                )
                    .addMigrations(
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_7_8
                    )
                    .fallbackToDestructiveMigration(true)
                    .build().also { INSTANCE = it }

            }
        }
    }
}
