package com.rodcollab.afazeres.form

data class FormUiState(
    val datePicked: Long?,
    val timePicked: Long,
    val alarmActive: Boolean,
    val reminderTime: Long
)