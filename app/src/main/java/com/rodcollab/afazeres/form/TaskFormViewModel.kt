package com.rodcollab.afazeres.form

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rodcollab.afazeres.core.repository.TasksRepository
import com.rodcollab.afazeres.form.domain.GetCurrentUserIdUseCase
import com.rodcollab.afazeres.util.TextUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskFormViewModel @Inject constructor(
    private val app: Application,
    private val getCurrentUserId: GetCurrentUserIdUseCase,
    private val repository: TasksRepository
) : AndroidViewModel(app) {

    private val uiState: MutableLiveData<FormUiState> by lazy {
        MutableLiveData<FormUiState>(
            FormUiState(
                datePicked = 0L,
                timePicked = 0L,
                alarmActive = false,
                reminderTime = 3600000L
            )
        )
    }

    fun stateOnceAndStream(): LiveData<FormUiState> {
        return uiState
    }

    fun alarmStatus(alarmActive: Boolean) {
        viewModelScope.launch {
            uiState.postValue(
                uiState.value?.let {
                    FormUiState(
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
                uiState.value =
                    currentUiState.copy(reminderTime = TextUtil.toLong(app.resources, reminderTime))
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

            val triggerTime = if (taskTime.toString() != "") getTriggerTime(
                taskDate,
                TextUtil.getValueTimeInLong(taskTime.toString()),
                reminderTime
            ) else null

            repository.add(
                currentUserId = getCurrentUserId(),
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
                    FormUiState(
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