package com.utopia.todolist.datasource

import android.os.Looper
import androidx.annotation.WorkerThread
import com.utopia.todolist.debugCheck

@WorkerThread
class DataSource private constructor() : IDataSource {
    companion object {
        val instance = DataSource()
    }

    private val lazyTaskList = lazy {
        (0..10)
            .map { Task(it, "name${it}", "content${it}") }
            .toMutableSet()
    }

    override fun queryTaskList(): Set<Task> {
        checkThread()
        return lazyTaskList.value
    }

    override fun queryTaskById(taskId: Int): Task? {
        checkThread()
        return lazyTaskList.value.firstOrNull { taskId == it.id }
    }

    override fun updateTask(task: Task): Int {
        val set = lazyTaskList.value
        if (task.id < 0) {
            return insertTask(task.name, task.content)
        }

        val old = set.firstOrNull { task.id == it.id }
        if (old != null) {
            set.remove(old)
        }
        val new = task.copy()
        set.add(new)
        return new.id
    }

    override fun insertTask(name: String, content: String): Int {
        val set = lazyTaskList.value
        val max = set.map(Task::id).max()
        val id = (max ?: -1) + 1
        val task = Task(id, name, content)
        set.add(task)
        return id
    }

    private fun checkThread() {
        debugCheck(Thread.currentThread() != Looper.getMainLooper().thread)
    }
}