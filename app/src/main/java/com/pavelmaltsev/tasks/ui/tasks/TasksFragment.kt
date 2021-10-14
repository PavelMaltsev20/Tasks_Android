package com.pavelmaltsev.tasks.ui.tasks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pavelmaltsev.tasks.R
import com.pavelmaltsev.tasks.databinding.FragmentNewTaskBinding
import com.pavelmaltsev.tasks.databinding.FragmentTasksBinding
import com.pavelmaltsev.tasks.module.Task
import com.pavelmaltsev.tasks.ui.tasks.list.OnTaskListener
import com.pavelmaltsev.tasks.ui.tasks.list.TasksAdapter


class TasksFragment : Fragment(), OnTaskListener {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private val tasksAdapter = TasksAdapter(this)
    private val viewModel by lazy {
        ViewModelProvider(this).get(TaskViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        initRecyclerView()

        return binding.root
    }

    private fun initRecyclerView() {
        binding.tasksList.adapter = tasksAdapter
        binding.tasksList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.tasksList.hasFixedSize()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.tasksFab.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_tasksFragment_to_newTaskFragment)
        }

        viewModel.tasksList.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                binding.tasksEmptyList.visibility = View.VISIBLE
            } else {
                binding.tasksEmptyList.visibility = View.GONE
            }
            tasksAdapter.setList(it)
            tasksAdapter.notifyDataSetChanged()
        })
    }

    override fun onTaskClick(task: Task) {
        val action = TasksFragmentDirections.actionTasksFragmentToNewTaskFragment(task)
        Navigation.findNavController(binding.root)
            .navigate(action)
    }


}