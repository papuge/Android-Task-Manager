package com.papuge.taskmanager

import android.app.Activity
import android.app.ActivityManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.papuge.taskmanager.taskManager.TaskManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    lateinit var mainHandler: Handler

    private var values = MutableList(10) { value -> 0f}
    private var dataset = LineDataSet(listOf<Entry>(), null)


    private val updateUsageInfo = object: Runnable {
        override fun run() {
            updateViews()
            mainHandler.postDelayed(this, 1500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainHandler = Handler(Looper.getMainLooper())

        setUpChartAttrs()
        setUpDatasetAttrs()
        setChartData()

        val lineData = LineData(dataset)
        lineData.setValueTextSize(9f)
        lineData.setDrawValues(false)
        cpu_chart.data = lineData
        cpu_chart.invalidate()
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
        tv_cpu_usage.text = getString(R.string.total_cpu_usage, "%.5f".format(cpuU1) + "%")
        setChartData(cpuU1)
    }


    private fun setChartData(cpuUsage: Double = 0.0) {
        if (cpu_chart.data != null) {
            for (i in 0..8) {
                values[i] = values[i + 1]
            }
            values[9] = cpuUsage.toFloat()
        }
        val entries = mutableListOf<Entry>()
        for (i in values.indices) {
            entries.add(Entry(i.toFloat(), values[i]))
        }
        dataset.values = entries
        if (cpu_chart.data != null) {
            cpu_chart.data.notifyDataChanged()
        }
        cpu_chart.notifyDataSetChanged()
        cpu_chart.invalidate()
    }

    private fun setUpDatasetAttrs() {
        dataset.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataset.cubicIntensity = 0.2f
        dataset.setDrawFilled(true)
        dataset.setDrawCircles(false)
        dataset.lineWidth = 1.8f
        dataset.circleRadius = 4f
        dataset.setCircleColor(Color.WHITE)
        dataset.highLightColor = Color.rgb(244, 117, 117)
        dataset.color = Color.rgb(104, 241, 175)
        dataset.fillColor = Color.rgb(104, 241, 175)
        dataset.fillAlpha = 100
        dataset.setDrawHorizontalHighlightIndicator(false)
    }

    private fun setUpChartAttrs() {
        cpu_chart.setViewPortOffsets(0f, 0f, 0f, 0f)
        cpu_chart.setBackgroundColor(Color.rgb(254, 254, 254))

        // no description text
        cpu_chart.description.isEnabled = false
        cpu_chart.setDrawGridBackground(false)
        cpu_chart.maxHighlightDistance = 300f

        val x: XAxis = cpu_chart.xAxis
        x.isEnabled = false

        val y: YAxis = cpu_chart.axisLeft
        y.setLabelCount(10, false)
        y.axisMinimum = 0f
        y.axisMaximum = 10f
        y.textColor = Color.BLACK
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        y.setDrawGridLines(false)
        y.axisLineColor = Color.WHITE

        cpu_chart.axisRight.isEnabled = false

        cpu_chart.legend.isEnabled = false

        cpu_chart.animateXY(100, 100)
    }
}
