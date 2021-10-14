package com.pavelmaltsev.tasks.data.room

import com.pavelmaltsev.tasks.module.Task

class TaskRepository(private val taskDao: TaskDao) {

    suspend fun addTask(task: Task) {
        taskDao.insert(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.update(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.delete(task)
    }

//    suspend fun getList() = taskDao.getList()
}