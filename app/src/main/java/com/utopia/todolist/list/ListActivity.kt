package com.utopia.todolist.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
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
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list)
        listView.adapter = adapter
    }

    private fun openDetailPage(taskId: Int? = -1) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_TASK_ID, taskId)
        startActivity(intent)
    }
}