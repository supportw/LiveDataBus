package com.rapid.livedatabus

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * author: wcw
 * date: 2020/7/14
 * des: hook observe方法，实现粘性或非粘性Observer
 */
class StatefulLiveData<Any> : MutableLiveData<Any>() {
    @MainThread
    fun observe(owner: LifecycleOwner, observer: Observer<in Any>, sticky: Boolean = false) {
        observe(owner, observer)
        if (!sticky) {
            return
        }
        hook(owner, observer)
    }

    private fun hook(owner: LifecycleOwner, observer: Observer<in Any>) {
        val clazz = LiveData::class.java
        val mObservers = clazz.getDeclaredField("mObservers")
        mObservers.isAccessible = true
        val observers = mObservers[this]
        val observersClass: Class<*> = observers.javaClass
        val methodGet: Method = observersClass.getDeclaredMethod("get", kotlin.Any::class.java)
        methodGet.isAccessible = true
        val objectWrapperEntry = methodGet.invoke(observers, observer)
        var objectWrapper: kotlin.Any? = null
        if (objectWrapperEntry is Map.Entry<*, *>) {
            objectWrapper = objectWrapperEntry.value
        }
        if (objectWrapper == null) {
            throw  NullPointerException("ObserverWrapper can not be null")
        }
        val wrapperClass: Class<*>? = objectWrapper.javaClass.superclass
        val mLastVersion: Field = wrapperClass!!.getDeclaredField("mLastVersion")
        mLastVersion.isAccessible = true
        val mVersion: Field = clazz.getDeclaredField("mVersion")
        mVersion.isAccessible = true
        val mV = mVersion[this]
        mLastVersion.set(objectWrapper, mV)
    }

}