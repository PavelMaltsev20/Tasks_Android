package com.pavelmaltsev.tasks.ui.manage_task

import android.app.Application
import android.location.Location
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pavelmaltsev.tasks.data.room.MainDatabase
import com.pavelmaltsev.tasks.data.room.task.TaskRepository
import com.pavelmaltsev.tasks.module.Task
import kotlinx.coroutines.launch
import java.util.*

class ManageViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository: TaskRepository
    var calendar = Calendar.getInstance()
    var imageUri = ""
    val LOCATION_REQUEST_CODE = 116

    private var _latitude = 0.0
    private var _longitude = 0.0
    val latitude get() = _latitude
    val longitude get() = _longitude

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

    fun removeTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }

    fun setLocation(lat: Double, long: Double) {
        _latitude = lat
        _longitude = long
    }

    fun getReadableLocation(): String {
        val lat = String.format("%.3f", latitude)
        val long = String.format("%.3f", longitude)
        return "lat: $lat, long: $long"
    }

    fun getMapUri(): Uri {
        val strUri =
            "http://maps.google.com/maps?q=loc:${_latitude},${_longitude}(A)"
        return Uri.parse(strUri)
    }

}