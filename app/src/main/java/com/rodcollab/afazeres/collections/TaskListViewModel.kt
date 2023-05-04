package com.rodcollab.afazeres.collections

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.*
import com.rodcollab.afazeres.collections.domain.*
import com.rodcollab.afazeres.collections.model.TaskItem
import com.rodcollab.afazeres.receiver.AlarmReceiver
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
class TaskListViewModel @Inject constructor(
    private val app: Application,
    private val getTasksWithAlarmUseCase: GetTasksWithAlarmUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val onToggleTaskCompletedUseCase: OnToggleTaskCompletedUseCase,
    private val getCompletedTasksUseCase: GetCompletedTasksUseCase,
    private val getUncompletedTasksUseCase: GetUncompletedTasksUseCase
) : AndroidViewModel(app) {

    private var getTasksJob: Job? = null

    private lateinit var alarmManager: AlarmManager

    private var alarmIntent = Intent(app, AlarmReceiver::class.java)

    private lateinit var pendingIntent: PendingIntent

    @SuppressLint("SimpleDateFormat")
    var simpleDateFormat = SimpleDateFormat("MMM dd, yyyy")
    private val uiState: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(
            UiState(
                uncompletedTasks = emptyList(),
                completedTasks = emptyList(),
                date = simpleDateFormat.format(Calendar.getInstance().time),
                dateToGetTasks = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now())
                    .toString()
            )
        )

    }

    private fun toString(reminderTimeText: Long): String {
        return when (reminderTimeText) {
            3600000L -> "1 hour from now"
            1800000L -> "30 min from now"
            900000L -> "15 min from now"
            else -> {
                ""
            }
        }
    }

    fun stateOnceAndStream(): LiveData<UiState> {
        return uiState
    }

    private fun getTasks() {

        getUncompletedTasks()
        getCompletedTasks()

        getTasksWithAlarm()

    }

    private fun getUncompletedTasks() {
        getTasksJob =
            getUncompletedTasksUseCase(uiState.value?.dateToGetTasks.toString()).onEach { uncompletedTasks ->
                uiState.value?.let { currentState ->
                    uiState.value =
                        currentState.copy(uncompletedTasks = uncompletedTasks)
                }
                Log.d("uncompleted_tasks", uiState.value?.uncompletedTasks.toString())
            }.launchIn(viewModelScope)
    }

    private fun getTasksWithAlarm() {

        getTasksJob =
            getTasksWithAlarmUseCase(uiState.value?.dateToGetTasks.toString()).onEach { tasks ->

                tasks.onEach { task ->

                    alarmIntent.putExtra("title", task.title)
                    alarmIntent.putExtra("category", task.category)
                    alarmIntent.putExtra("reminder_time", toString(task.reminderTime!!))

                    alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    pendingIntent = PendingIntent.getBroadcast(
                        app,
                        task.hashCode(),
                        alarmIntent,
                        PendingIntent.FLAG_MUTABLE
                    )
                    task.triggerTime?.let { triggerTime ->
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            pendingIntent
                        )
                    }

                }
            }.launchIn(viewModelScope)
    }

    private fun getCompletedTasks() {
        getTasksJob = getCompletedTasksUseCase(uiState.value?.dateToGetTasks.toString())
            .onEach { completedTasks ->
                uiState.value?.let { currentState ->
                    uiState.value =
                        currentState.copy(completedTasks = completedTasks)
                }
            }.launchIn(viewModelScope)
    }

    fun onResume() {

        getTasks()
        Log.d("header_date", uiState.value?.dateToGetTasks.toString())
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
                uiState.value = currentUiState.copy(
                    date = getStringFrom(value),
                    dateToGetTasks = getLocalDateFrom(value)
                )
            }
            getTasks()
        }

    }

    private fun getLocalDateFrom(value: Long): String {
        val date = getDate(value)
        val day = (date.get(Calendar.DAY_OF_MONTH)).toString().padStart(2, '0')
        val month = (date.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val year = date.get(Calendar.YEAR).toString()
        return "$day/$month/$year"
    }

    private fun getStringFrom(value: Long): String {
        val calendar = getDate(value)
        val date = calendar.time
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy").withZone(ZoneId.systemDefault())
        return DateTimeFormatter.ofPattern("MMM dd, yyyy").format(
            LocalDate.parse(formatter.format(date.toInstant()).toString(), formatter)
        ).toString()
    }

    private fun getDate(value: Long): Calendar {
        val calendarInstance = Calendar.getInstance()
        calendarInstance.timeInMillis = value
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
