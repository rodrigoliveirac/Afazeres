package com.rodcollab.afazeres.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rodcollab.afazeres.core.repository.TasksRepository
import kotlinx.coroutines.launch

class TaskFormViewModel(private val repository: TasksRepository) : ViewModel() {


    fun addForm(taskTitle: String, taskCategory: String, taskDate: String) {

        viewModelScope.launch {
            repository.add(taskTitle = taskTitle, taskCategory = taskCategory, taskDate = taskDate)
        }
    }


    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val repository: TasksRepository,
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TaskFormViewModel(
                repository,
            ) as T
        }
    }
}