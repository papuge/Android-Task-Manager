package com.papuge.taskmanager

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.papuge.taskmanager.extra.BackgroundService
import com.papuge.taskmanager.taskManager.SwipeToDeleteCallback
import com.papuge.taskmanager.taskManager.TaskManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    lateinit var mainHandler: Handler
    var adapter = ProcessAdapter(this, mutableListOf())
    val memoryInfo = ActivityManager.MemoryInfo()

    private var valuesCPU = MutableList(10) { value -> 0f}
    private var valuesMEM = MutableList(10) { value -> 0f}
    private var datasetCPU = LineDataSet(listOf<Entry>(), null)
    private var datasetMEM = LineDataSet(listOf<Entry>(), null)


    private val updateUsageInfo = object: Runnable {
        override fun run() {
            updateViews()
            mainHandler.postDelayed(this, 2700)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainHandler = Handler(Looper.getMainLooper())
        startService(Intent(this, BackgroundService::class.java))

        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)

        setUpChartAttrs()
        datasetCPU = getDatasetWithAttrs(Triple(184, 142, 255))
        datasetMEM = getDatasetWithAttrs(Triple(104, 241, 175))
        setUpChartAttrs()
        setChartData()

        processes_list.adapter = adapter
        processes_list.layoutManager = LinearLayoutManager(this)
        val decoration = DividerItemDecoration(this, LinearLayout.VERTICAL)
        processes_list.addItemDecoration(decoration)
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
        itemTouchHelper.attachToRecyclerView(processes_list)

        val lineData = LineData(datasetCPU)
        lineData.addDataSet(datasetMEM)
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

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, BackgroundService::class.java))
    }

    private fun updateViews() {
        var cpuUsage = TaskManager.getAllCpuUsage()
        Log.d(TAG, "CPU - $cpuUsage")

        var memoryUsage = TaskManager.getMemUsage(memoryInfo)
        Log.d(TAG, "MEM - $memoryUsage")

        var topInfo = TaskManager.getInfoFromTop()
        adapter.processes = topInfo
        Log.d(TAG, "Processes - \n$topInfo")


        tv_cpu_usage.text = getString(R.string.total_cpu_usage, "%.3f".format(cpuUsage) + "%",
            "%.3f".format(memoryUsage) + "%")
        setChartData(cpuUsage, memoryUsage)
    }


    private fun setChartData(cpuUsage: Double = 0.0, memUsage: Double = 0.0) {
        if (cpu_chart.data != null) {
            for (i in 0..8) {
                valuesCPU[i] = valuesCPU[i + 1]
                valuesMEM[i] = valuesMEM[i + 1]
            }
            valuesCPU[9] = cpuUsage.toFloat()
            valuesMEM[9] = memUsage.toFloat()
        }
        val entriesCPU = mutableListOf<Entry>()
        val entriesMEM = mutableListOf<Entry>()
        for (i in valuesCPU.indices) {
            entriesCPU.add(Entry(i.toFloat(), valuesCPU[i]))
            entriesMEM.add(Entry(i.toFloat(), valuesMEM[i]))
        }
        datasetCPU.values = entriesCPU
        datasetMEM.values = entriesMEM
        if (cpu_chart.data != null) {
            cpu_chart.data.notifyDataChanged()
        }
        cpu_chart.notifyDataSetChanged()
        cpu_chart.invalidate()
    }

    private fun getDatasetWithAttrs(rgb: Triple<Int, Int, Int>): LineDataSet {
        val dataset = LineDataSet(listOf<Entry>(), null)
        dataset.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataset.cubicIntensity = 0.2f
        dataset.setDrawFilled(true)
        dataset.setDrawCircles(false)
        dataset.lineWidth = 1.8f
        dataset.circleRadius = 4f
        dataset.setCircleColor(Color.WHITE)
        dataset.highLightColor = Color.rgb(rgb.first, rgb.second, rgb.third)
        dataset.color = Color.rgb(rgb.first, rgb.second, rgb.third)
        dataset.fillColor = Color.rgb(rgb.first, rgb.second, rgb.third)
        dataset.fillAlpha = 100
        dataset.setDrawHorizontalHighlightIndicator(false)
        return dataset
    }

    private fun setUpChartAttrs() {
        cpu_chart.setViewPortOffsets(60f, 0f, 0f, 0f)
        cpu_chart.setBackgroundColor(Color.rgb(254, 254, 254))

        // no description text
        cpu_chart.description.isEnabled = false
        cpu_chart.setDrawGridBackground(false)
        cpu_chart.maxHighlightDistance = 300f

        val x: XAxis = cpu_chart.xAxis
        x.isEnabled = false

        val y: YAxis = cpu_chart.axisLeft
        y.setLabelCount(5, false)
        y.axisMaximum = 103f
        y.axisMinimum = -2f
        y.textColor = Color.BLACK
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        y.setDrawGridLines(true)
        y.axisLineColor = Color.WHITE

        cpu_chart.axisRight.isEnabled = false

        cpu_chart.legend.isEnabled = false

        cpu_chart.animateXY(50, 50)
    }
}
