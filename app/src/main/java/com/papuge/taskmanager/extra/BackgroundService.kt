package com.papuge.taskmanager.extra

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlin.concurrent.thread


class BackgroundService: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        thread(start = true) {
            while (true) {
                for (i in 1..100000) {
                    2*2
                }
                Thread.sleep(10)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? = null

}