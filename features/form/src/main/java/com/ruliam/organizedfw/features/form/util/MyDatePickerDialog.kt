package com.ruliam.organizedfw.features.form.util

import android.app.DatePickerDialog
import androidx.fragment.app.FragmentActivity
import com.ruliam.organizedfw.features.form.R
import java.util.*

class MyDatePickerDialog (
    activity : FragmentActivity,
    onShowDateClicked: (Int, Int, Int) -> Unit
) {
    private val dialog =
        DatePickerDialog(activity, com.ruliam.organizedfw.core.ui.R.style.Theme_Organized_DialogTheme).apply {
            setOnDateSetListener { _, year, month, day ->
                onShowDateClicked.invoke(year, month, day)
            }
            datePicker.maxDate = System.currentTimeMillis()
        }
    fun show() {
        dialog.show()
    }
}