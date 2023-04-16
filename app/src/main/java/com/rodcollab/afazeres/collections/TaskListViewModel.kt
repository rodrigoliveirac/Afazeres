package com.rodcollab.afazeres.collections

import android.annotation.SuppressLint
import androidx.lifecycle.*
import com.rodcollab.afazeres.collections.domain.DeleteTaskUseCase
import com.rodcollab.afazeres.collections.domain.GetCompletedTasksUseCase
import com.rodcollab.afazeres.collections.domain.GetUncompletedTasksUseCase
import com.rodcollab.afazeres.collections.domain.OnToggleTaskCompletedUseCase
import com.rodcollab.afazeres.collections.model.TaskItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor (
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val onToggleTaskCompletedUseCase: OnToggleTaskCompletedUseCase,
    private val getCompletedTasksUseCase: GetCompletedTasksUseCase,
    private val getUncompletedTasksUseCase: GetUncompletedTasksUseCase
) : ViewModel() {


    private var getTasksJob: Job? = null

    private fun getTasks() {

        getTasksJob?.cancel()

        getUncompletedTasks()
        getCompletedTasks()
    }

    private fun getUncompletedTasks() {
        getTasksJob = getUncompletedTasksUseCase().onEach { tasks ->
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
        viewModelScope.launch {
            getTasks()
        }
    }

    fun toggleTaskCompleted(id: String, isCompleted: Boolean) {
        viewModelScope.launch {
            val checkedInt = if (!isCompleted) 1 else 0
            onToggleTaskCompletedUseCase(id, checkedInt)
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

    fun deleteTask(id: String) {
        viewModelScope.launch {
            deleteTaskUseCase(id)
        }
    }

    data class UiState(
        val completedTasks: List<TaskItem>,
        val uncompletedTasks: List<TaskItem>,
        val date: String
    )
}
