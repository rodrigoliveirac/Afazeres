package com.rodcollab.afazeres.core.repository

import com.rodcollab.afazeres.core.model.ProgressDomain

interface ProgressRepository {

    /**
     *
     * @param habitId ID of the specific Habit
     * @param completedAt time in millis when this habit was completed
     */
    suspend fun fetch(habitId: String, completedAt: Long) : List<ProgressDomain>

    /**
     *
     * @param id ID of the progress
     */
    suspend fun delete(id: String)

    /**
     * @param habitId ID of the specific habit that we make progress
     */
    suspend fun add(habitId: String)
}