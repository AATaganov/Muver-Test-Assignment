package com.aataganov.muvermockup.base

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.aataganov.muvermockup.helpers.CommonHelper
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class BaseViewModel(application: Application): AndroidViewModel(application) {
    private var viewModelJob = Job()
    internal val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val disposeBag = CompositeDisposable()

    override fun onCleared() {
        CommonHelper.unsubscribeDisposable(disposeBag)
        viewModelJob.cancel()
        super.onCleared()
    }
}