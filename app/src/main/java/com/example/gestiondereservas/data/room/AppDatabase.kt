package com.example.gestiondereservas.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gestiondereservas.data.room.entidades.AuditoriaEntity
import com.example.gestiondereservas.data.room.entidades.Equipamiento
import com.example.gestiondereservas.data.room.entidades.HorarioEntity
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import com.example.gestiondereservas.utils.Converters

@TypeConverters(Converters::class)
@Database(entities = [ReservaEntity::class, SalaEntity::class, Equipamiento::class,
    AuditoriaEntity::class, HorarioEntity::class],
        version = 4,
        exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reservaDao(): ReservaDao
    abstract fun salaDao(): SalaDao
    abstract fun horarioDao(): HorarioDao
    abstract fun auditoriaDao(): AuditoriaDao
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