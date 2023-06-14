package com.ruliam.organizedfw.features.home.model

open class ListItemType(
    val typeOfItem: Int
) {
    companion object {
        const val TYPE_FINANCE = 0
        const val TYPE_HEADER = 1
    }
}
