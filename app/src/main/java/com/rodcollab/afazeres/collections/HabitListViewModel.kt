package com.rodcollab.afazeres.collections

import android.annotation.SuppressLint
import androidx.lifecycle.*
import com.rodcollab.afazeres.core.repository.HabitsRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HabitListViewModel(private val repository: HabitsRepository) : ViewModel() {

    /**
     * Mutable Live Data that initialize with the current list of saved Habits.
     */

    @SuppressLint("SimpleDateFormat")
    var sdformat = SimpleDateFormat("MMM dd, yyyy")
    private val uiState: MutableLiveData<UiState> by lazy {
        MutableLiveData<UiState>(
            UiState(
                habitItemListIncomplete = repository.fetchHabits(),
                habitItemListCompleted = repository.fetchHabits(),
                date = sdformat.format(Calendar.getInstance().time)
            )
        )
    }


    /**
     * Expose the uiState as LiveData to UI.
     */
    fun stateOnceAndStream(): LiveData<UiState> {
        return uiState
    }

    /**
     * Toggle a Habit complete status.
     */
    fun toggleHabitCompleted(id: String) {
        repository.toggleHabitCompleted(id)
        refreshHabitList()
    }

    fun addHabit(
        name: String,
        category: String,
        habitDate: String
    ) {
        viewModelScope.launch {
            repository.addHabit(name, category, habitDate)
        }
        refreshHabitList()
    }

    private fun refreshHabitList() {
        viewModelScope.launch {
            uiState.value?.let { currentUiState ->
                uiState.value = currentUiState.copy(
                    habitItemListIncomplete = repository.fetchHabits()
                        .filter { it.date == currentUiState.date }.filter { !it.isCompleted }
                        .sortedBy { it.id },
                    habitItemListCompleted = repository.fetchHabits()
                        .filter { it.date == currentUiState.date }.filter { it.isCompleted }
                        .sortedBy { it.id }
                )
            }
        }
    }

    fun changeDate(newValue: String): String {
        viewModelScope.launch {
            uiState.value?.let { currentUiState ->
                uiState.value = currentUiState.copy(date = newValue)
            }
        }
        refreshHabitList()
        return newValue
    }

    /**
     * UI State containing every data needed to show Habits.
     */
    data class UiState(
        val habitItemListIncomplete: List<HabitItem>,
        val habitItemListCompleted: List<HabitItem>,
        val date: String
    )

    /**
     * ViewModel Factory needed to provide Repository injection to ViewModel.
     */
    @Suppress("UNCHECKED_CAST")
    class Factory(private val repository: HabitsRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HabitListViewModel(repository) as T
        }
    }
}
