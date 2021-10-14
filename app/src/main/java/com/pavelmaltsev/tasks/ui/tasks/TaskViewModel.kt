package com.pavelmaltsev.tasks.ui.tasks

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pavelmaltsev.tasks.data.room.MainDatabase
import com.pavelmaltsev.tasks.data.room.task.TaskRepository
import com.pavelmaltsev.tasks.module.Task
import kotlinx.coroutines.launch
import kotlin.math.log

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository: TaskRepository
    private var _tasksList: LiveData<List<Task>> = MutableLiveData()
    val tasksList get() = _tasksList

    init {
        val mainDatabase = MainDatabase.getDatabase(application)
        val taskDao = mainDatabase.taskDao()
        taskRepository = TaskRepository(taskDao)

        getTaskList()
    }

    private fun getTaskList() {
        viewModelScope.launch {
            _tasksList = taskRepository.getList()
        }
    }
}