package com.rodcollab.afazeres.core.repository

import com.rodcollab.afazeres.core.database.dao.TaskDao
import com.rodcollab.afazeres.core.database.entity.Task
import com.rodcollab.afazeres.core.model.TaskDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class TasksRepositoryImpl @Inject constructor(private val dao: TaskDao) : TasksRepository {

    override fun uncompletedTasks(): Flow<List<TaskDomain>> {
        return dao.fetchUncompletedTasks().map { tasks ->
            tasks.map {
                TaskDomain(
                    taskId = it.uuid,
                    taskTitle = it.title,
                    taskCategory = it.category,
                    taskDate = it.date,
                    taskTime = it.time,
                    isCompleted = it.isCompleted,
                    alarmActive = it.alarmActive,
                    reminderTime = it.reminderTime,
                    triggerTime = it.triggerTime
                )
            }
        }
    }

    override fun completedTasks(): Flow<List<TaskDomain>> {
        return dao.fetchCompletedTasks().map { tasks ->
            tasks.map {
                TaskDomain(
                    taskId = it.uuid,
                    taskTitle = it.title,
                    taskCategory = it.category,
                    taskDate = it.date,
                    taskTime = it.time,
                    isCompleted = it.isCompleted,
                    alarmActive = it.alarmActive,
                    reminderTime = it.reminderTime,
                    triggerTime =  it.triggerTime
                )
            }
        }
    }

    override suspend fun toggleTaskCompleted(taskId: String, isCompleted: Int) {

        dao.onToggleChecked(taskId, isCompleted)
    }

    override suspend fun add(
        taskTitle: String,
        taskCategory: String,
        taskDate: Long?,
        taskTime: String?,
        alarmActive: Boolean,
        reminderTime: Long?,
        triggerTime: Long?,
    ) {

        dao.insert(

            Task(
                uuid = UUID.randomUUID().toString(),
                title = taskTitle,
                category = taskCategory,
                date = convertDate(taskDate),
                time = taskTime,
                isCompleted = false,
                alarmActive = alarmActive,
                reminderTime = reminderTime,
                 triggerTime = triggerTime
            )
        )
    }

    private fun convertDate(taskDate: Long?): String {
        val day = taskDate?.let { getDayMonthYear(it)[0] }
        val month = taskDate?.let { getDayMonthYear(taskDate)[1] }
        val year = taskDate?.let { getDayMonthYear(taskDate)[2] }
        return "$day/$month/$year"
    }

    private fun getDayMonthYear(taskDate: Long): Array<String> {
        val date = Date()
        date.time = taskDate
        val calendarInstance = Calendar.getInstance()
        calendarInstance.time = date
        val day = (calendarInstance.get(Calendar.DAY_OF_MONTH) + 1).toString().padStart(2, '0')
        val month = (calendarInstance.get(Calendar.MONTH) + 1).toString().padStart(2,'0')
        val year = calendarInstance.get(Calendar.YEAR).toString()
        return arrayOf(day, month, year)
    }

    override suspend fun delete(taskId: String) {
        dao.delete(taskId)
    }
}