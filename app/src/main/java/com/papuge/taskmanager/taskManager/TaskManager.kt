package com.papuge.taskmanager.taskManager

import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
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

        outputStream.writeBytes("top -n 1" + "\n")
        outputStream.flush()

        val reader = BufferedReader(InputStreamReader(su.inputStream))
        var processes = mutableListOf<ProcessInfoTop>()
        var lines = mutableListOf<String>()

        for (i in 1..104) {
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

    fun killProcess(pid: Int) {
        val su = Runtime.getRuntime().exec("su")
        val outputStream = DataOutputStream(su.outputStream)
        outputStream.writeBytes("kill -9 $pid\n")
        outputStream.flush()
        outputStream.writeBytes("exit \n")
        outputStream.flush()
        outputStream.close()
        su.waitFor()
    }

    fun getMemUsage(mi: ActivityManager.MemoryInfo) = (mi.availMem / mi.totalMem.toDouble()) * 100

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

