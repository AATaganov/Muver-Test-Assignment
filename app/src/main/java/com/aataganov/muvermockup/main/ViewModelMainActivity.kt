package com.aataganov.muvermockup.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.aataganov.muvermockup.*
import com.aataganov.muvermockup.helpers.CommonHelper
import com.aataganov.muvermockup.singletones.UserInfoManager
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class ViewModelMainActivity(application: Application): AndroidViewModel(application) {
    companion object {
        private val LOG_TAG = ViewModelMainActivity::class.java.simpleName
        const val TRIAL_CHANGE_TIMEOUT = 7L
    }

    enum class DriverManagerUpdateStatuses{
        AGGREGATES_UPDATED,
        SINGLE_UPDATE_SUCCESS,
        SINGLE_UPDATE_FAIL,
        ALL_UPDATE_SUCCESS,
        ALL_UPDATE_FAIL,
    }

    private var viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val apiManager = BackendApiImpl()
    val userInfoManager = UserInfoManager.getInstance(application)
    val disposeBag = CompositeDisposable()

    private val trialChangeSubject = PublishSubject.create<Boolean>()
    private val driverManagerUpdatesSubject = PublishSubject.create<DriverManagerUpdateStatuses>()
    private val aggregatorsSubject: BehaviorSubject<List<String>> = BehaviorSubject.createDefault(listOf("Yandex Taxi", "Gett"))
    val drivingManager = DrivingManagerImpl()

    var profileLiveData: MutableLiveData<Profile> = MutableLiveData()

    init {
        refreshProfile()
        subscribeToTrialChanges()
        subscribeToAggregatorsChange()
    }

    override fun onCleared() {
        CommonHelper.unsubscribeDisposable(disposeBag)
        viewModelJob.cancel()
        super.onCleared()
    }



    private fun refreshProfile(){
        viewModelScope.launch{
            lateinit var newProfile:Profile
            withContext((Dispatchers.IO)){
                newProfile = apiManager.loadProfile(userInfoManager.getToken())
            }
            profileLiveData.postValue(newProfile)
        }
    }

    fun simulateTrialChangePush(value: Boolean){
        trialChangeSubject.onNext(value)
    }

    private fun subscribeToTrialChanges(){
        disposeBag.add(trialChangeSubject.subscribeOn(Schedulers.io())
            .debounce(TRIAL_CHANGE_TIMEOUT, TimeUnit.SECONDS)
            .observeOn(Schedulers.io())
            .subscribe(
                { result ->
                    userInfoManager.changeTokenTrial(result)
                    refreshProfile()
                },{
                    it.printStackTrace()
                }))
    }

    fun updateApplicationState(name: String, state: Boolean){
        viewModelScope.launch(Dispatchers.IO){
            val result = drivingManager.executeCommand(DMAppCommand(name, state))
            when (result.isSuccess) {
                true -> driverManagerUpdatesSubject.onNext(DriverManagerUpdateStatuses.SINGLE_UPDATE_SUCCESS)
                else  -> driverManagerUpdatesSubject.onNext(DriverManagerUpdateStatuses.SINGLE_UPDATE_FAIL)
            }
        }
    }

    fun updateAllApplications(state: Boolean){
        viewModelScope.launch(Dispatchers.IO){
            val result = drivingManager.executeCommand(DMAllCommand(state))
            when (result.isSuccess) {
                true -> driverManagerUpdatesSubject.onNext(DriverManagerUpdateStatuses.ALL_UPDATE_SUCCESS)
                else  -> driverManagerUpdatesSubject.onNext(DriverManagerUpdateStatuses.ALL_UPDATE_FAIL)
            }
        }
    }

    private fun subscribeToAggregatorsChange(){
        disposeBag.add(aggregatorsSubject.subscribeOn(Schedulers.io())
            .subscribe ({
                drivingManager.updateApplications(it)
                driverManagerUpdatesSubject.onNext(DriverManagerUpdateStatuses.AGGREGATES_UPDATED)
            },{
                it.printStackTrace()
            }))
    }

    fun getDriverManagerSubscription(): Observable<DriverManagerUpdateStatuses> {
        return driverManagerUpdatesSubject.subscribeOn(Schedulers.io())
    }
}