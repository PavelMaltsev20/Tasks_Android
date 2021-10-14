package com.pavelmaltsev.tasks.ui.new_task

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pavelmaltsev.tasks.data.room.MainDatabase
import com.pavelmaltsev.tasks.data.room.task.TaskRepository
import com.pavelmaltsev.tasks.module.Task
import kotlinx.coroutines.launch

class NewTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository: TaskRepository

    init {
        val mainDatabase = MainDatabase.getDatabase(application)
        val taskDao = mainDatabase.taskDao()
        taskRepository = TaskRepository(taskDao)
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskRepository.addTask(task)
        }
    }
}