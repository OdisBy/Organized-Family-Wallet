package com.ruliam.organizedfw.features.home

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruliam.organizedfw.core.data.repository.AuthRepository
import com.ruliam.organizedfw.core.data.repository.AvatarRepository
import com.ruliam.organizedfw.core.data.repository.UsersRepository
import com.ruliam.organizedfw.core.data.util.AmountUtils
import com.ruliam.organizedfw.core.data.util.UiStateFlow
import com.ruliam.organizedfw.features.home.domain.GetBalanceUseCase
import com.ruliam.organizedfw.features.home.domain.GetFinancesOfMonthUseCase
import com.ruliam.organizedfw.features.home.model.BalanceCardItem
import com.ruliam.organizedfw.features.home.model.ListItemType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val getFinancesOfMonthUseCase: GetFinancesOfMonthUseCase,
    private val getBalanceUseCase: GetBalanceUseCase,
    private val avatarRepository: AvatarRepository,
    private val authRepository: AuthRepository
) :
    ViewModel() {

    private val _uiState = MutableStateFlow<UiStateFlow<UiState>>(UiStateFlow.Empty())
    val uiState: StateFlow<UiStateFlow<UiState>> = _uiState

    private val _signInState = MutableStateFlow(false)
    val signInState: StateFlow<Boolean> = _signInState

    internal val amountUtils = AmountUtils()


    fun isLogged() = viewModelScope.launch {
        _signInState.value = authRepository.checkLogin()
    }

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