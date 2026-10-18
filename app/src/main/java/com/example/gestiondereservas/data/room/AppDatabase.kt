package com.example.gestiondereservas.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gestiondereservas.data.room.entidades.Equipamiento
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import com.example.gestiondereservas.utils.Converters
import com.example.gestiondereservas.data.room.entidades.ReporteEntity
import com.example.gestiondereservas.data.room.entidades.NotificacionEntity

@TypeConverters(Converters::class)
@Database(
    entities = [
        ReservaEntity::class,
        SalaEntity::class,
        Equipamiento::class,
        ReporteEntity::class,
        NotificacionEntity::class
    ],
    version = 5,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun reservaDao(): ReservaDao

    abstract fun notificacionDao(): NotificacionDao
    abstract fun salaDao(): SalaDao

    abstract fun reporteDao(): ReporteDao
    companion object {

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
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS notificacion (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                titulo TEXT NOT NULL,
                descripcion TEXT NOT NULL,
                tipo TEXT NOT NULL,
                esSupervisor INTEGER NOT NULL,
                leida INTEGER NOT NULL,
                reservaId INTEGER
            )
        """.trimIndent())
            }
        }

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


        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(content: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                INSTANCE?: Room.databaseBuilder(
                    content.applicationContext,
                    AppDatabase::class.java,
                    "gestion_reservas_db"
                )
                    .addMigrations(MIGRATION_2_3,MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                    .also { INSTANCE = it }

            }
        }
    }
}