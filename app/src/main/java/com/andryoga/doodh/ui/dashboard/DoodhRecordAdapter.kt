package com.andryoga.doodh.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andryoga.doodh.R
import com.andryoga.doodh.data.db.DoodhEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DoodhRecordAdapter :
    ListAdapter<DoodhEntity, DoodhRecordAdapter.DoodhRecordViewHolder>(DoodhRecordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoodhRecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_doodh_record, parent, false)
        return DoodhRecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoodhRecordViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    inner class DoodhRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val qtyTextView: TextView = itemView.findViewById(R.id.qtyTextView)

        fun bind(doodhRecord: DoodhEntity) {
            val calendar = Calendar.getInstance().apply {
                set(doodhRecord.year, doodhRecord.month - 1, doodhRecord.day)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val date = calendar.time
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            dateTextView.text = dateFormat.format(date)
            qtyTextView.text = "${String.format("%.2f", doodhRecord.qty)} L"
        }
    }
}

class DoodhRecordDiffCallback : DiffUtil.ItemCallback<DoodhEntity>() {
    override fun areItemsTheSame(oldItem: DoodhEntity, newItem: DoodhEntity): Boolean {
        return oldItem.day == newItem.day && oldItem.month == newItem.month && oldItem.year == newItem.year
    }

    override fun areContentsTheSame(oldItem: DoodhEntity, newItem: DoodhEntity): Boolean {
        return oldItem == newItem
    }
}