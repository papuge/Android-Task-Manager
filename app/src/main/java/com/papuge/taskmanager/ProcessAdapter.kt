package com.papuge.taskmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.papuge.taskmanager.taskManager.ProcessUsage

class ProcessAdapter(
    private var _processes: List<ProcessUsage>
): RecyclerView.Adapter<ProcessAdapter.ViewHolder>() {

    var processes: List<ProcessUsage>
        get() {
            return _processes
        }
        set(value) {
            _processes = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_process, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(_processes[position])
    }

    override fun getItemCount(): Int = _processes.size

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private var pid: TextView = itemView.findViewById(R.id.pro_pid)
        private var commandName: TextView = itemView.findViewById(R.id.pro_command)
        private var cpuUsage: TextView = itemView.findViewById(R.id.pro_cpu)
        private var state: TextView = itemView.findViewById(R.id.pro_state)
        private var memory: TextView = itemView.findViewById(R.id.pro_memory)
        private var startTime: TextView = itemView.findViewById(R.id.pro_start_time)

        fun bind(processUsage: ProcessUsage) {
            pid.text = processUsage.process.pid
            commandName.text = processUsage.process.commandName
            cpuUsage.text = "%.3f".format(processUsage.cpuUsage)
            state.text = processUsage.process.state
            memory.text = processUsage.process.vmSizeKb.toInt().toString()
            startTime.text = processUsage.process.startTimeSeconds.toInt().toString()
        }
    }
}