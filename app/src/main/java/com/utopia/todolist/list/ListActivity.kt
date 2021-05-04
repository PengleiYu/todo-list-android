package com.utopia.todolist.list

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.utopia.todolist.R
import com.utopia.todolist.datasource.DataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        fetchData()
    }

    private fun fetchData() {
        GlobalScope.launch(Dispatchers.IO) {
            val result = mockResult()
            GlobalScope.launch(Dispatchers.Main) {
                setupUI(result)
            }
        }
    }

    private fun setupUI(beans: List<UiTaskBean>) {
        val list = beans.map(UiTaskBean::toString)
        val listView: ListView = findViewById(R.id.listView)
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list)
        listView.adapter = adapter
    }

    private val dataSource = DataSource()

    private fun mockResult(): List<UiTaskBean> {
        return dataSource.getTaskList().map {
            UiTaskBean(it.name, it.content)
        }
    }
}