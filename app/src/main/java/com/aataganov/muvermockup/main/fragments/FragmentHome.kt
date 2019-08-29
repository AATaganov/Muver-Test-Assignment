package com.aataganov.muvermockup.main.fragments

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.aataganov.muvermockup.Profile
import com.aataganov.muvermockup.R
import com.aataganov.muvermockup.adapters.AdapterAggregatorsList
import com.aataganov.muvermockup.main.DriverManagerUpdateStatuses
import com.aataganov.muvermockup.models.ModelAggregatorState
import com.aataganov.muvermockup.utils.DriverManagerUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class FragmentHome: BaseMainActivityFragment(), AdapterAggregatorsList.AggregatorAdapterListener {
    companion object{
        const val MAIN_CHECKBOX_CHANGE_DEBOUNCE_TIME = 1L
    }

    override fun onCheckChanged(item: ModelAggregatorState) {
        activityViewModel.updateApplicationState(item.name, item.isEnabled)
    }

    val adapter = AdapterAggregatorsList(this)
    val allAggregatorsCheckboxSubject = PublishSubject.create<Boolean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun buildRecreateBundle(): Bundle? {
        return null
    }

    override fun getToolbarTitle(): String {
        return getString(R.string.fragment_home_title)
    }

    override fun afterCreateViewForeground() {
        super.afterCreateViewForeground()
        initRecycler()
        subscribeToProfile()
        initMainCheckbox()
    }

    private fun initMainCheckbox() {
        checkbox_all_aggregators.setOnClickListener {
            allAggregatorsCheckboxSubject.onNext(checkbox_all_aggregators.isChecked)
        }
    }

    private fun initRecycler(){
        context?.let {
            recycler_aggregators.layoutManager = LinearLayoutManager(it, RecyclerView.VERTICAL,false)
            recycler_aggregators.setHasFixedSize(true)
            recycler_aggregators.adapter = adapter
        }
    }

    override fun subscribeToDefaultObservables() {
        super.subscribeToDefaultObservables()
        subscribeToDriverManagerChanges()
        refreshDriverManager()
        subscribeToMainCheckboxChanges()
    }

    private fun subscribeToMainCheckboxChanges(){
        defaultDisposeBag.add(allAggregatorsCheckboxSubject.subscribeOn(Schedulers.io())
            .debounce(MAIN_CHECKBOX_CHANGE_DEBOUNCE_TIME, TimeUnit.SECONDS)
            .subscribe ({ value ->
                activityViewModel.updateAllApplications(value)
            },{it.printStackTrace()}))
    }

    private fun subscribeToDriverManagerChanges(){
        defaultDisposeBag.add(activityViewModel.getDriverManagerSubscription()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ status ->
                if(status == DriverManagerUpdateStatuses.SINGLE_UPDATE_SUCCESS && adapter.dataList.isNotEmpty()){
                    return@subscribe
                }
                when (status){
                    DriverManagerUpdateStatuses.SINGLE_UPDATE_FAIL -> onSingleAggregatorUpdateFail()
                    DriverManagerUpdateStatuses.ALL_UPDATE_FAIL -> onAllAggregatorsUpdateFail()
                }
                refreshDriverManager()
            }, {
                it.printStackTrace()
            }))
    }

    private fun onAllAggregatorsUpdateFail() {
        showToast(getString(R.string.fragment_home_all_aggregators_state_change_fail))
        checkbox_all_aggregators.isChecked = !checkbox_all_aggregators.isChecked
    }

    private fun onSingleAggregatorUpdateFail() {
        showToast(getString(R.string.fragment_home_single_aggregator_state_change_fail))
    }

    private fun subscribeToProfile(){
        activityViewModel.getProfileLiveData().observe(this, Observer<Profile> { result ->
            result?.let {
                checkbox_all_aggregators.isEnabled = it.isEnabled
                adapter.updateCheckboxes(it.isEnabled)
                return@Observer
            }
        })
    }

    private fun refreshDriverManager() {
        fragmentScope.launch(Dispatchers.IO){
            val newState = activityViewModel.getDrivingManagerState()
            val statesList = DriverManagerUtil.buildAggregatorStateList(newState.applicationStates)
            withContext(Dispatchers.Main){
                adapter.updateData(statesList)
            }
        }
    }
}