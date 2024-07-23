package com.example.todo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.ItemTodoBinding
import com.example.todo.realmdb.RealmTodo

class TodoAdapter(
    private val onEditClick: (RealmTodo) -> Unit,
    private val onDeleteClick: (RealmTodo) -> Unit
) : ListAdapter<RealmTodo, TodoAdapter.TodoViewHolder>(TodoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = getItem(position)
        holder.bind(todo)
    }

    inner class TodoViewHolder(private val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: RealmTodo) {
            binding.todo = todo
            binding.executePendingBindings()

            binding.editButton.setOnClickListener { onEditClick(todo) }
            binding.deleteButton.setOnClickListener { onDeleteClick(todo) }

        }
    }

    class TodoDiffCallback : DiffUtil.ItemCallback<RealmTodo>() {
        override fun areItemsTheSame(oldItem: RealmTodo, newItem: RealmTodo): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: RealmTodo, newItem: RealmTodo): Boolean {
            return oldItem == newItem
        }
    }
}


