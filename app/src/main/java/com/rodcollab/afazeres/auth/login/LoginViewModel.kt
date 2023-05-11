package com.rodcollab.afazeres.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodcollab.afazeres.auth.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val accountService: AccountService) : ViewModel() {

    val email by lazy { MutableLiveData<String>() }
    val password by lazy { MutableLiveData<String>() }

    private val _navigateToMainScreen by lazy {
        MutableLiveData(
            LoginResult(
                navigate = false,
                msg = ""
            )
        )
    }
    val navigateToMainScreen: LiveData<LoginResult>
        get() = _navigateToMainScreen


    fun onLoginButtonClick() {
        viewModelScope.launch {
            accountService.signInWithEmailAndPassword(
                email = email.value.toString(),
                password = password.value.toString()
            ) { isSuccessful, msg ->
                _navigateToMainScreen.value?.let {
                    _navigateToMainScreen.value = it.copy(navigate = isSuccessful, msg = msg)
                }
            }
        }
    }
}