package com.rodcollab.afazeres.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodcollab.afazeres.core.repository.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskFormViewModel @Inject constructor(private val repository: TasksRepository) : ViewModel() {

    fun addForm(taskTitle: String, taskCategory: String, taskDate: String, taskTime: Long, alarmActive: Boolean, reminderTime:Long?) {

        viewModelScope.launch {
            repository.add(taskTitle = taskTitle, taskCategory = taskCategory, taskDate = taskDate, taskTime = taskTime, alarmActive = alarmActive, reminderTime = reminderTime)
        }
    }
}