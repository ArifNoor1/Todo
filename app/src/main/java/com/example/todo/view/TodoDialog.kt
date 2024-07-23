package com.example.todo.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.todo.R
import com.example.todo.databinding.UpdateTodoBinding
import com.example.todo.realmdb.RealmTodo

class TodoDialog(
    context: Context,
    private val todo: RealmTodo? = null,
    private val onSave: (String, Boolean) -> Unit
) : Dialog(context) {

    private lateinit var binding: UpdateTodoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.update_todo, null, false)
        setContentView(binding.root)

        binding.todo = todo ?: RealmTodo()


        binding.btnSave.setOnClickListener {
            val title = binding.todoTitleInput.text.toString()
            val completed = binding.todoCompletedInput.isChecked
            onSave(title, completed)
            dismiss()
        }
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}