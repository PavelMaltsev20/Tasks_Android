package com.pavelmaltsev.tasks.ui.manage_task

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.pavelmaltsev.tasks.R
import com.pavelmaltsev.tasks.databinding.FragmentManageTaskBinding
import com.pavelmaltsev.tasks.module.Task
import com.pavelmaltsev.tasks.ui.dialog.calendar.CalendarDialog
import com.pavelmaltsev.tasks.ui.dialog.calendar.OnDateSelected
import java.util.*

class ManageTaskFragment : Fragment(), OnDateSelected {

    private val TAG = "NewTaskFragment"
    private val viewModel by lazy {
        ViewModelProvider(this).get(ManageViewModel::class.java)
    }
    private lateinit var selectedTask: Task
    private var _binding: FragmentManageTaskBinding? = null
    private val binding get() = _binding!!
    private var calendar = Calendar.getInstance()
    private var imageUri = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentManageTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkSelectedTask()
        setDate()
        initListeners()
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

    private fun initListeners() {
        binding.manageTaskBtn.setOnClickListener {
            manageTask()
            hideKeyboard()
        }

        binding.manageTaskRemove.setOnClickListener {
            viewModel.removeTask(selectedTask)
            closeFragment()
        }

        binding.manageTaskDate.setOnClickListener {
            val calendar = CalendarDialog(this)
            activity?.let { activity ->
                calendar.show(activity.supportFragmentManager, "Calendar dialog")
            }
        }

        binding.manageTaskAddImage.setOnClickListener {
            openGallery()
        }
    }

    private fun manageTask() {
        if (getTitle().isEmpty()) {
            Toast.makeText(requireContext(), R.string.please_enter_title, Toast.LENGTH_SHORT).show()
            return
        }

        val task = Task(
            0,
            getDate(),
            getTitle(),
            getDesc(),
            false,
            imageUri
        )

        if (this::selectedTask.isInitialized) {
            task.id = selectedTask.id
            viewModel.updateTask(task)
        } else {
            viewModel.addTask(task)
        }

        closeFragment()
    }

    private fun setTaskData() {
        binding.manageTaskFragTitle.text = getText(R.string.update_task)
        binding.manageTaskRemove.visibility = View.VISIBLE
        binding.manageTaskTitle.setText(selectedTask.title)
        binding.manageTaskDesc.setText(selectedTask.desc)
        calendar.timeInMillis = selectedTask.date
        imageUri = selectedTask.imageUrl
        if (imageUri.isNotEmpty())
            setImage()
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri = result.data!!.data!!.toString()
                setImage()
            }
        }

    private fun setDate() {
        binding.manageTaskDate.text =
            DateFormat.format("dd.MM.yyyy", calendar)
    }

    private fun setImage() {
        binding.manageTaskImage.visibility = View.VISIBLE
        Glide.with(requireContext())
            .load(imageUri)
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.manageTaskImage)
    }

    private fun getDate() = calendar.timeInMillis
    private fun getTitle() = binding.manageTaskTitle.text.toString()
    private fun getDesc() = binding.manageTaskDesc.text.toString()

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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun selectedDate(calendar: Calendar) {
        this.calendar = calendar
        setDate()
    }
}