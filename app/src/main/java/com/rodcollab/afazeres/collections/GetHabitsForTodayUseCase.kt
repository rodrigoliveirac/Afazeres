package com.rodcollab.afazeres.collections

interface GetHabitsForTodayUseCase {

    suspend fun invoke(): List<HabitItem>
}