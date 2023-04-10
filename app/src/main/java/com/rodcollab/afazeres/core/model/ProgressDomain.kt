package com.rodcollab.afazeres.core.model

import java.time.DayOfWeek

data class ProgressDomain(
    val id: String,
    val habitId: String,
    val dayOfWeek: Int
)
