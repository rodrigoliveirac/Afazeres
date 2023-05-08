package com.rodcollab.afazeres.collections.ui.model

import com.rodcollab.afazeres.collections.model.TaskItem

data class UiState(
    val completedTasks: List<TaskItem>,
    val uncompletedTasks: List<TaskItem>,
    val currentDateSelectedTextView: String,
    val dateToFetchTasks: String,
)
