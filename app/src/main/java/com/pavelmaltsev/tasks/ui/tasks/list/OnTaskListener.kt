package com.pavelmaltsev.tasks.ui.tasks.list

import com.pavelmaltsev.tasks.module.Task

interface OnTaskListener {
    fun onTaskClick(task: Task)
}