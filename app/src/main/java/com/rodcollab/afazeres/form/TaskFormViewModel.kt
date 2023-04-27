package com.rodcollab.afazeres.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodcollab.afazeres.core.repository.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskFormViewModel @Inject constructor(private val repository: TasksRepository) : ViewModel() {

    private val uiState: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(UiState(dateField = "", timeField = "", alarmActive = false, reminderTime = ""))
    }

    fun stateOnceAndStream(): LiveData<UiState> {
        return uiState
    }

    data class UiState(
        val dateField: String,
        val timeField: String,
        val alarmActive: Boolean,
        val reminderTime: String
    )

    fun alarmStatus(alarmActive: Boolean) {
        viewModelScope.launch {
            uiState.postValue(
                uiState.value?.let {
                    UiState(
                        dateField = it.dateField,
                        timeField = it.timeField,
                        alarmActive = alarmActive,
                        reminderTime = it.reminderTime
                    )
                }
            )
        }

    }

    fun reminderTime(reminderTime: String) {
        viewModelScope.launch {
            uiState.value?.let { currentUiState ->
                uiState.value = currentUiState.copy(reminderTime = reminderTime)
            }
        }
    }

    fun addForm(
        taskTitle: String,
        taskCategory: String,
        taskDate: String,
        taskTime: Long?,
        alarmActive: Boolean,
        reminderTime: Long?
    ) {

        viewModelScope.launch {
            repository.add(
                taskTitle = taskTitle,
                taskCategory = taskCategory,
                taskDate = taskDate,
                taskTime = taskTime,
                alarmActive = alarmActive,
                reminderTime = reminderTime
            )
        }

    }

    fun onResume() {
        viewModelScope.launch {
            uiState.postValue(
                uiState.value?.let {
                    UiState(
                        dateField = it.dateField,
                        timeField = it.timeField,
                        alarmActive = it.alarmActive,
                        reminderTime = it.reminderTime
                    )
                }
            )
        }
    }
}