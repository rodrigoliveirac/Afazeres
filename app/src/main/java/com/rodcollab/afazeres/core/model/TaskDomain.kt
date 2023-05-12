package com.rodcollab.afazeres.core.model

data class TaskDomain(
    val userId:String,
    val taskId: String,
    val taskTitle: String,
    val taskCategory: String,
    val taskDate: String,
    val taskTime: String?,
    val isCompleted: Boolean,
    val alarmActive: Boolean,
    val reminderTime: Long?,
    val triggerTime:Long?,
    val createdAt: Long
)