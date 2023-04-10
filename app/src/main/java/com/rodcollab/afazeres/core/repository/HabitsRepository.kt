package com.rodcollab.afazeres.core.repository

import com.rodcollab.afazeres.collections.HabitItem

interface HabitsRepository {

    fun fetchHabits(): List<HabitItem>

    fun toggleHabitCompleted(id: String)

    fun addHabit(name: String, category: String, habitDate: String)
}
