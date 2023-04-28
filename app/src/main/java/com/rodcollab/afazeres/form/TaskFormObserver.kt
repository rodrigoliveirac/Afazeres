package com.rodcollab.afazeres.form

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.rodcollab.afazeres.databinding.FragmentTaskFormBinding

class TaskFormObserver(
    private val binding: FragmentTaskFormBinding?,
    private val viewModel: TaskFormViewModel
) : DefaultLifecycleObserver {
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        viewModel.onResume()
        if(binding != null) {
            viewModel.stateOnceAndStream().observe(owner) {
                if(it.alarmActive) {
                    binding.setAlarmSwitch.isChecked = true
                }
            }
        }
    }
}
