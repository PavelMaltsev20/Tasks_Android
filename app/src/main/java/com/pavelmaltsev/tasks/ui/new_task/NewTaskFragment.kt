package com.pavelmaltsev.tasks.ui.new_task

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.pavelmaltsev.tasks.databinding.FragmentNewTaskBinding
import com.pavelmaltsev.tasks.module.Task
import java.lang.Exception

class NewTaskFragment : Fragment() {

    private val TAG = "NewTaskFragment"
    private var _binding: FragmentNewTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var selectedTask: Task
    private val viewModel by lazy {
        ViewModelProvider(this).get(NewTaskViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewTaskBinding.inflate(inflater, container, false)
        checkSelectedTask()
        return binding.root
    }

    private fun checkSelectedTask() {
        try {
            val navArgs: NewTaskFragmentArgs by navArgs()
            selectedTask = navArgs.selectedTask
            setTaskData()
        } catch (e: Exception) {
            Log.i(TAG, "task not selected")
        }
    }

    private fun setTaskData() {
        binding.newTaskRemove.visibility = View.VISIBLE
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
    }

    private fun updateTask() {
        val task = Task(
            selectedTask.id,
            getTime(),
            getTitle(),
            getDesc(),
        )
        viewModel.updateTask(task)
    }

    private fun createNewTask() {
        val task = Task(
            0,
            getTime(),
            getTitle(),
            getDesc()
        )

        viewModel.addTask(task)
    }

    private fun getTime() = System.currentTimeMillis()
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

}