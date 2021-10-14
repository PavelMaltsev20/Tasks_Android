package com.pavelmaltsev.tasks.module

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "TASK_TABLE")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: Long = 0,
    val title: String = "",
    val desc: String = "",
    val imageUrl: String = "",
) : Serializable
