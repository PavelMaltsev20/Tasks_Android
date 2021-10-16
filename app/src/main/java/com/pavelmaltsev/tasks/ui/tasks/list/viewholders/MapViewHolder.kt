package com.pavelmaltsev.tasks.ui.tasks.list.viewholders

import android.graphics.Paint
import android.text.format.DateFormat
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pavelmaltsev.tasks.R
import com.pavelmaltsev.tasks.databinding.ItemTaskMapBinding
import com.pavelmaltsev.tasks.module.Task
import com.pavelmaltsev.tasks.ui.tasks.list.listeners.OnCompleteListener
import com.pavelmaltsev.tasks.ui.tasks.list.listeners.OnUpdateListener

class MapViewHolder(
    private val binding: ItemTaskMapBinding,
    private val onUpdateListener: OnUpdateListener,
    private val onCompleteListener: OnCompleteListener
) :
    RecyclerView.ViewHolder(binding.root) {

    /**
     * We use two ViewHolders to display information in a different forms
     *
     * View binding in this file is - ItemTaskMapBinding
     *
     * */
    fun displayData(task: Task) {
        binding.itemTaskDate.text = DateFormat.format("dd.MM", task.date)
        binding.itemTaskTitle.text = task.title
        binding.itemTaskDesc.text = task.desc

        //Display selected image
        if (task.imageUrl.isNotEmpty()) {
            binding.itemTaskImage.visibility = View.VISIBLE
            Glide.with(binding.root)
                .load(task.imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.itemTaskImage)
        } else {
            binding.itemTaskImage.visibility = View.VISIBLE
            Glide.with(binding.root)
                .load(R.drawable.ic_placeholder)
                .into(binding.itemTaskImage)
        }

        //Display completed task
        if (task.isComplete) {
            binding.itemTaskTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.itemTaskDesc.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.itemTaskDate.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            binding.itemTaskTitle.paintFlags = 0
            binding.itemTaskDesc.paintFlags = 0
            binding.itemTaskDate.paintFlags = 0
        }

        //On click task will colored like completed
        binding.itemTaskParent.setOnClickListener {
            onCompleteListener.onComplete(task)
        }

        //On long click opens fragment for update task
        binding.itemTaskParent.setOnLongClickListener {
            onUpdateListener.onUpdate(task)
            return@setOnLongClickListener true
        }
    }
}