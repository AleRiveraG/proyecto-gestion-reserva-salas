package com.example.gestiondereservas.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gestiondereservas.utils.Converters

@TypeConverters(Converters::class)
@Database(entities = [ReservaEntity::class],
        version = 1,
        exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reservaDao(): ReservaDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(content: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                Room.databaseBuilder(
                    content.applicationContext,
                    AppDatabase::class.java,
                    "gestion_reservas_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}