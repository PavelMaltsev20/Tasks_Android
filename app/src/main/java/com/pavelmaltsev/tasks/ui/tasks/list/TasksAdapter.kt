package com.pavelmaltsev.tasks.ui.tasks.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pavelmaltsev.tasks.data.enum.DisplayMode
import com.pavelmaltsev.tasks.databinding.ItemTaskListBinding
import com.pavelmaltsev.tasks.databinding.ItemTaskMapBinding
import com.pavelmaltsev.tasks.module.Task
import com.pavelmaltsev.tasks.ui.tasks.list.listeners.OnCompleteListener
import com.pavelmaltsev.tasks.ui.tasks.list.listeners.OnUpdateListener
import com.pavelmaltsev.tasks.ui.tasks.list.viewholders.ListViewHolder
import com.pavelmaltsev.tasks.ui.tasks.list.viewholders.MapViewHolder


class TasksAdapter(
    private val onUpdateListener: OnUpdateListener,
    private val onCompleteListener: OnCompleteListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = listOf<Task>()
    private var displayMode: DisplayMode = DisplayMode.LIST_MODE
    private val listMode = 0
    private val mapMode = 1

    fun setList(list: List<Task>) {
        this.list = list
    }

    fun setListMode(displayMode: DisplayMode) {
        this.displayMode = displayMode
    }

    override fun getItemViewType(position: Int) = if (DisplayMode.LIST_MODE == displayMode) {
        listMode
    } else {
        mapMode
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (DisplayMode.LIST_MODE == displayMode) {
            val binding =
                ItemTaskListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ListViewHolder(binding, onUpdateListener, onCompleteListener)
        } else {
            val binding =
                ItemTaskMapBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MapViewHolder(binding, onUpdateListener, onCompleteListener)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = list[position]

        when (holder.itemViewType) {
            listMode -> {
                val holderView = holder as ListViewHolder
                holderView.displayData(task)
            }
            mapMode -> {
                val holderView = holder as MapViewHolder
                holderView.displayData(task)
            }
        }
    }

    override fun getItemCount() = list.size
}
