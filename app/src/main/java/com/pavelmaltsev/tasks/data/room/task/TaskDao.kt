package com.pavelmaltsev.tasks.data.room.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.pavelmaltsev.tasks.module.Task

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM TASK_TABLE ORDER BY isComplete ASC, date ASC, title ASC")
      fun getList(): LiveData<List<Task>>
}