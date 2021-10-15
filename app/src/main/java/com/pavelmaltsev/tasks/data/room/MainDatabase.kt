package com.pavelmaltsev.tasks.data.room

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.firebase.auth.FirebaseAuth
import com.pavelmaltsev.tasks.data.room.task.TaskDao
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
        var INSTANCE: MainDatabase? = null

        fun getDatabase(context: Context): MainDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): MainDatabase {
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            return Room.databaseBuilder(
                context.applicationContext,
                MainDatabase::class.java,
                "${userId}_tasks"
            ).build()
        }
    }
}