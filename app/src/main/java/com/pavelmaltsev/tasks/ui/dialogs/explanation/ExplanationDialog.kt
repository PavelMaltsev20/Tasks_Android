package com.pavelmaltsev.tasks.ui.dialogs.explanation

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.pavelmaltsev.tasks.R
import com.pavelmaltsev.tasks.ui.dialogs.calendar.OnDateSelected

class ExplanationDialog(
    private val title: String,
    private val msg: String
) :
    AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}


