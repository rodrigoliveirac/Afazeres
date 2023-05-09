package com.rodcollab.afazeres.auth.signup

import androidx.lifecycle.*
import com.rodcollab.afazeres.util.getEmailPrefix
import com.rodcollab.afazeres.util.validators.*
import kotlinx.coroutines.launch

class SignUpViewModel() : ViewModel() {

    val email by lazy { MutableLiveData<String>() }
    val password by lazy { MutableLiveData<String>() }
    val repeatPassword by lazy { MutableLiveData<String>() }
    val emailError by lazy { MutableLiveData<String>() }
    val passwordError by lazy { MutableLiveData<String>() }
    val repeatPasswordError by lazy { MutableLiveData<String>() }
    private val username: LiveData<String> = Transformations.map(email, ::generateUsername)

    private val _emailIsValid by lazy { MutableLiveData(true) }
    var emailIsValid: LiveData<Boolean> = _emailIsValid

    private val _passwordIsValid by lazy { MutableLiveData(true) }
    var passwordIsValid: LiveData<Boolean> = _passwordIsValid

    private val _repeatPasswordIsValid by lazy { MutableLiveData(true) }
    var repeatPasswordIsValid: LiveData<Boolean> = _repeatPasswordIsValid

    private val allFieldsIsValid by lazy { MutableLiveData(false) }

    val enableRegistration: LiveData<Boolean> by lazy {
        MediatorLiveData<Boolean>().apply {
            addSources(email, password, repeatPassword) {
                value = allFieldsIsValid.value
            }
        }
    }

    private val _showRegistrationSuccessDialog by lazy { MutableLiveData(false) }
    val showRegistrationSuccessDialog: LiveData<Boolean>
        get() = _showRegistrationSuccessDialog

    fun showEmailError() {
        emailError.value = EmailValidator.getMessageAccordingToTheError(email.value)
    }

    fun showPasswordError() {
        passwordError.value = PasswordValidator.getMessageAccordingToTheError(password.value)
    }

    fun showRepeatPasswordError() {
        repeatPasswordError.value =
            RepeatPasswordValidator.getMessageAccordingToTheError(
                password.value,
                repeatPassword.value
            )
    }

    fun onPasswordChanged(newValue: String) {
        password.value = newValue
        _passwordIsValid.value = newValue.isValidPassword()
    }

    fun onEmailChanged(newValue: String) {
        email.value = newValue
        _emailIsValid.value = newValue.isValidEmail()
    }

    fun onRepeatPasswordChanged(newValue: String) {
        repeatPassword.value = newValue
        _repeatPasswordIsValid.value = newValue.passwordMatches(password.value.toString()).apply {
            allFieldsIsValid.value = this
        }
    }

    fun onSignupButtonClick() {
        if (emailIsValid.value == true && passwordIsValid.value == true && repeatPasswordIsValid.value == true) {
            viewModelScope.launch {
//                accountService.register(
//                    userName = username.value.toString(),
//                    email = email.value.toString(),
//                    password = password.value.toString()
//                ) { isSuccessful ->
//                    if (isSuccessful) {
//                        _showRegistrationSuccessDialog.value = true
//                    }
//                }
            }

        } else {
            emailError.value = EmailValidator.getMessageAccordingToTheError(password.value)
            passwordError.value = PasswordValidator.getMessageAccordingToTheError(password.value)
            repeatPasswordError.value = RepeatPasswordValidator.getMessageAccordingToTheError(
                password.value,
                repeatPassword.value
            )
        }
    }


    private fun generateUsername(email: String): String {
        val prefix = getEmailPrefix(email)
        val suffix = prefix.length
        return "$prefix$suffix".lowercase()
    }

    private fun <T> MediatorLiveData<T>.addSources(
        vararg sources: LiveData<out Any>,
        onChanged: Observer<Any>
    ) {
        sources.forEach { source ->
            addSource(source, onChanged)
        }
    }
}