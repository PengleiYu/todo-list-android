package com.utopia.todolist.datasource

import android.os.Looper
import androidx.annotation.WorkerThread
import com.utopia.todolist.datasource.sp.SP
import com.utopia.todolist.debugCheck

@WorkerThread
class DataSource private constructor() : IDataSource {
    companion object {
        val instance = DataSource()
    }

    private val sp = SP()

    override fun queryTaskList(): Set<Task> {
        checkThread()
        return sp.getTasks().toSet()
    }

    override fun queryTaskById(taskId: Int): Task? {
        checkThread()
        return sp.getTasks().firstOrNull { taskId == it.id }
    }

    override fun updateTask(task: Task): Int {
        checkThread()
        if (task.id < 0) {
            return insertTask(task.name, task.content)
        }

        val set = sp.getTasks(true).toMutableSet()
        val old = set.firstOrNull { task.id == it.id }
        if (old != null) {
            set.remove(old)
        }
        val new = task.copy()
        set.add(new)
        sp.setTasks(set).flush()
        return new.id
    }

    override fun insertTask(name: String, content: String): Int {
        checkThread()
        val set = sp.getTasks(true).toMutableSet()
        val max = set.map(Task::id).max()
        val id = (max ?: -1) + 1
        val task = Task(id, name, content)
        set.add(task)
        sp.setTasks(set).flush()
        return id
    }

    override fun deleteTask(taskId: Int): Boolean {
        checkThread()
        val set = sp.getTasks(true).toMutableSet()
        val success = set.removeIf { taskId == it.id }
        if (success) {
            sp.setTasks(set).flush()
        }
        return success
    }

    private fun checkThread() {
        debugCheck(Thread.currentThread() != Looper.getMainLooper().thread)
    }
}