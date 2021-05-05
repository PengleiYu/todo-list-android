package com.utopia.todolist.list

import com.utopia.todolist.datasource.Task

data class UiTaskBean(val id: Int, val title: String, val desc: String) {

    companion object {
        fun fromTask(task: Task): UiTaskBean {
            return with(task) { UiTaskBean(id, name, content) }
        }
    }
}