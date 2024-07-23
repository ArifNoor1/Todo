package com.example.todo.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.realmdb.RealmTodo
import com.example.todo.repository.TodoRepository
import com.example.todo.utils.NetworkUtils
import kotlinx.coroutines.launch

class TodoViewModel(
    private val repository: TodoRepository,
    private val application: Application
    ) : ViewModel() {

    private val _todos = MutableLiveData<List<RealmTodo>>()
    val todos: LiveData<List<RealmTodo>> get() = _todos

    init {
        fetchAndSaveTodos()
    }

    private fun fetchAndSaveTodos() {
        viewModelScope.launch {
            try {
                val localTodos = repository.getTodosFromRealmSync()
                if (localTodos.isNotEmpty()) {
                    Log.d("TAG", "Using local todos: $localTodos")
                    _todos.value = localTodos
                } else {
                    if (NetworkUtils.isNetworkAvailable(application)) {
                        Log.d("TAG", "Network is available")
                        repository.fetchAndSaveTodos()
                        loadTodosFromRealm()
                    } else {
                        Log.d("TAG", "Network is not available")
                    }
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error fetching and saving todos", e)
            }
        }
    }

    fun addTodo(todo: RealmTodo) {
        viewModelScope.launch {
            repository.addTodoToRealm(todo)
            loadTodosFromRealm()
        }
    }

    fun updateTodo(todo: RealmTodo) {
        viewModelScope.launch {
            repository.updateTodoInRealm(todo)
            loadTodosFromRealm()
        }
    }

    fun deleteTodo(todo: RealmTodo) {
        viewModelScope.launch {
            repository.deleteTodoFromRealm(todo)
            loadTodosFromRealm()
        }
    }

    private fun loadTodosFromRealm() {
        repository.getTodosFromRealm().observeForever { todo ->
            _todos.value = todo
        }
    }

}