package com.rodcollab.afazeres.collections

import android.util.Log
import com.rodcollab.afazeres.core.repository.HabitRepository
import com.rodcollab.afazeres.core.repository.ProgressRepository
import java.util.*

class GetHabitsForTodayUseCaseImpl(
    private val habitRepository: HabitRepository,
    private val progressRepository: ProgressRepository
) : GetHabitsForTodayUseCase {
    override suspend fun invoke(): List<HabitItem> {
        // Passar o dia da semana
        val today = Calendar.getInstance()
        val dayOfWeek = today.get(Calendar.DAY_OF_WEEK)

        Log.d(TAG, "Fetching all habits for $dayOfWeek")

        return habitRepository
            .fetch(dayOfWeek)
            .map { habit ->
                Log.d(TAG, "Check we have already work on ${habit.id} at ${today.timeInMillis}")

                val progress = progressRepository.fetch(habit.id, today.timeInMillis)
                val isCompletedToday =  progress.isNotEmpty()

                Log.d(TAG, "Habit for today: ${habit.title} is completed: $isCompletedToday")

                HabitItem(
                    id =  habit.id,
                    title = habit.title,
                    category = habit.category,
                    isCompleted = isCompletedToday,
                    date = habit.date
                )
            }
    }

    companion object {
        private const val TAG = "GetHabitsForToday"
    }
}