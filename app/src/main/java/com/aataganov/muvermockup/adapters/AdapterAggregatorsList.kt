package com.aataganov.muvermockup.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aataganov.muvermockup.R
import com.aataganov.muvermockup.models.ModelAggregatorState
import kotlinx.android.synthetic.main.list_item_aggregator.view.*
import java.lang.ref.WeakReference

class AdapterAggregatorsList(listener: AggregatorAdapterListener): BaseAdapterWithEmptyMessage(R.string.fragment_home_no_aggregators) {
    override fun createItemViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_aggregator, parent, false)
        return AggregatorViewHolder(view)
    }
    var isEnabled: Boolean = false
    var dataList: List<ModelAggregatorState> = emptyList()
    var weakListener = WeakReference(listener)

    override fun getDataListSize(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AggregatorViewHolder) {
            holder.update(position)
        }
    }

    fun updateData(statesList: List<ModelAggregatorState>) {
        dataList = statesList
        notifyDataSetChanged()
    }

    fun updateCheckboxes(enabled: Boolean){
        if(isEnabled != enabled){
            isEnabled = enabled
            notifyDataSetChanged()
        }
    }

    inner class AggregatorViewHolder(aggregatorView: View): RecyclerView.ViewHolder(aggregatorView){
        private val checkBox = aggregatorView.checkbox_is_enabled
        init {
            checkBox.setOnClickListener {
                val item = dataList.getOrNull(adapterPosition) ?: return@setOnClickListener
                item.isEnabled = checkBox.isChecked
                weakListener.get()?.onCheckChanged(item)
            }
        }

        fun update(position: Int){
            val item = dataList.getOrNull(position) ?: return
            checkBox.text = item.name
            checkBox.isChecked = item.isEnabled
            checkBox.isEnabled = isEnabled
        }
    }

    interface AggregatorAdapterListener{
        fun onCheckChanged(item: ModelAggregatorState)
    }
}