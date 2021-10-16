package com.pavelmaltsev.tasks.ui.dialogs.calendar

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.CalendarView
import androidx.appcompat.app.AppCompatDialogFragment
import com.pavelmaltsev.tasks.databinding.DialogCalendarBinding
import java.util.*

class CalendarDialog(private val onDateSelected: OnDateSelected) : AppCompatDialogFragment() {

    private var _binding: DialogCalendarBinding? = null
    private val binding get() = _binding!!
    private var calendar = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = activity?.layoutInflater?.let {
            DialogCalendarBinding.inflate(
                it,
                null,
                false
            )
        }

        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setView(binding.root)
        initListeners()
        return dialogBuilder.create()
    }

    private fun initListeners() {
        binding.dialogDateCancel.setOnClickListener {
            dismiss()
        }

        binding.dialogDateCalendar.setOnDateChangeListener { view: CalendarView?, year: Int, month: Int, dayOfMonth: Int ->
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month
            calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        }

        binding.dialogDateSave.setOnClickListener {
            onDateSelected.selectedDate(calendar)
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}


