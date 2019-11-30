package com.papuge.taskmanager

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.papuge.taskmanager.taskManager.TaskManager
import java.io.File
import java.io.FileFilter
import java.io.RandomAccessFile


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val cpuU1= TaskManager.getAllCpuUsage()
        Log.d(TAG, "CPU - $cpuU1")
        val cpuU2 = TaskManager.calculateUsage()
        Log.d(TAG, "Processes - \n$cpuU2")
    }
}
