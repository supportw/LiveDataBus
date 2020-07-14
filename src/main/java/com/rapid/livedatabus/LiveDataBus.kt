package com.rapid.livedatabus

import androidx.lifecycle.MutableLiveData

/**
 * author: wcw
 * date: 2020/7/14
 * des:
 */
class LiveDataBus {
    companion object {
        private val dataCache: HashMap<String, StatefulLiveData<Any>> = HashMap()

        @Synchronized
        @JvmStatic
        fun getLiveData(channel: String): StatefulLiveData<Any> {
            if (!dataCache.containsKey(channel)) {
                val data = StatefulLiveData<Any>()
                dataCache.put(channel, data)
                return data
            }
            return dataCache[channel]!!
        }
    }
}