package com.pavelmaltsev.tasks.ui.new_task

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.pavelmaltsev.tasks.R
import com.pavelmaltsev.tasks.databinding.FragmentManageTaskBinding
import com.pavelmaltsev.tasks.module.Task
import com.pavelmaltsev.tasks.ui.dialog.calendar.CalendarDialog
import com.pavelmaltsev.tasks.ui.dialog.calendar.OnDateSelected
import java.util.*

class ManageTaskFragment : Fragment(), OnDateSelected {

    private val TAG = "NewTaskFragment"
    private var _binding: FragmentManageTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var selectedTask: Task
    private var calendar = Calendar.getInstance()
    private val viewModel by lazy {
        ViewModelProvider(this).get(ManageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentManageTaskBinding.inflate(inflater, container, false)
        checkSelectedTask()
        return binding.root
    }

    private fun checkSelectedTask() {
        try {
            val navArgs: ManageTaskFragmentArgs by navArgs()
            selectedTask = navArgs.selectedTask
            setTaskData()
        } catch (e: Exception) {
            Log.i(TAG, "task not selected")
        }
    }

    private fun setTaskData() {
        binding.newTaslFragTitle.text = getText(R.string.update_task)
        binding.newTaskRemove.visibility = View.VISIBLE
        binding.newTaslDate.text =
            DateFormat.format("dd.MM.yyyy", selectedTask.date)
        binding.newTaslTitle.setText(selectedTask.title)
        binding.newTaslDesc.setText(selectedTask.desc)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newTaslBtn.setOnClickListener {
            if (this::selectedTask.isInitialized) {
                updateTask()
            } else {
                createNewTask()
            }
            hideKeyboard()
            closeFragment()
        }

        binding.newTaskRemove.setOnClickListener {
            viewModel.removeTask(selectedTask)
            closeFragment()
        }

        binding.newTaslDate.setOnClickListener {
            val calendar = CalendarDialog(this)
            activity?.let { activity ->
                calendar.show(activity.supportFragmentManager, "Calendar dialog")
            }
        }
    }

    private fun updateTask() {
        Log.i("tester ", "updateTask: ${getDate()}")
        val task = Task(
            selectedTask.id,
            getDate(),
            getTitle(),
            getDesc(),
        )
        viewModel.updateTask(task)
    }

    private fun createNewTask() {
        val task = Task(
            0,
            getDate(),
            getTitle(),
            getDesc()
        )

        viewModel.addTask(task)
    }

    private fun getDate() = calendar.timeInMillis
    private fun getTitle() = binding.newTaslTitle.text.toString()
    private fun getDesc() = binding.newTaslDesc.text.toString()

    private fun hideKeyboard() {
        val inputManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun closeFragment() {
        Navigation.findNavController(binding.root).popBackStack()
    }

    override fun selectedDate(calendar: Calendar) {
        this.calendar = calendar
    }
}