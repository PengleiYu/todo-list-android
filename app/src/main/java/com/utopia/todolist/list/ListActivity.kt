package com.utopia.todolist.list

import android.content.Intent
import android.os.Bundle
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

            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_TASK_ID, item.id)
            startActivity(intent)
        }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list)
        listView.adapter = adapter
    }
}