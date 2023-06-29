package com.ruliam.organizedfw.features.group

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruliam.organizedfw.core.data.model.GroupUsersDomain
import com.ruliam.organizedfw.core.data.repository.AuthRepository
import com.ruliam.organizedfw.core.data.repository.GroupRepository
import com.ruliam.organizedfw.core.data.util.UiStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupPageViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {
    private val _signInState = MutableStateFlow(false)
    val signInState: StateFlow<Boolean> = _signInState

    private val _uiState = MutableStateFlow<UiStateFlow<UiState>>(UiStateFlow.Empty())
    val uiState: StateFlow<UiStateFlow<UiState>> = _uiState

    private var shouldOpenDialog: Boolean = false


    fun isLogged() = viewModelScope.launch {
        _signInState.value = authRepository.checkLogin()
    }


    private suspend fun getGroupId() : String {
        val inviteCode = state.get<String>("groupID")
        Log.d(TAG, "Invite code ${inviteCode ?: "none"}")

        val groupId = groupRepository.getGroupInviteCode()

        if (inviteCode != null) {
            if(inviteCode.isNotEmpty()){
                return if(inviteCode == groupId){
                    Log.d(TAG, "User tried to enter in the same group with a invite code")
                    groupId
                } else{
                    Log.d(TAG, "User trying to enter in a new group")
                    shouldOpenDialog = true
                    groupId
                }
            }
        }

        return groupId
    }

    fun getUiState() = viewModelScope.launch {
        _uiState.value = UiStateFlow.Loading()

        try{
            val inviteCode = getGroupId()
            val users = groupRepository.getUsers()
            _uiState.value = UiStateFlow.Success(
                UiState(
                    inviteCode,
                    users
                )
            )
        } catch (e: Exception){
            Log.w(TAG, "Exception on getUiState")
            _uiState.value = UiStateFlow.Error(e.message.toString())
        }
    }

    fun askForEnterGroup() = viewModelScope.launch {
        val inviteCode = state.get<String>("groupID")
        groupRepository.askEnterGroup(inviteCode!!)
    }

    fun checkGroupId(): Boolean {
        return shouldOpenDialog
    }


    data class UiState(
        val groupInviteCode: String,
        val usersList: List<GroupUsersDomain?>
    )

    companion object {
        const val TAG = "GroupPageViewModel"
    }
}