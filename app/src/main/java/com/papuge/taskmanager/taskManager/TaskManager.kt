package com.papuge.taskmanager.taskManager

import android.util.Log
import androidx.core.text.isDigitsOnly
import java.io.File

object TaskManager {

    private val TAG = "TaskManager"

    fun getAllCpuUsage(): Double {

        // returns CPU usage in percentage

        val cpuStat = File("/proc/stat").useLines { it.first() }
        val str = cpuStat.split(" ")
        Log.d(TAG, "String to parse - $str")
        val times = str
            .filter { it != "" }
            .drop(1)
            .map { it.toLong() }
        val totalTime = times.sum().toDouble()
        Log.d(TAG, "total time- $totalTime")
        val idle = times[3].toDouble()
        Log.d(TAG, "idle - $idle")
        return (1.0 - idle/totalTime) * 100
    }

    fun calculateUsage(): List<ProcessUsage> {
        val pidStatFilesBefore = getPidStatFiles()
        val cpuStat = File("/proc/stat")

        var totalTimeBefore = cpuStat
             .useLines { it.first() }
             .split(" ")
             .filter { it != "" }
             .drop(1)
             .map { number -> number.toLong() }
             .sum().toDouble()

        var pidsBefore = getProcessInfos(pidStatFilesBefore)
        try {
            Thread.sleep(50)
        } catch (e: Exception) { }

        val pidStatFilesAfter = getPidStatFiles()
        val pidStatFiles = pidStatFilesBefore.toSet().intersect(pidStatFilesAfter).toList()
        val stillExistingPids = pidStatFiles.map { it -> it.toString()
            .substringAfter("/proc/")
            .substringBefore("/stat")
        }
        pidsBefore = pidsBefore.filter { it.pid in stillExistingPids }
        val pidsAfter = getProcessInfos(pidStatFiles)

        val totalTimeAfter = cpuStat
            .useLines { it.first() }
            .split(" ")
            .filter { it != "" }
            .drop(1)
            .map { number -> number.toLong() }
            .sum().toDouble()

        val totalTime = totalTimeAfter - totalTimeBefore
        val processesUsage = mutableListOf<ProcessUsage>()
        for (i in pidStatFilesBefore.indices) {
            val userUsage = (pidsAfter[i].utime - pidsBefore[i].utime).toDouble()
            val sysUsage = (pidsAfter[i].stime - pidsBefore[i].stime).toDouble()
            val cpuUsage = (userUsage + sysUsage) / totalTime * 100
            processesUsage.add(ProcessUsage(pidsAfter[i].copy(), cpuUsage))
        }
        return processesUsage
    }

    private fun getProcessInfos(files: List<File>): List<ProcessInfo> {
        val processes = mutableListOf<ProcessInfo>()
        val filesContent = files.map { file -> file.readText() }
        for (pid in filesContent) {
            val attrs = pid.split(" ")
            processes.add(
                ProcessInfo(
                    pid = attrs[0],
                    commandName = attrs[1],
                    state = attrs[2],
                    utime = attrs[13].toLong(),
                    stime = attrs[14].toLong(),
                    vmSizeBytes = attrs[22].toDouble(),
                    startTimeClock = attrs[21].toDouble()
                )
            )
        }
        return  processes
    }

    private fun getPidStatFiles(): List<File> {
        val dir = File("/proc/")
        var dirs = dir.listFiles().filter { it.isDirectory && it.toString().substringAfter("/proc/")
            .isDigitsOnly() }?.toList()
        return dirs.map { pid -> File("$pid/stat") }
    }
}

