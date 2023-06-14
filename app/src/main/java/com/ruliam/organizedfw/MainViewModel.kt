package com.ruliam.organizedfw

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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