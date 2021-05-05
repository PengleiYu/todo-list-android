package com.utopia.todolist.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.utopia.todolist.R
import com.utopia.todolist.datasource.DataSource
import com.utopia.todolist.datasource.IDataSource
import com.utopia.todolist.datasource.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private val dataSource: IDataSource = DataSource.instance
    private val itemId: Int
        get() {
            return intent.getIntExtra(EXTRA_TASK_ID, ID_INVALID)
        }

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        initView()

        fetchData(itemId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(Menu.NONE, R.id.id_menu_item1, Menu.NONE, "Hello")
            ?.apply {
                setIcon(android.R.drawable.ic_menu_save)
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.id_menu_item1 -> saveInput()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        etTitle = findViewById(R.id.et_title)
        etContent = findViewById(R.id.et_content)
    }

    private fun fetchData(taskId: Int) {
        GlobalScope.launch(Dispatchers.IO) io@{
            val task = dataSource.queryTaskById(taskId)
            GlobalScope.launch(Dispatchers.Main) main@{
                if (task == null) {
                    val msg = "can`t find task by id=${taskId}"
                    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                    return@main
                }
                applyData(task)
            }
        }
    }

    private fun applyData(task: Task) {
        etTitle.setText(task.name)
        etContent.setText(task.content)
    }

    private fun saveInput() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()
        if (!Content(title, content).valid()) {
            Toast.makeText(this, "内容不合法", Toast.LENGTH_SHORT).show()
            return
        }
        val task = Task(itemId, title, content)
        dataSource.updateTask(task)
        finish()
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val ID_INVALID = -1

        private data class Content(val title: String, val content: String) {
            fun valid(): Boolean {
                return title.isNotBlank() && content.isNotBlank()
            }
        }
    }
}
