package com.example.todo.model

data class TodoResponse(
    val limit: Int,
    val skip: Int,
    val todos: List<Todo>,
    val total: Int
)