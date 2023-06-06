package com.odisby.organizedfw

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odisby.organizedfw.features.login.login_screen.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

//    init {
//        viewModelScope.launch {
//            _isLoading.value = false
//            Log.d("MainViewModel", "isLoading = false")
//
//        }
//    }
//    private fun isLogged(): Boolean {
//        return if(firebaseAuth.currentUser != null){
//            sharedPreferences.getBoolean(IS_LOGGED, false)
//        } else{
//            logoutUser()
//            false
//        }
//    }
}