package com.utopia.todolist.datasource

interface IDataSource {
    fun queryTaskList(): Set<Task>
    fun queryTaskById(taskId: Int): Task?
    fun updateTask(task: Task): Int
    fun insertTask(name: String, content: String): Int
}