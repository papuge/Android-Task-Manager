package com.papuge.taskmanager.taskManager

import android.util.Log
import androidx.core.text.isDigitsOnly
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.InputStreamReader


object TaskManager {

    private val TAG = "TaskManager"

    fun getAllCpuUsage(): Double {
        val times = getCpuTimes()
        val totalTime = times.sum().toDouble()
        Log.d(TAG, "total time- $totalTime")
        val idle = times[3].toDouble()
        Log.d(TAG, "idle - $idle")
        return (1.0 - idle/totalTime) * 100  // return usage in percentage
    }

    fun getInfoFromTop(): List<ProcessInfoTop> {
        val su = Runtime.getRuntime().exec("su")
        val outputStream = DataOutputStream(su.outputStream)

        outputStream.writeBytes("top -n -1" + "\n")
        outputStream.flush()

        val reader = BufferedReader(InputStreamReader(su.inputStream))
        var processes = mutableListOf<ProcessInfoTop>()
        var lines = mutableListOf<String>()

        for (i in 1..100) {
            lines.add(reader.readLine())
        }

        reader.close()

        lines.drop(4)
            .forEach { line ->
                var splited = line.split(Regex("""\s+""")).filter { it != "" }
                Log.d(TAG, "splt. $splited")
                processes.add(
                    ProcessInfoTop(
                    splited[0], splited[4], splited[5],
                    splited[8], splited[10]
                    )
                )
            }

        outputStream.writeBytes("exit" + "\n")
        outputStream.flush()
        outputStream.close()
        Log.d(TAG, "Closing Streams")
        su.destroy()
        Log.d(TAG, "Su destroyed")
        return processes
    }

    fun getAllProcessUsage(): List<ProcessUsage> {
        val totalTimeBefore = getCpuTimes().sum().toDouble()

        val pidStatFilesBefore = getPidStatFiles()
        var pidsBefore = getProcessInfos(pidStatFilesBefore)
        try {
            Thread.sleep(300)
        } catch (e: Exception) { }

        val pidStatFilesAfter = getPidStatFiles()
        val pidStatFiles = pidStatFilesBefore.toSet().intersect(pidStatFilesAfter).toList()

        val pidsAfter = getProcessInfos(pidStatFiles)

        val stillExistingPids = pidStatFiles.map { it -> it.toString()
            .substringAfter("/proc/")
            .substringBefore("/stat")
        }
        pidsBefore = pidsBefore.filter { it.pid in stillExistingPids }

        val totalTimeAfter = getCpuTimes().sum().toDouble()

        val totalTime = totalTimeAfter - totalTimeBefore
        val processesUsage = mutableListOf<ProcessUsage>()
        for (i in pidStatFiles.indices) {
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
                    vmSizeBytes = attrs[22].toDouble()
                )
            )
        }
        return  processes
    }

    private fun getPidStatFiles(): List<File> {
        val dir = File("/proc/")
        val dirs = dir.listFiles()!!.filter { it.isDirectory && it.toString()
            .substringAfter("/proc/")
            .isDigitsOnly() }.toList()
        return dirs.map { pid -> File("$pid/stat") }
    }

    private fun getCpuTimes(): List<Long> {
        val cpuStat = File("/proc/stat")
        return cpuStat
            .useLines { it.first() }
            .split(" ")
            .filter { it != "" }
            .drop(1)
            .map { number -> number.toLong() }
    }
}

