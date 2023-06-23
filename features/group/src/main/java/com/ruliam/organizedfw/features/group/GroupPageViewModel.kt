package com.ruliam.organizedfw.features.group

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruliam.organizedfw.core.data.repository.AuthRepository
import com.ruliam.organizedfw.core.data.util.SignInResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupPageViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signInState = MutableStateFlow(false)
    val signInState: StateFlow<Boolean> = _signInState

    fun isLogged() = viewModelScope.launch {
        _signInState.value = authRepository.checkLogin()
    }

    suspend fun getGroupId(): String {
//        val groupId = state.get<String>("groupID")
        return "vazio"
    }
}