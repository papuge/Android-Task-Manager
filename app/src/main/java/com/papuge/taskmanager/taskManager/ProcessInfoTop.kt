package com.papuge.taskmanager.taskManager

data class ProcessInfoTop(
    val pid: String,
    val cpu: String,
    val state: String,
    val rSetSize: String,
    val commandName: String
)