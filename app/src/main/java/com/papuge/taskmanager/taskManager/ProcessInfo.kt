package com.papuge.taskmanager.taskManager

data class ProcessInfo(
    val pid: String,
    val commandName: String,
    val state: String,
    val utime: Long,
    val stime: Long,
    val cpu: Double = 0.0,
    private val vmSizeBytes: Double
) {
    val vmSizeKb: Double
        get() = vmSizeBytes / 1024
}