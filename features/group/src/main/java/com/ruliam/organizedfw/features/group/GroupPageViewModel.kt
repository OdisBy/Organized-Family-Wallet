package com.ruliam.organizedfw.features.group

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.ruliam.organizedfw.core.data.repository.AuthRepository
import com.ruliam.organizedfw.core.data.repository.GroupRepository
import com.ruliam.organizedfw.core.data.repository.UsersRepository
import com.ruliam.organizedfw.core.data.util.SignInResult
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

    lateinit var groupId: String

    fun isLogged() = viewModelScope.launch {
        _signInState.value = authRepository.checkLogin()
    }


    suspend fun getGroupId() {
        val inviteCode = state.get<String>("groupID")
        Log.d(TAG, "Invite code ${inviteCode ?: "none"}")

        groupId = groupRepository.getGroupInviteCode()

        if (inviteCode != null) {
            if(inviteCode.isNotEmpty()){
                if(inviteCode == groupId){
                    Log.d(TAG, "User tried to enter in the same group with a invite code")
                    return
                } else{
                    Log.d(TAG, "User trying to enter in a new group")
                    return
                    //TODO
                }
            }
        }


    }


    companion object {
        const val TAG = "GroupPageViewModel"
    }
}