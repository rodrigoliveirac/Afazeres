package com.rodcollab.afazeres.core.model

data class TaskDomain(
    val taskId: String,
    val taskTitle: String,
    val taskCategory: String,
    val taskDate: String,
    val isCompleted: Boolean
)