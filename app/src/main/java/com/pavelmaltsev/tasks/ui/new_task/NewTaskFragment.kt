package com.pavelmaltsev.tasks.ui.new_task

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.pavelmaltsev.tasks.databinding.FragmentNewTaskBinding
import com.pavelmaltsev.tasks.module.Task
import java.util.*


class NewTaskFragment : Fragment() {

    private var _binding: FragmentNewTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy {
        ViewModelProvider(this).get(NewTaskViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newTaslBtn.setOnClickListener {
            createNewTask()
        }
    }

    private fun createNewTask() {
        val title = binding.newTaslTitle.text.toString()
        val desc = binding.newTaslDesc.text.toString()

        val task = Task(
            0,
            System.currentTimeMillis(),
            title,
            desc
        )

        viewModel.addTask(task)
    }

}