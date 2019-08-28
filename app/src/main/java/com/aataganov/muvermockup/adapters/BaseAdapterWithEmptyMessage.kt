package com.aataganov.muvermockup.adapters

import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aataganov.muvermockup.R

abstract class BaseAdapterWithEmptyMessage(@StringRes val emptyMessageResId: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TYPE_ITEM = 0
        const val TYPE_NO_DATA = -1
    }

    abstract fun createItemViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    abstract fun getDataListSize(): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ITEM) {
            return createItemViewHolder(parent)
        } else if (viewType == TYPE_NO_DATA) {
            val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_no_data_message, parent, false)
            return NoDataViewHolder(v)
        }
        throw RuntimeException("there is no type that matches the type $viewType + make sure your using types correctly")
    }
    override fun getItemViewType(position: Int): Int {
        return if (isContentMode()) {
            getItemType(position)
        } else TYPE_NO_DATA
    }

    open fun getItemType(position: Int): Int {
        return TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return if (isContentMode()) {
            getDataListSize()
        } else {
            1
        }
    }
    private fun isContentMode(): Boolean {
        return getDataListSize() > 0
    }

    inner class NoDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtMessage: TextView = itemView.findViewById(R.id.txt_message) as TextView
        init {
            txtMessage.setText(emptyMessageResId)
        }
    }
}