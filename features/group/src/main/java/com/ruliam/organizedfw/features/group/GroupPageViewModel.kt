package com.ruliam.organizedfw.features.group

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruliam.organizedfw.core.data.model.GroupUserDomain
import com.ruliam.organizedfw.core.data.repository.AuthRepository
import com.ruliam.organizedfw.core.data.repository.GroupRepository
import com.ruliam.organizedfw.core.data.util.RequestPendingResult
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

    private val _dialogState = MutableStateFlow<DialogModel>(DialogModel.Empty)
    val dialogState: StateFlow<DialogModel> = _dialogState

    fun isLogged() = viewModelScope.launch {
        _signInState.value = authRepository.checkLogin()
    }
    fun checkNewInviteCode() = viewModelScope.launch {
        val inviteCode = state.get<String>("groupInviteCode")
        Log.d(TAG, "check new invite code $inviteCode")
        if (inviteCode != null) {
            if (inviteCode.isNotEmpty()) {
                val groupInviteCode = groupRepository.getGroupInviteCode()

                if (inviteCode == groupInviteCode) {
                    Log.d(TAG, "User tried to enter in the same group with a invite code")
                } else {
                    Log.d(TAG, "User trying to enter in a new group")
                    _dialogState.value = DialogModel.NewGroup
                }
            }
        }
    }

    fun askForEnterGroup() = viewModelScope.launch {
        val inviteCode = state.get<String>("groupInviteCode")
        val requestGroup = groupRepository.askEnterGroup(inviteCode!!, false)
        Log.d(TAG, requestGroup.toString())

        when(requestGroup){
            is RequestPendingResult.Success -> {
                _dialogState.value = DialogModel.Success
            }
            is RequestPendingResult.alreadyRequestForAnotherGroup -> {
                _dialogState.value = DialogModel.PendingAnotherGroup
            }
            is RequestPendingResult.alreadyPendingUserInThisGroup -> {
                _dialogState.value = DialogModel.AlreadyPendingUserInThisGroup
            }
            is RequestPendingResult.groupDoesNotExist -> {
                _dialogState.value = DialogModel.DoesNotExistGroup
            }
            is RequestPendingResult.Error -> {
                Log.w(TAG, "Error ${requestGroup.message}")
            }
            is RequestPendingResult.userAlreadyInThisGroup -> {
                return@launch
            }
            else -> Unit
        }
    }

    fun getUiState() = viewModelScope.launch {
        _uiState.value = UiStateFlow.Loading()

        try{
            val inviteCode = groupRepository.getGroupInviteCode()
            val users = groupRepository.getUsers()
            val pendingUsers = groupRepository.checkIfPendingUsers()
            _uiState.value = UiStateFlow.Success(
                UiState(
                    inviteCode,
                    users,
                    pendingUsers
                )
            )
        } catch (e: Exception){
            e.stackTrace
            Log.w(TAG, "Exception on getUiState: ${e.message}")
            _uiState.value = UiStateFlow.Error(e.message.toString())
        }
    }

    fun onUserClick(user: GroupUserDomain) {
        _dialogState.value = DialogModel.BindUser(user)
    }

    fun emptyDialog() {
        _dialogState.value = DialogModel.Empty
    }

    fun addPendingUser(user: GroupUserDomain) = viewModelScope.launch {
        groupRepository.addPendingUserToGroup(user)
        getUiState()
    }

    fun deniedPendingUser(user: GroupUserDomain) = viewModelScope.launch {
        groupRepository.deniedPendingUser(user)
        getUiState()
    }

    fun changePendingGroup() = viewModelScope.launch {
        val inviteCode = state.get<String>("groupInviteCode")
        groupRepository.askEnterGroup(inviteCode!!, ignoreCurrentPending = true)
    }


    data class UiState(
        val groupInviteCode: String,
        val usersList: List<GroupUserDomain>,
        val pendingUsers: List<GroupUserDomain?>
    )

    sealed class DialogModel {
        data class BindUser(val user: GroupUserDomain) : DialogModel()
        object NewGroup : DialogModel()
        object Empty : DialogModel()
        object PendingAnotherGroup : DialogModel()
        object Success : DialogModel()
        object AlreadyPendingUserInThisGroup : DialogModel()

        object DoesNotExistGroup : DialogModel()
    }

    companion object {
        const val TAG = "GroupPageViewModel"
    }
}