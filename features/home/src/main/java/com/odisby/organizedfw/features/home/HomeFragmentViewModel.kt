package com.odisby.organizedfw.features.home

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.odisby.organizedfw.core.data.repository.AvatarRepository
import com.odisby.organizedfw.core.data.repository.GroupRepository
import com.odisby.organizedfw.features.home.domain.GetBalanceUseCase
import com.odisby.organizedfw.features.home.domain.GetFinancesOfMonthUseCase
import com.odisby.organizedfw.features.home.domain.GetGroupBalanceUseCase
import com.odisby.organizedfw.features.home.model.FinanceItemType
import com.odisby.organizedfw.features.home.model.HeaderItemType
import com.odisby.organizedfw.features.home.model.ListItemType
import com.odisby.organizedfw.core.data.repository.UsersRepository
import com.odisby.organizedfw.core.data.util.RepositoryState
import com.odisby.organizedfw.core.data.util.ResourceFirebase
import com.odisby.organizedfw.core.data.util.UiStateFlow
import com.odisby.organizedfw.features.home.model.BalanceCardItem
import dagger.hilt.android.lifecycle.HiltViewModel
import com.odisby.organizedfw.core.data.util.AmountUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val getFinancesOfMonthUseCase: GetFinancesOfMonthUseCase,
    private val getBalanceUseCase: GetBalanceUseCase,
    private val avatarRepository: AvatarRepository
) :
    ViewModel() {

    private val _uiState = MutableStateFlow<UiStateFlow<UiState>>(UiStateFlow.Empty())
    val uiState: StateFlow<UiStateFlow<UiState>> = _uiState

    internal val amountUtils = AmountUtils()

    data class UiState(
        val balanceCardList: List<BalanceCardItem>,
        val financeEntityList: List<ListItemType>,
        val welcomeName: String,
        val welcomeDay: String,
        val avatar: Bitmap
    )


    fun getUiState() = viewModelScope.launch {
        _uiState.value = UiStateFlow.Loading()
        val user = usersRepository.getMainUser()!!
        try{
            val transactions = getFinancesOfMonthUseCase()
            val getWelcomeName = getWelcomeName(user.username!!)
            val getWelcomeDay = getWelcomeDay()
            val getAvatar = getAvatar(user.id)!!
            val balanceCards = getBalanceCards()

            _uiState.value = UiStateFlow.Success(
                UiState(
                balanceCards,
                transactions,
                getWelcomeName,
                getWelcomeDay,
                getAvatar
            ))
        } catch (e: Exception){
            Log.w(TAG, "Exception on getUiState")
            _uiState.value = UiStateFlow.Error(e.message.toString())
        }
    }

    private suspend fun getAvatar(userId: String): Bitmap? {
        return avatarRepository.getAvatarById(userId)
    }

    private suspend fun getBalanceCards(): List<BalanceCardItem> {
        return getBalanceUseCase()
    }

    private fun getWelcomeName(username: String): String {
        return "Olá ${username},"
    }

    private fun getWelcomeDay(): String {
        val currentDate = LocalDate.now()
        val month = currentDate.month.getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
        val monthCapitalize = month.replaceFirstChar { it.uppercase() }
        return "Hoje é dia ${currentDate.dayOfMonth} $monthCapitalize"
    }

    companion object {
        private const val TAG = "HomeFragmentViewModel"
    }
}