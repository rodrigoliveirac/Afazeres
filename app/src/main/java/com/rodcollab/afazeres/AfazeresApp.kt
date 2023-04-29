package com.rodcollab.afazeres

import android.app.Application
import android.content.Intent
import com.rodcollab.afazeres.service.NotificationService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AfazeresApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startService(Intent(this, NotificationService::class.java))
    }
}