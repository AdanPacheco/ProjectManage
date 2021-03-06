package com.udemy.projectmanage.core

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.udemy.projectmanage.R
import java.util.*

class DatePickerFragment(val listener: (year: Int, month: Int, day: Int) -> Unit) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(activity as Context, R.style.datePickerTheme, this, year, month, day)
    }

    override fun onDateSet(dt: DatePicker?, year: Int, month: Int, day: Int) = listener(year, month, day)

}