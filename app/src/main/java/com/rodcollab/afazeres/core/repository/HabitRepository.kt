package com.rodcollab.afazeres.core.repository

import com.rodcollab.afazeres.core.model.HabitDomain

interface HabitRepository {

    suspend fun fetchAll(): List<HabitDomain>

    suspend fun fetch(dayOfWeek: Int) : List<HabitDomain>

    suspend fun add(title: String, daysOfWeek: List<Int>)

}