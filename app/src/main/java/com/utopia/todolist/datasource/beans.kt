package com.utopia.todolist.datasource

import com.google.gson.annotations.SerializedName

data class Task(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("content")
    val content: String
)