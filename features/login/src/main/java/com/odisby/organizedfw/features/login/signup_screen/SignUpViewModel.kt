package com.odisby.organizedfw.features.login.signup_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odisby.organizedfw.core.data.repository.AuthRepository
import com.odisby.organizedfw.core.data.util.SignUpResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _signInState = MutableStateFlow<SignUpResult>(SignUpResult.Empty)
    val signInState: StateFlow<SignUpResult> = _signInState

    fun registerUser(email: String, password: String) = viewModelScope.launch {
        _signInState.value = SignUpResult.Loading
        try{
            _signInState.value = authRepository.registerUserWithEmailAndPassword(email, password)
        } catch(e: Exception) {
            _signInState.value = SignUpResult.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    companion object{
        const val TAG = "LoginViewModel"
    }
}