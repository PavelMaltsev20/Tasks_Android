package com.pavelmaltsev.tasks.ui.tasks.list

import android.net.Uri
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pavelmaltsev.tasks.databinding.ItemTaskBinding
import com.pavelmaltsev.tasks.module.Task


class TasksAdapter(private val onTaskListener: OnTaskListener) :
    RecyclerView.Adapter<TasksAdapter.ViewHolder>() {

    private var list = listOf<Task>()

    fun setList(list: List<Task>) {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = list[position]

        holder.date.text = DateFormat.format("dd.MM", task.date)
        holder.title.text = task.title
        holder.desc.text = task.desc

        if (task.imageUrl.length > 1) {
            holder.image.visibility = View.VISIBLE
            Glide.with(holder.parent.context)
                .load(task.imageUrl)
                .into(holder.image);
        } else {
            holder.image.visibility = View.INVISIBLE
        }


        holder.parent.setOnClickListener {
            onTaskListener.onTaskClick(task)
        }

    }

    override fun getItemCount() = list.size

    class ViewHolder(binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        val parent = binding.itemTaskParent
        val date = binding.itemTaskDate
        val title = binding.itemTaskTitle
        val desc = binding.itemTaskDesc
        val image = binding.itemTaskImage
    }
}
