package com.example.todo

import com.example.todo.adapter.TodoAdapter
import com.example.todo.view.TodoDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.ActivityMainBinding
import com.example.todo.network.Networking
import com.example.todo.realmdb.RealmTodo
import com.example.todo.repository.TodoRepository
import com.example.todo.viewmodel.TodoViewModel
import com.example.todo.viewmodel.TodoViewModelFactory

class MainActivity : AppCompatActivity(){

      private lateinit var binding: ActivityMainBinding
      private lateinit var viewModel: TodoViewModel
      private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)


        val networking = Networking.retrofit
        val repository = TodoRepository(networking)
        val viewModelFactory = TodoViewModelFactory(repository,application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(TodoViewModel::class.java)

        todoAdapter = TodoAdapter(
            onEditClick = {todo -> showEditDialog(todo)},
            onDeleteClick = {todo-> viewModel.deleteTodo(todo)}
        )

        val divider = DividerItemDecoration(binding.recyclerView.context,LinearLayoutManager.VERTICAL)
        binding.recyclerView.addItemDecoration(divider)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = todoAdapter


        viewModel.todos.observe(this) { todos ->
            todos?.let {
                todoAdapter.submitList(it)
                Log.d("TAG", "Response data $it")
            }
        }

        binding.fabAddTodo.setOnClickListener {
            showAddDialog()
            Log.d("TAG", "onCreate: fabAddTodo")
        }

    }


    private fun showAddDialog() {
        val dialog = TodoDialog(this) { title, completed ->
            val todo = RealmTodo().apply {
                id = 0
                this.title = title
                this.completed = completed
            }
            viewModel.addTodo(todo)
        }
        dialog.show()
    }

    private fun showEditDialog(todo: RealmTodo) {
        Log.d("TAG", "Editing Todo: ${todo.id}, ${todo.title}, ${todo.completed}")
        TodoDialog(this, todo) { title, completed ->
            val updatedTodo = RealmTodo().apply {
                id = todo.id
                this.title = title
                this.completed = completed
            }
            viewModel.updateTodo(updatedTodo)
        }.show()
    }
}