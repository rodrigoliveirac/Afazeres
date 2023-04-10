package com.rodcollab.afazeres.collections

import android.annotation.SuppressLint
import androidx.lifecycle.*
import com.rodcollab.afazeres.collections.domain.GetCompletedTasksUseCase
import com.rodcollab.afazeres.collections.domain.GetUncompletedTasksUseCase
import com.rodcollab.afazeres.collections.domain.OnToggleTaskCompletedUseCase
import com.rodcollab.afazeres.core.repository.TasksRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TaskListViewModel(
    private val repository: TasksRepository,
    private val onToggleTaskCompletedUseCase: OnToggleTaskCompletedUseCase,
    private val getCompletedTasksUseCase: GetCompletedTasksUseCase,
    private val getUncompletedTasksUseCase: GetUncompletedTasksUseCase
) : ViewModel() {


    private var getTasksJob: Job? = null

    private fun getTasks() {

        getTasksJob?.cancel()

        getCompletedTasks()

        getUncompletedTasks()
    }

    private fun getUncompletedTasks() {
        getTasksJob = getUncompletedTasksUseCase()
            .onEach { tasks ->
                uiState.value?.let { currentState ->
                    uiState.value =
                        currentState.copy(uncompletedTasks = tasks.filter { it.date == currentState.date })
                }
            }.launchIn(viewModelScope)
    }

    private fun getCompletedTasks() {
        getTasksJob = getCompletedTasksUseCase()
            .onEach { tasks ->
                uiState.value?.let { currentState ->
                    uiState.value =
                        currentState.copy(completedTasks = tasks.filter { it.date == currentState.date })
                }
            }.launchIn(viewModelScope)
    }

    @SuppressLint("SimpleDateFormat")
    var sdformat = SimpleDateFormat("MMM dd, yyyy")
    private val uiState: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(
            UiState(
                uncompletedTasks = emptyList(),
                completedTasks = emptyList(),
                date = sdformat.format(Calendar.getInstance().time)
            )
        )
    }

    fun stateOnceAndStream(): LiveData<UiState> {
        return uiState
    }

    fun onResume() {
        getTasks()
    }

    fun toggleTaskCompleted(id: String) {
        viewModelScope.launch {
            onToggleTaskCompletedUseCase(id)
        }
    }

    fun addForm(
        name: String,
        category: String,
        habitDate: String
    ) {
        viewModelScope.launch {
            repository.add(name, category, habitDate)
        }
    }

    fun changeDate(newValue: String): String {
        viewModelScope.launch {
            uiState.value?.let { currentUiState ->
                uiState.value = currentUiState.copy(date = newValue)
            }
        }
        getTasks()
        return newValue
    }

    data class UiState(
        val completedTasks: List<TaskItem>,
        val uncompletedTasks: List<TaskItem>,
        val date: String
    )

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val repository: TasksRepository,
        private val onToggleTaskCompletedUseCase: OnToggleTaskCompletedUseCase,
        private val getCompletedTasksUseCase: GetCompletedTasksUseCase,
        private val getUncompletedTasksUseCase: GetUncompletedTasksUseCase
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TaskListViewModel(
                repository,
                onToggleTaskCompletedUseCase,
                getCompletedTasksUseCase,
                getUncompletedTasksUseCase
            ) as T
        }
    }
}
