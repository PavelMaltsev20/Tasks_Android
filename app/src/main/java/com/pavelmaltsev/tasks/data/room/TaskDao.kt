package com.pavelmaltsev.tasks.data.room

import androidx.lifecycle.LiveData
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

    @Query("SELECT * FROM TASK_TABLE ORDER BY date, title DESC")
    suspend fun getList(): LiveData<List<Task>>
}