package com.papuge.taskmanager.taskManager

data class ProcessInfo(
    val pid: String,
    val commandName: String,
    val state: String,
    val utime: Long,
    val stime: Long,
    private val vmSizeBytes: Double
) {
    val vmSizeKb: Double
        get() = vmSizeBytes / 1024
}