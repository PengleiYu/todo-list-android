package com.utopia.todolist.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.utopia.todolist.R
import com.utopia.todolist.datasource.DataSource
import com.utopia.todolist.detail.DetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ListActivity : AppCompatActivity() {
    private val dataSource = DataSource.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
    }

    override fun onStart() {
        super.onStart()
        fetchData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(Menu.NONE, R.id.id_menu_item1, Menu.NONE, "New")?.apply {
            setIcon(android.R.drawable.ic_menu_add)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.id_menu_item1 -> openDetailPage()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.IO) {
            val result = dataSource.queryTaskList()
                .map(UiTaskBean.Companion::fromTask)
            GlobalScope.launch(Dispatchers.Main) {
                setupUI(result)
            }
        }
    }

    private fun setupUI(beans: List<UiTaskBean>) {
        val list = beans.map(UiTaskBean::toString)
        val listView: ListView = findViewById(R.id.listView)
        listView.setOnItemClickListener { parent, view, position, id ->
            val item = beans[position]
            openDetailPage(item.id)
        }
        listView.setOnItemLongClickListener { parent, view, position, id ->
            val bean = beans[position]
            AlertDialog.Builder(this)
                .setTitle("删除该任务")
                .setMessage(bean.title)
                .setPositiveButton("OK") { dialog, which ->
                    deleteTask(bean.id)
                }
                .setNegativeButton("NO") { dialog, which ->
                }
                .show()
            true
        }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list)
        listView.adapter = adapter
    }

    private fun deleteTask(taskId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val success = dataSource.deleteTask(taskId)
            GlobalScope.launch(Dispatchers.Main) main@{
                if (!success) {
                    val msg = "删除失败: id=${taskId}"
                    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                    return@main
                }
                fetchData()
            }
        }
    }

    private fun openDetailPage(taskId: Int? = -1) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_TASK_ID, taskId)
        startActivity(intent)
    }
}