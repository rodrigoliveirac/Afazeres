package com.rodcollab.afazeres.util.validators

object RepeatPasswordValidator {
    fun getMessageAccordingToTheError(password: String?, repeatPassword: String?) = when {
        repeatPassword != password -> "Password doesn't match"
        else -> null
    }
}