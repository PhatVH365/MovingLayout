package com.example.movinglayout.ui.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movinglayout.databinding.TableItemViewBinding
import com.example.movinglayout.model.Table


class ViewHolder constructor(val binding: TableItemViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: Table,
        clickListener: (Table) -> Unit,
        longClickListener: (View, Table) -> Boolean,
        touchListener: (View, MotionEvent, Table) -> Boolean
    ) {
        binding.tableName.text = item.name
        binding.tableId.text = item.id.toString()
        binding.tableName.isSelected = true
        binding.layout.setOnClickListener { clickListener(item) }
        binding.layout.setOnLongClickListener { v -> longClickListener(v, item) }
        binding.layout.setOnTouchListener { v, event -> touchListener(v, event, item) }
    }

    companion object {
        fun from(parent: ViewGroup): ViewHolder {
            return ViewHolder(
                TableItemViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}

class TableRecyclerAdapter(private val l: ItemListener) :
    ListAdapter<Table, ViewHolder>(DiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), l.clickListener, l.longClickListener, l.touchListener)
    }

}

class DiffCallBack : DiffUtil.ItemCallback<Table>() {
    override fun areItemsTheSame(oldItem: Table, newItem: Table): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Table, newItem: Table): Boolean {
        return oldItem == newItem
    }
}

data class ItemListener(
    val clickListener: (Table) -> Unit,
    val longClickListener: (View, Table) -> Boolean,
    val touchListener: (View, MotionEvent, Table) -> Boolean
)