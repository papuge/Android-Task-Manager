package com.papuge.taskmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.papuge.taskmanager.taskManager.ProcessInfoPs
import com.papuge.taskmanager.taskManager.ProcessUsage

class ProcessAdapter(
    private var _processes: List<ProcessInfoPs>
): RecyclerView.Adapter<ProcessAdapter.ViewHolder>() {

    var processes: List<ProcessInfoPs>
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

        fun bind(processPs: ProcessInfoPs) {
            pid.text = processPs.pid
            commandName.text = processPs.commandName
            cpuUsage.text = processPs.cpu
            state.text = processPs.state
            memory.text = processPs.vSetSize
        }
    }
}