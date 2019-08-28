package com.aataganov.muvermockup.utils

import com.aataganov.muvermockup.models.ModelAggregatorState

class DriverManagerUtil {
    companion object{
        fun buildAggregatorStateList(applicationStates: HashMap<String, Boolean>): List<ModelAggregatorState>{
            val resultList: MutableList<ModelAggregatorState> = mutableListOf()
            applicationStates.keys.forEach { key ->
                resultList.add(ModelAggregatorState(key,applicationStates[key] ?: false))
            }
            return resultList.sortedBy { it.name }
        }
    }
}