package com.rodcollab.afazeres.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.rodcollab.afazeres.collections.domain.GetTasksWithAlarmUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : Service() {

    @Inject
    lateinit var getTasksWithAlarmUseCase: GetTasksWithAlarmUseCase

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO + Job()).launch {
            getTasksWithAlarmUseCase().collectLatest { tasks ->
                Log.d("tasksWithAlarm", tasks.filter { it.date == "28/04/2023" }.toString())
            }
        }

    }
}