package com.utopia.todolist.datasource

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.lang.reflect.Type

class NetDataSource : IDataSource {
    companion object {
        val instance = NetDataSource()
        private val GSON = Gson()
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    private val mainUrlBuilder: HttpUrl.Builder
        get() = HttpUrl.Builder()
            .scheme("http")
            .host("49.233.117.117")
            .port(8000)
            .addPathSegment("tasks")

    private fun getTaskUrl(taskId: Int): HttpUrl = mainUrlBuilder
        .addPathSegment(taskId.toString())
        .build()

    private fun getInsertUrl(): HttpUrl = mainUrlBuilder
        .addPathSegment("add")
        .build()

    override fun queryTaskList(): Set<Task> {
        val url = mainUrlBuilder.build()
        val request = Request.Builder()
            .url(url)
            .build()
        val type = object : TypeToken<Set<Task>>() {}.type
        return getResult<Set<Task>>(request, type) ?: emptySet()
    }

    override fun queryTaskById(taskId: Int): Task? {
        val request = Request.Builder()
            .url(getTaskUrl(taskId))
            .build()
        return getResult(request, Task::class.java)
    }

    private fun <T> getResult(request: Request, type: Type): T? {
        val response = client.newCall(request).execute()
        val json = response.body?.string() ?: return null
        return GSON.fromJson<T>(json, type)
    }

    override fun updateTask(task: Task): Int {
        val request = Request.Builder()
            .url(getTaskUrl(task.id))
            .post(getJsonRequestBody(task))
            .build()
        return getResult<Int>(request, Int::class.java) ?: -1
    }

    private fun getJsonRequestBody(task: Task): RequestBody {
        val mediaType = "application/json;charset=utf-8".toMediaTypeOrNull()
        return GSON.toJson(task).toString().toRequestBody(mediaType)
    }

    override fun insertTask(name: String, content: String): Int {
        val jsonObject = JSONObject()
        jsonObject.put("name", name)
        jsonObject.put("content", content)

        val mediaType = "application/json;charset=utf-8".toMediaTypeOrNull()
        val requestBody = jsonObject.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(getInsertUrl())
            .put(requestBody)
            .build()
        return getResult(request, Int::class.java) ?: -1
    }

    override fun deleteTask(taskId: Int): Boolean {
        val request = Request.Builder()
            .url(getTaskUrl(taskId))
            .delete()
            .build()
        return getResult<Boolean>(request, Boolean::class.java) ?: false
    }
}