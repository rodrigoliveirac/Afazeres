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
        MutableLiveData<UiState>(
            UiState(
                datePicked = 0L,
                timePicked = 0L,
                alarmActive = false,
                reminderTime = 3600000L
            )
        )
    }

    fun stateOnceAndStream(): LiveData<UiState> {
        return uiState
    }

    data class UiState(
        val datePicked: Long?,
        val timePicked: Long,
        val alarmActive: Boolean,
        val reminderTime: Long
    )

    fun alarmStatus(alarmActive: Boolean) {
        viewModelScope.launch {
            uiState.postValue(
                uiState.value?.let {
                    UiState(
                        datePicked = it.datePicked,
                        timePicked = it.timePicked,
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
                uiState.value = currentUiState.copy(reminderTime = toLong(reminderTime))
            }
        }
    }

    private fun toLong(reminderTimeText: String): Long {
        return when (reminderTimeText) {
            "1 hour before" -> 3600000L
            "30 min before" -> 1800000L
            "15 min before" -> 900000L
            else -> {
                0L
            }
        }
    }

    fun addForm(
        taskTitle: String,
        taskCategory: String,
        taskDate: Long?,
        taskTime: String?,
        alarmActive: Boolean,
        reminderTime: Long
    ) {

        viewModelScope.launch {

            val triggerTime = if(taskTime.toString() != "") getTriggerTime(
                taskDate,
                getValueTimeInLong(taskTime.toString()),
                reminderTime
            ) else null

            repository.add(
                taskTitle = taskTitle,
                taskCategory = taskCategory,
                taskDate = taskDate,
                taskTime = taskTime,
                alarmActive = alarmActive,
                reminderTime = reminderTime,
                triggerTime = triggerTime
            )
        }

    }

    private fun getValueTimeInLong(textFromView: CharSequence): Long {

        val hourIndex = 0
        return textFromView.toString().split(":").mapIndexed { hour, text ->
            when (hour) {
                hourIndex -> text.toLong() * 3600000L
                else -> {
                    text.toLong() * 60000L
                }
            }
        }.sumOf { totalTime ->
            totalTime
        }

    }

    private fun getTriggerTime(
        taskDate: Long?,
        taskTime: Long?,
        reminderTime: Long?
    ): Long? {

        val time = reminderTime?.let { minutesBefore -> taskTime?.minus(minutesBefore) }
        return time?.let { taskDate?.plus(it) }

    }

    fun onResume() {
        viewModelScope.launch {
            uiState.postValue(
                uiState.value?.let {
                    UiState(
                        datePicked = it.datePicked,
                        timePicked = it.timePicked,
                        alarmActive = it.alarmActive,
                        reminderTime = it.reminderTime
                    )
                }
            )
        }
    }

    fun updateDatePicked(datePicked: Long?) {
        viewModelScope.launch {
            uiState.value?.let {
                uiState.value = it.copy(datePicked = datePicked)
            }
        }
    }
}