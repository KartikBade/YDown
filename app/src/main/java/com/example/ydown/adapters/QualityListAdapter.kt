package com.example.ydown.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.python.PyObject
import com.example.ydown.databinding.MainQualityRvListItemBinding

class QualityListAdapter(
    private val onDownloadClick: (Int) -> Unit
): ListAdapter<PyObject, QualityListAdapter.QualityListViewHolder>(DiffCallback) {

    class QualityListViewHolder(
        val binding: MainQualityRvListItemBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(pyObject: PyObject) {
            val qualityItem = pyObject.toString()
            when (val videoType = qualityItem.substringAfter(" type=\"").substringBefore("\"")) {
                "video" -> {
                    val videoFormat = qualityItem.substringAfter("mime_type=\"video/").substringBefore("\"")
                    val videoQuality = qualityItem.substringAfter("res=\"").substringBefore("\"")
                    binding.tvFormat.text = videoFormat
                    binding.tvQuality.text = videoQuality
                }
                "audio" -> {
                    val videoFormat = qualityItem.substringAfter("mime_type=\"audio/").substringBefore("\"")
                    val videoQuality = qualityItem.substringAfter("abr=\"").substringBefore("\"")
                    binding.tvFormat.text = videoFormat
                    binding.tvQuality.text = videoQuality
                }
                else -> {
                    Log.e("QualityAdapter", "Type $videoType")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QualityListViewHolder {
        return QualityListViewHolder(MainQualityRvListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: QualityListViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(pyObject = currentItem)
        holder.binding.fabDownload.setOnClickListener {
            onDownloadClick(position)
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<PyObject>() {
        override fun areItemsTheSame(oldItem: PyObject, newItem: PyObject): Boolean {
            return oldItem.id() == newItem.id()
        }

        override fun areContentsTheSame(oldItem: PyObject, newItem: PyObject): Boolean {
            return oldItem == newItem
        }

    }
}