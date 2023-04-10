package com.rodcollab.afazeres.dummy

import android.annotation.SuppressLint
import com.rodcollab.afazeres.collections.HabitItem
import com.rodcollab.afazeres.core.repository.HabitsRepository
import java.util.*

object MockHabits : HabitsRepository {

    private val habitItemList: MutableList<HabitItem> = mutableListOf()

    override fun fetchHabits() = habitItemList.map { it.copy() }

    override fun toggleHabitCompleted(id: String) {
        val habitIndex = findHabitIndexById(id)
        val habit = habitItemList[habitIndex]
        habitItemList[habitIndex] = habit.copy(isCompleted = !habit.isCompleted)
    }

    @SuppressLint("SimpleDateFormat")
    override fun addHabit(name: String, category: String, habitDate: String) {
        habitItemList.add(
            HabitItem(
                id = UUID.randomUUID().toString(),
                title = name,
                isCompleted = false,
                date = habitDate,
                category = category
            )
        )
    }

    private fun findHabitIndexById(id: String) = habitItemList.indexOfFirst { habitItem ->
        habitItem.id == id
    }
}
