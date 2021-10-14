package com.pavelmaltsev.tasks.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pavelmaltsev.tasks.module.Task

@Database(
    entities = [
        Task::class,
    ], version = 1, exportSchema = false
)
abstract class MainDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: MainDatabase? = null

        fun getDatabase(context: Context): MainDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): MainDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MainDatabase::class.java,
                "tasks"
            ).build()
        }
    }
}