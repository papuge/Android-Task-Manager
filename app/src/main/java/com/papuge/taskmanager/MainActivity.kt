package com.papuge.taskmanager

import android.app.Activity
import android.app.ActivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.papuge.taskmanager.taskManager.TaskManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    lateinit var mainHandler: Handler

    private val updateUsageInfo = object: Runnable {
        override fun run() {
            updateViews()
            mainHandler.postDelayed(this, 2000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainHandler = Handler(Looper.getMainLooper())
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateUsageInfo)
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateUsageInfo)
    }

    private fun updateViews() {
        var cpuU1 = TaskManager.getAllCpuUsage()
        Log.d(TAG, "CPU - $cpuU1")
        var cpuU2 = TaskManager.getAllProcessUsage()
        Log.d(TAG, "Processes - \n$cpuU2")
        val am = getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        val amProcesses = am.runningAppProcesses
        Log.d(TAG, "AM - ${amProcesses.map { it.processName }}")
        tv_cpu_usage.text = "%.4f".format(cpuU1)
    }
}
