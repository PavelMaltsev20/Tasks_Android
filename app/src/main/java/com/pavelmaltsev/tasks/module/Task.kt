package com.pavelmaltsev.tasks.module

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "TASK_TABLE")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: Date = Date(),
    val title: String = "",
    val desc: String = "",
)
