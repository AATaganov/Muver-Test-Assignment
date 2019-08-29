package com.aataganov.muvermockup.main

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.aataganov.muvermockup.*
import com.aataganov.muvermockup.base.BaseViewModel
import com.aataganov.muvermockup.singletones.UserInfoManager
import com.aataganov.muvermockup.singletones.UserInfoManagerImpl
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class ViewModelMainActivityImpl(application: Application): BaseViewModel(application),ViewModelMainActivity {
    companion object {
        private val LOG_TAG = ViewModelMainActivityImpl::class.java.simpleName
        const val TRIAL_CHANGE_TIMEOUT = 7L
    }
    private val apiManager = BackendApiImpl()
    private val userInfoManager: UserInfoManager = UserInfoManagerImpl.getInstance(application)
    private val disposeBag = CompositeDisposable()

    private val trialChangeSubject = PublishSubject.create<Boolean>()
    private val driverManagerUpdatesSubject = PublishSubject.create<DriverManagerUpdateStatuses>()
    private val aggregatorsSubject: BehaviorSubject<List<String>> = BehaviorSubject.createDefault(listOf("Yandex Taxi", "Gett"))
    private val drivingManager: DrivingManager = DrivingManagerImpl()

    var profileLiveData: MutableLiveData<Profile> = MutableLiveData()

    init {
        refreshProfile()
        subscribeToTrialChanges()
        subscribeToAggregatorsChange()
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

    private fun subscribeToAggregatorsChange(){
        disposeBag.add(aggregatorsSubject.subscribeOn(Schedulers.io())
            .subscribe ({
                drivingManager.updateApplications(it)
                driverManagerUpdatesSubject.onNext(DriverManagerUpdateStatuses.AGGREGATES_UPDATED)
            },{
                it.printStackTrace()
            }))
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

    override fun simulateTrialChangePush(value: Boolean){
        trialChangeSubject.onNext(value)
    }

    override fun updateApplicationState(name: String, state: Boolean){
        viewModelScope.launch(Dispatchers.IO){
            val result = drivingManager.executeCommand(DMAppCommand(name, state))
            when (result.isSuccess) {
                true -> driverManagerUpdatesSubject.onNext(DriverManagerUpdateStatuses.SINGLE_UPDATE_SUCCESS)
                else  -> driverManagerUpdatesSubject.onNext(DriverManagerUpdateStatuses.SINGLE_UPDATE_FAIL)
            }
        }
    }

    override fun updateAllApplications(state: Boolean){
        viewModelScope.launch(Dispatchers.IO){
            val result = drivingManager.executeCommand(DMAllCommand(state))
            when (result.isSuccess) {
                true -> driverManagerUpdatesSubject.onNext(DriverManagerUpdateStatuses.ALL_UPDATE_SUCCESS)
                else  -> driverManagerUpdatesSubject.onNext(DriverManagerUpdateStatuses.ALL_UPDATE_FAIL)
            }
        }
    }

    override fun getDriverManagerSubscription(): Observable<DriverManagerUpdateStatuses> {
        return driverManagerUpdatesSubject.subscribeOn(Schedulers.io())
    }

    override fun getProfileLiveData(): LiveData<Profile> {
        return profileLiveData
    }

    override suspend fun getDrivingManagerState(): DrivingManagerState {
        return drivingManager.getState()
    }

    override fun getUserPhone(): String {
        return userInfoManager.getPhone()
    }
}

interface ViewModelMainActivity{
    suspend fun getDrivingManagerState(): DrivingManagerState
    fun getUserPhone(): String
    fun getProfileLiveData(): LiveData<Profile>
    fun updateAllApplications(state: Boolean)
    fun updateApplicationState(name: String, state: Boolean)
    fun simulateTrialChangePush(value: Boolean)
    fun getDriverManagerSubscription(): Observable<DriverManagerUpdateStatuses>
}

enum class DriverManagerUpdateStatuses{
    AGGREGATES_UPDATED,
    SINGLE_UPDATE_SUCCESS,
    SINGLE_UPDATE_FAIL,
    ALL_UPDATE_SUCCESS,
    ALL_UPDATE_FAIL,
}