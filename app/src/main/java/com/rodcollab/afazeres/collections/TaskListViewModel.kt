package com.rodcollab.afazeres.collections

import android.annotation.SuppressLint
import android.util.Log
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
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
                    currentState.copy(uncompletedTasks = tasks.filter { it.date ==  currentState.dateToGetTasks })
                Log.d("task_list", currentState.dateToGetTasks)
            }
        }.launchIn(viewModelScope)
    }

    private fun getCompletedTasks() {
        getTasksJob = getCompletedTasksUseCase()
            .onEach { tasks ->
                uiState.value?.let { currentState ->
                    uiState.value =
                        currentState.copy(completedTasks = tasks.filter { it.date == currentState.dateToGetTasks })
                }
            }.launchIn(viewModelScope)
    }

    @SuppressLint("SimpleDateFormat")
    var simpleDateFormat = SimpleDateFormat("MMM dd, yyyy")
    private val uiState: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(
            UiState(
                uncompletedTasks = emptyList(),
                completedTasks = emptyList(),
                date = simpleDateFormat.format(Calendar.getInstance().time),
                dateToGetTasks = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now()).toString()
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

    fun changeDate(value: Long) {
        viewModelScope.launch {
            uiState.value?.let { currentUiState ->
                uiState.value = currentUiState.copy(date = getStringFrom(value), dateToGetTasks = getLocalDateFrom(value))
            }
        }
        getTasks()
    }

    private fun getLocalDateFrom(value: Long): String {
        val date = getDate(value)
        val day = (date.get(Calendar.DAY_OF_MONTH) + 1).toString().padStart(2, '0')
        val month = (date.get(Calendar.MONTH) + 1).toString().padStart(2,'0')
        val year = date.get(Calendar.YEAR).toString()
        return "$day/$month/$year"
    }

    private fun getStringFrom(value:Long) : String {
        val calendar = getDate(value)
        val date = calendar.time
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy").withZone(ZoneId.systemDefault())
        return DateTimeFormatter.ofPattern("MMM dd, yyyy").format(LocalDate.parse(formatter.format(date.toInstant()).toString(), formatter).plusDays(1)).toString()
    }

    private fun getDate(value: Long) : Calendar {
        val date = Date()
        date.time = value
        val calendarInstance = Calendar.getInstance()
        calendarInstance.time = date
        return calendarInstance
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            deleteTaskUseCase(id)
        }
    }

    data class UiState(
        val completedTasks: List<TaskItem>,
        val uncompletedTasks: List<TaskItem>,
        val date: String,
        val dateToGetTasks: String,
    )
}
