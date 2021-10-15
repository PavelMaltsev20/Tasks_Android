package com.pavelmaltsev.tasks.ui.manage_task

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
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
        checkSelectedTask()
        setDate()
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

    private fun setDate() {
        binding.manageTaskDate.text =
            DateFormat.format("dd.MM.yyyy", calendar)
    }

    private fun setTaskData() {
        binding.manageTaskFragTitle.text = getText(R.string.update_task)
        binding.manageTaskRemove.visibility = View.VISIBLE
        binding.manageTaskTitle.setText(selectedTask.title)
        binding.manageTaskDesc.setText(selectedTask.desc)
        calendar.timeInMillis = selectedTask.date
        imageUri = selectedTask.imageUrl
        if (imageUri.isNotEmpty())
            displayImage()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.manageTaskBtn.setOnClickListener {
            if (this::selectedTask.isInitialized) {
                updateTask()
            } else {
                createNewTask()
            }
            hideKeyboard()
            closeFragment()
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
                displayImage()
            }
        }

    private fun displayImage() {
        binding.manageTaskImage.visibility = View.VISIBLE
        Glide.with(requireContext())
            .load(imageUri)
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.manageTaskImage)
    }

    private fun updateTask() {
        val task = Task(
            selectedTask.id,
            getDate(),
            getTitle(),
            getDesc(),
            imageUri
        )
        viewModel.updateTask(task)
    }

    private fun createNewTask() {
        val task = Task(
            0,
            getDate(),
            getTitle(),
            getDesc(),
            imageUri
        )

        viewModel.addTask(task)
    }

    private fun getDate() = calendar.timeInMillis
    private fun getTitle(): String {
        val title = binding.manageTaskTitle.text.toString()
        if (title.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.please_enter_title),
                Toast.LENGTH_SHORT
            ).show()
            return ""
        }

        return title
    }

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

    override fun selectedDate(calendar: Calendar) {
        this.calendar = calendar
        setDate()
    }


}