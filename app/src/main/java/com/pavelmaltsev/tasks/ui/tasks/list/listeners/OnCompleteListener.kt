package com.pavelmaltsev.tasks.ui.tasks.list.listeners

import com.pavelmaltsev.tasks.module.Task

interface OnCompleteListener {
    fun onComplete(task: Task)
}