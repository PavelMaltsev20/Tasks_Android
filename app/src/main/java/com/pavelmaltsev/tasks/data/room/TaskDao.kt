package com.pavelmaltsev.tasks.data.room

import androidx.lifecycle.LiveData
import androidx.room.*

class TaskDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Update
    fun update(user: User)

    @Delete
    fun deleteCurrent(user: User)


    @Query("SELECT * FROM USER_TABLE WHERE id != :userId ORDER BY lastMsg, userName DESC")
    fun getList(userId: String): LiveData<List<User>>

}