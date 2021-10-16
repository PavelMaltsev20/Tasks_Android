package com.pavelmaltsev.tasks.module

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "TASK_TABLE")
data class Task(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val date: Long = 0,
    val title: String = "",
    val desc: String = "",
    var isComplete: Boolean = false,
    val imageUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
) : Serializable
