package com.papuge.taskmanager.taskManager

data class ProcessInfoPs(
    val pid: String,
    val cpu: String,
    val vSetSize: String,
    val state: String,
    val commandName: String
)