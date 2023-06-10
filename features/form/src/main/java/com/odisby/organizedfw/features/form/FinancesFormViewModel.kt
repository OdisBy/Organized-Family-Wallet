package com.odisby.organizedfw.features.form

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odisby.organizedfw.core.data.repository.FinanceRepository
import com.odisby.organizedfw.core.data.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FinancesFormViewModel @Inject constructor(
//    private val sessionManager: SessionManager,
    private val financeRepository: FinanceRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {

    /**
     * @param name: Name or title
     * @param date: date, default is the actual day but can change
     * @param isRecurrent: Automatically all months
     * @param isCoupleFinance: finance as a couple
     * @param description: Bill, restaurants, salary, etc.
     * @param amount: How much spent or earned
     * @param financeType: Expense or Budget
     */
    fun addFinance(
        name: String,
        date: Date,
        description: String,
        amount: Double,
        recurrent: Boolean,
        onResult: (Boolean) -> Unit) = viewModelScope.launch{
        try {
//            val userId = usersRepository.getMainUser()!!.id
//            val groupId = usersRepository.getGroupId()
            financeRepository.add(name, date, description, amount, recurrent)
            onResult(true) // Indicate success
        }catch (e: Throwable){
            Log.d(TAG, "An error occurred in formViewModel. The error is: $e")
            onResult(false) // Indicate failure
        }
    }

    companion object {
        private const val TAG = "FormViewModel"
    }
}