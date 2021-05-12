package com.utopia.todolist.datasource.sp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.utopia.todolist.App
import com.utopia.todolist.datasource.Task

class SP {
    companion object {
        private const val SP_NAME = "todo_list"
        private const val SP_KEY_LIST = "key_list"
        private val gson: Gson = Gson()
    }

    private var lazyTaskList: List<Task>? = null

    fun setTasks(taskList: Collection<Task>): SP {
        lazyTaskList = taskList.toList()
        return this
    }

    fun getTasks(): List<Task> = getTasks(false)

    fun getTasks(forceRefresh: Boolean): List<Task> {
        val list: List<Task> =
            if (forceRefresh) unSerializeTasks()
            else (lazyTaskList ?: unSerializeTasks())
        lazyTaskList = list
        return list
    }

    fun flush() {
        val sp = App.instance.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        val list = lazyTaskList
        if (list == null) {
            sp.edit().remove(SP_KEY_LIST).apply()
        } else {
            sp.edit().putString(SP_KEY_LIST, gson.toJson(list)).apply()
        }
    }

    private fun unSerializeTasks(): List<Task> {
        val sp = App.instance.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        val taskListStr = sp.getString(SP_KEY_LIST, "[]")
        val token = object : TypeToken<List<Task>>() {}
        return gson.fromJson(taskListStr, token.type)
    }
}