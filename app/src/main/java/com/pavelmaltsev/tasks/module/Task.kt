package com.pavelmaltsev.tasks.module

import java.util.*


data class Task(
    val id: Int,
    val date: Date = Date(),
    val title: String = "",
    val desc: String = "",
)
