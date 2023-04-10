package com.rodcollab.afazeres.collections

data class TaskItem(
    val id: String,
    val title: String,
    val category: String,
    val isCompleted: Boolean,
    var date: String,
)