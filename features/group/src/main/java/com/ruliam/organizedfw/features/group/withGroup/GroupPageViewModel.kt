package com.ruliam.organizedfw.features.group.withGroup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class GroupPageViewModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {
    suspend fun getGroupId(): String {
        val groupId = state.get<String>("groupID")
        return groupId ?: "vazio"
    }
}