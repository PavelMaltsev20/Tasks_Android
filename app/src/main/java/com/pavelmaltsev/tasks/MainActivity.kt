package com.pavelmaltsev.tasks

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pavelmaltsev.tasks.databinding.ActivityMainBinding
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences(getString(R.string.shared_pref_name), 0)
        val editor = sharedPref.edit()

        val isExplained = sharedPref.getBoolean(getString(R.string.is_explanation_showed), false)
        if (!isExplained) {
            showExplanations()
            editor.putBoolean(getString(R.string.is_explanation_showed), true).apply()
        }
    }

    private fun showExplanations() {
        val array = arrayListOf(
            mapOf(
                "title" to getString(R.string.title_expl_1),
                "msg" to getString(R.string.msg_expl_1)
            ),
            mapOf(
                "title" to getString(R.string.title_expl_2),
                "msg" to getString(R.string.msg_expl_2)
            )
        )

        for (item in array) {
            createDialog(item["title"]!!, item["msg"]!!)
        }
    }

    private fun createDialog(title: String, msg: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}


