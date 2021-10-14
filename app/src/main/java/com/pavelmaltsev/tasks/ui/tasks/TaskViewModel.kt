package com.pavelmaltsev.tasks.ui.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Tasks
import com.pavelmaltsev.tasks.data.room.MainDatabase
import com.pavelmaltsev.tasks.data.room.TaskRepository
import com.pavelmaltsev.tasks.module.Task
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository: TaskRepository
    private var _tasksList: MutableLiveData<List<Task>> = MutableLiveData()
    val tasksList get() = _tasksList

    init {
        val mainDatabase = MainDatabase.getDatabase(application)
        val taskDao = mainDatabase.taskDao()
        taskRepository = TaskRepository(taskDao)

        getTaskList()
    }

    private fun getTaskList() {
        viewModelScope.launch {
            _tasksList.value = taskRepository.getList()
        }
    }
}