package com.rodcollab.afazeres.util.validators

import android.util.Patterns

object EmailValidator {
    fun getMessageAccordingToTheError(email: String?) = when {
        email == null || email.isBlank() -> "Email cannot be empty"
        email.length < 5 -> "Email cannot be this short"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email"
        else -> null
    }
}