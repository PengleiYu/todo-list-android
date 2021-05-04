package com.utopia.todolist.datasource

class DataSource {
    fun getTaskList(): List<Task> {
        println("thread: ${Thread.currentThread().name}")
        return (0..10).map { Task("name${it}", "content${it}") }
    }
}