package com.rodcollab.afazeres.collections.ui

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rodcollab.afazeres.collections.domain.*
import com.rodcollab.afazeres.collections.model.TaskItem
import com.rodcollab.afazeres.collections.ui.model.UiState
import com.rodcollab.afazeres.receiver.AlarmReceiver
import com.rodcollab.afazeres.util.DateUtil
import com.rodcollab.afazeres.util.TextUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val app: Application,
    private val getTasksWithAlarmUseCase: GetTasksWithAlarmUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val onToggleTaskCompletedUseCase: OnToggleTaskCompletedUseCase,
    private val getCompletedTasksUseCase: GetCompletedTasksUseCase,
    private val getUncompletedTasksUseCase: GetUncompletedTasksUseCase
) : AndroidViewModel(app) {

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private var alarmIntent = Intent(app, AlarmReceiver::class.java)
    private var getTasksWithAlarmJob: Job? = null

    private val uiState: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(
            UiState(
                uncompletedTasks = emptyList(),
                completedTasks = emptyList(),
                currentDateSelectedTextView = currentDateSelectedTextView(),
                dateToFetchTasks = todayByDefault()
            )
        )

    }
    private fun currentDateSelectedTextView() = DateUtil.simpleDateFormat()

    private fun todayByDefault() = DateUtil.dateTimeFormatter()

    fun stateOnceAndStream(): LiveData<UiState> {
        return uiState
    }

    fun onResume() {
        refreshTasks()
        tasksWithAlarm()
        Log.d("header_date", uiState.value?.dateToFetchTasks.toString())
    }

    private fun refreshTasks() {
        viewModelScope.launch {
            uiState.value?.let { state ->
                uiState.postValue(
                    UiState(
                        uncompletedTasks = getUncompletedTasksUseCase(state.dateToFetchTasks),
                        completedTasks = getCompletedTasksUseCase(state.dateToFetchTasks),
                        dateToFetchTasks = state.dateToFetchTasks,
                        currentDateSelectedTextView = state.currentDateSelectedTextView
                    )
                )
                Log.d("uncompleted_tasks", state.uncompletedTasks.toString())
            }
        }
    }

    private fun tasksWithAlarm() {
        getTasksWithAlarmJob?.cancel()
        getTasksWithAlarm()

    }

    private fun getTasksWithAlarm() {
        getTasksWithAlarmJob = getTasksWithAlarmUseCase().onEach { tasks ->

            tasks.onEach { task ->
                setupAlarm(task)
            }
        }.launchIn(viewModelScope)
    }

    private fun setupAlarm(task: TaskItem) {
        alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmIntent.putExtra("title", task.title)
        alarmIntent.putExtra("category", task.category)
        alarmIntent.putExtra("reminder_time", TextUtil.toString(app.resources, task.reminderTime!!))

        pendingIntent = PendingIntent.getBroadcast(
            app,
            task.hashCode(),
            alarmIntent,
            PendingIntent.FLAG_ONE_SHOT
        )


        task.triggerTime?.let { triggerTime ->
            setAlarmOrNot(triggerTime)
        }
    }

    private fun setAlarmOrNot(triggerTime: Long) {
        if (triggerTime < System.currentTimeMillis()) {
            alarmManager.cancel(pendingIntent)
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun toggleTaskCompleted(id: String, isCompleted: Boolean) {
        viewModelScope.launch {
            val checkedInt = if (!isCompleted) 1 else 0
            onToggleTaskCompletedUseCase(id, checkedInt)
            withContext(Dispatchers.Main) {
                refreshTasks()
            }
        }

    }

    fun changeDate(value: Long) {
        viewModelScope.launch {
            uiState.postValue(
                UiState(
                    uncompletedTasks = getUncompletedTasksUseCase(dateToFetchTasks(value)),
                    completedTasks = getCompletedTasksUseCase(dateToFetchTasks(value)),
                    dateToFetchTasks = dateToFetchTasks(value),
                    currentDateSelectedTextView = headerTextViewDate(value)
                )
            )
        }
    }

    private fun headerTextViewDate(value: Long) = DateUtil.getStringFrom(value)

    private fun dateToFetchTasks(value: Long) = DateUtil.getDateInMillisFrom(value)

    fun deleteTask(id: String) {
        viewModelScope.launch {
            deleteTaskUseCase(id)
            withContext(Dispatchers.Main) {
                refreshTasks()
            }
        }
    }
}
