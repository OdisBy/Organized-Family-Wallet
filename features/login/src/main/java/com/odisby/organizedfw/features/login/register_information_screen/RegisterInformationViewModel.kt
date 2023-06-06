package com.odisby.organizedfw.features.login.register_information_screen

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odisby.organizedfw.core.data.repository.AuthRepository
import com.odisby.organizedfw.core.data.repository.AvatarRepository
import com.odisby.organizedfw.core.data.util.AuthResult
import com.odisby.organizedfw.core.data.util.UiStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterInformationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val avatarRepository: AvatarRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiStateFlow<UiState>>(UiStateFlow.Empty())
    val uiState: StateFlow<UiStateFlow<UiState>> = _uiState

    private val _updateState = MutableStateFlow<AuthResult>(AuthResult.Empty)
    val updateState: StateFlow<AuthResult> = _updateState

    data class UiState(
        val username: String?,
        val profileAvatar: Bitmap?,
        val groupInvite: String?
    )

    fun getUiState() = viewModelScope.launch {
        _uiState.value = UiStateFlow.Loading()
        try{
            _uiState.value = UiStateFlow.Success(
                UiState(
                    username = null,
                    profileAvatar = null,
                    groupInvite = null
                ))
        } catch (e: Exception){
            _uiState.value = UiStateFlow.Error(e.message.toString())
        }
    }

    fun updateAvatarView(bitmap: Bitmap){
        _uiState.value = UiStateFlow.Success(
            UiState(
                username = uiState.value.data?.username,
                profileAvatar = bitmap,
                groupInvite = uiState.value.data?.groupInvite
            ))
    }

    fun createUser(uiData: UiState) = viewModelScope.launch {
        _updateState.value = AuthResult.Loading
        try{
            authRepository.createUserAndAddToGroup(uiData.username!!, uiData.groupInvite, uiData.profileAvatar!!)
            _updateState.value = AuthResult.Success
        } catch (e: Exception) {
            Log.w(TAG, "Error on create user ${e.message.toString()}")
            _updateState.value = AuthResult.Error(e.message.toString())
        }
    }

    fun generateAvatar(username: String) = viewModelScope.launch {
        Log.d(TAG, "Generating avatar")
        val bitmap = avatarRepository.generateAvatar(username)
        _uiState.value = UiStateFlow.Success(
            UiState(
                username = uiState.value.data?.username,
                profileAvatar = bitmap,
                groupInvite = uiState.value.data?.groupInvite
            )
        )
    }

    companion object{
        const val TAG = "RegisterInfoViewModel"
    }
}