package com.utopia.todolist

fun debugCheck(value: Boolean) {
    if (BuildConfig.DEBUG && !value) {
        throw IllegalStateException("Check failed.")
    }
}