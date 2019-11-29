package com.papuge.taskmanager

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import java.io.File
import java.io.FileFilter
import java.io.RandomAccessFile


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val process = Runtime.getRuntime().exec("top -n 1")
//        val bufferedReader = BufferedReader(
//            InputStreamReader(process.inputStream)
//        )
//        Log.d("MainActivity", "${bufferedReader.lines()}")

        val dir = File("/proc/")
        val dirs = dir.listFiles(FileFilter { it.isDirectory && it.toString().substringAfter("/proc/")
            .isDigitsOnly() })?.toList() ?: emptyList()
        Log.d(TAG, "Loaded info - $dirs")
        val reader = RandomAccessFile("/proc/stat", "r")
        var load: String = reader.readLine()
        Log.d(TAG, "Loaded info - $load")

        var toks =
            load.split(" ").toTypedArray() // Split on one or more spaces


        val idle1 = toks[4].toLong()
        val cpu1 =
            toks[2].toLong() + toks[3].toLong() + toks[5].toLong() + toks[6].toLong() + toks[7].toLong() + toks[8].toLong()

        try {
            Thread.sleep(360)
        } catch (e: Exception) {
        }

        reader.seek(0)
        load = reader.readLine()
        reader.close()

        toks = load.split(" ").toTypedArray()

        val idle2 = toks[4].toLong()
        val cpu2 =
            toks[2].toLong() + toks[3].toLong() + toks[5].toLong() + toks[6].toLong() + toks[7].toLong() + toks[8].toLong()

        val calcValue =  (cpu2 - cpu1).toFloat() / (cpu2 + idle2 - (cpu1 + idle1))
        Log.d(TAG, "Calaculated usage - $calcValue")
    }
}
