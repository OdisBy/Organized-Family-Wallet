package com.odisby.organizedfw.features.login.login_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odisby.organizedfw.core.data.repository.AuthRepository
import com.odisby.organizedfw.core.data.util.SignInResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _signInState = MutableStateFlow<SignInResult>(SignInResult.Empty)
    val signInState: StateFlow<SignInResult> = _signInState

    var startLogged = false

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        _signInState.value = SignInResult.Loading
        try {
            _signInState.value = authRepository.loginUserWithEmailAndPassword(email, password)
            Log.d(TAG, "SignInState.value = ${signInState.value}")
        } catch(e: Exception) {
            _signInState.value = SignInResult.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    fun createLoginSession(userId: String, groupId: String){
        authRepository.createLoginSession(userId, groupId)
    }

    fun isLogged() = viewModelScope.launch {
        startLogged = authRepository.checkLogin()
    }

    companion object{
        const val TAG = "LoginViewModel"
    }
}