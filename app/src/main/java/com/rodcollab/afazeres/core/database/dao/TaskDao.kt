package com.rodcollab.afazeres.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rodcollab.afazeres.core.database.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {


    @Query("UPDATE tasks SET task_isCompleted=:isCompleted WHERE uuid = :taskId")
    suspend fun onToggleChecked(taskId: String, isCompleted: Int)

    @Query("SELECT * FROM tasks WHERE task_isCompleted = 0 AND task_date LIKE '%'||:date||'%' AND user_id = :currentId ORDER BY createdAt DESC")
    suspend fun fetchUncompletedTasks(currentId: String, date: String): List<Task>

    @Query("SELECT * FROM tasks WHERE task_isCompleted = 1 AND task_date LIKE '%'||:date||'%' AND user_id = :currentId ORDER BY createdAt DESC")
    suspend fun fetchCompletedTasks(currentId: String, date:String): List<Task>

    @Query("SELECT * FROM tasks WHERE task_alarmActive = 1 AND user_id = :currentId")
    fun fetchTasksWithAlarm(currentId: String) : Flow<List<Task>>

    @Insert
    suspend fun insert(task: Task)

    @Query(
        """
        DELETE FROM tasks WHERE uuid = :taskId
        """
    )
    suspend fun delete(taskId: String)
}