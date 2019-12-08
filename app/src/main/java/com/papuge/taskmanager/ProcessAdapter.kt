package com.papuge.taskmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.papuge.taskmanager.taskManager.ProcessInfoTop

class ProcessAdapter(
    private var _processes: List<ProcessInfoTop>
): RecyclerView.Adapter<ProcessAdapter.ViewHolder>() {

    var processes: List<ProcessInfoTop>
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

        fun bind(processTop: ProcessInfoTop) {
            pid.text = processTop.pid
            commandName.text = processTop.commandName
            cpuUsage.text = processTop.cpu
            state.text = processTop.state
            memory.text = processTop.rSetSize
        }
    }
}