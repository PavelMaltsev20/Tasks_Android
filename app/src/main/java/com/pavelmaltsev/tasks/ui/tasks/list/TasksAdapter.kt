package com.pavelmaltsev.tasks.ui.tasks.list

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pavelmaltsev.tasks.databinding.ItemTaskBinding
import com.pavelmaltsev.tasks.module.Task
import java.util.*

class TasksAdapter : RecyclerView.Adapter<TasksAdapter.ViewHolder>() {

    private var list = listOf<Task>()

    fun setList(list: List<Task>) {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksAdapter.ViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: TasksAdapter.ViewHolder, position: Int) {
        val task = list[position]
        holder.setUI(task)
    }

    override fun getItemCount() = list.size

    class ViewHolder(binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        private val date = binding.itemTaskDate
        private val title = binding.itemTaskTitle
        private val desc = binding.itemTaskDesc
        private val image = binding.itemTaskImage

        fun setUI(task: Task) {
            date.text = DateFormat.format("dd.MM", task.date)
            title.text = task.title
            desc.text = task.desc
            //image.setImageDrawable(task.imaga)
        }

    }
}
