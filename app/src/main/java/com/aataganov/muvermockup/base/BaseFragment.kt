package com.aataganov.muvermockup.base

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aataganov.muvermockup.R
import com.aataganov.muvermockup.helpers.CommonHelper
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*

abstract class BaseFragment: Fragment(){

    internal var fragmentsHolder: FragmentsHolder? = null
    internal var defaultDisposeBag: CompositeDisposable = CompositeDisposable()
    internal var longDisposeBag: CompositeDisposable = CompositeDisposable()

    private val fragmentJob = Job()
    var fragmentScope = CoroutineScope(Dispatchers.Main + fragmentJob)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentsHolder) {
            recreateLongDisposeBag()
            fragmentsHolder = context
        } else {
            throw RuntimeException(context.toString() + " must implement FragmentsHolder")
        }
    }
    override fun onDetach() {
        detachActions()
        super.onDetach()
    }

    override fun onDestroy() {
        detachActions()
        super.onDestroy()
    }

    fun detachActions(){
        fragmentsHolder = null
        CommonHelper.cancelJob(fragmentJob)
        CommonHelper.unsubscribeDisposeBag(defaultDisposeBag)
        CommonHelper.unsubscribeDisposable(longDisposeBag)
    }

    private fun recreateLongDisposeBag(){
        if(CommonHelper.isDisposed(longDisposeBag)){
            longDisposeBag = CompositeDisposable()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        launchAfterCreateTasks()
    }

    private fun launchAfterCreateTasks(){
        fragmentScope.launch {
            withContext(Dispatchers.Default) {
                backgroundPreparations()
            }
            afterCreateViewForeground()
            refreshAfterBundle()
            updateToolbar()
        }

    }

    private fun backgroundPreparations(){
        parseBundle(arguments)
        afterCreateViewBackground()
    }

    @LayoutRes
    abstract fun getLayoutId(): Int
    protected open fun afterCreateViewForeground(){}
    protected open fun parseBundle(bundle: Bundle?){}
    protected open fun refreshAfterBundle(){}
    protected open fun afterCreateViewBackground(){}
    open fun scrollUp(){}
    protected open fun subscribeToDefaultObservables(){}
    protected open fun unsubscribeFromDefaultObservables(){}
    protected open fun getToolbarTitle(): String{
        return getString(R.string.app_name)
    }
    abstract fun buildRecreateBundle(): Bundle?
    private fun updateToolbar(){
        setToolbarTitle(getToolbarTitle())
        setHasOptionsMenu(shouldShowOptionsMenu())
    }

    fun setToolbarTitle(@StringRes titleRes: Int){
        fragmentsHolder?.setToolbarTitle(titleRes)
    }

    fun setToolbarTitle(title: String){
        fragmentsHolder?.setToolbarTitle(title)
    }

    open fun shouldShowOptionsMenu(): Boolean {
        return false
    }

    open fun isReadyToCloseOnBackPress(): Boolean{
        return true
    }

    fun goBack() {
        fragmentsHolder?.goBack(this)
    }

    internal fun showToast(message: String) {
        fragmentsHolder?.showToast(message)
    }

    internal fun showToast(@StringRes messageId: Int) {
        fragmentsHolder?.showToast(messageId)
    }

    override fun onResume() {
        super.onResume()
        resetDisposeBag()
        subscribeToDefaultObservables()
    }

    private fun resetDisposeBag(){
        if(CommonHelper.isDisposed(defaultDisposeBag)) {
            CommonHelper.unsubscribeDisposeBag(defaultDisposeBag)
            defaultDisposeBag = CompositeDisposable()
        }
    }
    override fun onPause() {
        super.onPause()
        CommonHelper.unsubscribeDisposeBag(defaultDisposeBag)
        unsubscribeFromDefaultObservables()
    }
    protected open fun setDefaultValues() {
    }
    protected open fun onPopBackStack(){}

    fun onReshow(bundle: Bundle?) {
        if (activity == null) {
            return
        }
        updateToolbar()
        if (bundle == null) {
            onPopBackStack()
        } else {
            setDefaultValues()
            parseBundle(bundle)
            refreshAfterBundle()
        }
    }

    fun selectTab(tab: TabLayout.Tab?){
        if(tab?.isSelected == false){
            tab.select()
        }
    }
}


interface FragmentsHolder {
    fun logOut()
    fun goBack(caller: BaseFragment)
    fun setToolbarTitle(title: String)
    fun setToolbarTitle(@StringRes titleRes: Int)
    fun showToast(@StringRes resId: Int)
    fun showToast(message: String)
}