package com.example.todo.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todo.model.Todo
import com.example.todo.network.NetworkService
import com.example.todo.realmdb.RealmTodo
import io.realm.Realm

class TodoRepository(private val networkService: NetworkService) {

    suspend fun fetchAndSaveTodos() {
        try {
            val todos = networkService.getTodos().todos
            Log.d("TAG", "fetchAndSaveTodos: $todos")
            saveTodosToRealm(todos)
        } catch (e: Exception) {
            Log.d("TAG", "Error fetching todos from network", e)
        }
    }


    private fun saveTodosToRealm(todos: List<Todo>) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { transactionRealm ->
            transactionRealm.deleteAll()
            val realmTodo = todos.map { todo ->
                RealmTodo(todo.id, todo.todo, todo.completed)
            }
            transactionRealm.insert(realmTodo)
        }
        Log.d("TodoRepository", "Todos saved to Realm: ${getTodosFromRealmSync()}")
        realm.close()
    }

    fun getTodosFromRealm(): LiveData<List<RealmTodo>> {
        val realm = Realm.getDefaultInstance()
        val results = realm.where(RealmTodo::class.java).findAllAsync()
        val liveData = MutableLiveData<List<RealmTodo>>()
        results.addChangeListener { realmResults ->
            liveData.value = realm.copyFromRealm(realmResults)
        }
        realm.close()
        return liveData
    }

    fun addTodoToRealm(todo: RealmTodo) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { transactionRealm ->
            todo.id = getNextId()
            transactionRealm.insertOrUpdate(todo)
        }
        realm.close()
    }

    private fun getNextId(): Int {
        val realm = Realm.getDefaultInstance()
        val maxId = realm.where(RealmTodo::class.java).max("id")?.toInt() ?: 0
        realm.close()
        return maxId + 1
    }


    fun updateTodoInRealm(todo: RealmTodo) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { transactionRealm ->
            val existingTodo = transactionRealm.where(RealmTodo::class.java)
                .equalTo("id", todo.id)
                .findFirst()
            existingTodo?.apply {
                title = todo.title
                completed = todo.completed
            }
        }
        realm.close()
    }

    fun deleteTodoFromRealm(todo: RealmTodo) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { transactionRealm ->
            val result = transactionRealm.where(RealmTodo::class.java)
                .equalTo("id", todo.id)
                .findFirst()
            result?.deleteFromRealm()
        }
        realm.close()
    }

    fun getTodosFromRealmSync(): List<RealmTodo> {
        val realm = Realm.getDefaultInstance()
        return realm.where(RealmTodo::class.java).findAll().let { realm.copyFromRealm(it) }
    }
}