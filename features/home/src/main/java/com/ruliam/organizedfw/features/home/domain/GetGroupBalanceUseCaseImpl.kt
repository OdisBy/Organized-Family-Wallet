package com.ruliam.organizedfw.features.home.domain

import android.util.Log
import com.ruliam.organizedfw.core.data.repository.GroupRepository
import com.ruliam.organizedfw.core.data.repository.UsersRepository
import javax.inject.Inject

class GetGroupBalanceUseCaseImpl @Inject constructor(
    private val usersRepository: UsersRepository,
    private val groupRepository: GroupRepository
) : GetGroupBalanceUseCase {
    override suspend fun invoke(): Double {
        val user = usersRepository.getMainUser()
        Log.d(TAG, "Getting group balance id from user: ${user!!.username}")

        return if(groupRepository.hasGroup()){
            groupRepository.getGroupBalance()
        } else{
            0.0
        }
    }

    companion object {
        private const val TAG = "GetGroupBalance"
    }
}