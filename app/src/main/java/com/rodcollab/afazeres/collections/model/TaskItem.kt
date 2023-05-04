package com.rodcollab.afazeres.collections.model

data class TaskItem(
    val id: String,
    val title: String,
    val category: String,
    val isCompleted: Boolean,
    val date: String,
    val triggerTime: Long?,
    val reminderTime: Long?,
)