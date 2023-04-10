package com.rodcollab.afazeres.collections

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class TaskListObserver(private val viewModel: TaskListViewModel) : DefaultLifecycleObserver {
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        viewModel.onResume()
    }
}