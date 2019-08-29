package com.aataganov.muvermockup.base

import android.os.Bundle
import android.support.v4.app.FragmentManager

abstract class BaseFragmentHolderActivity: BaseActivity(), FragmentsHolder, BaseFragmentManagerImpl.FragmentChangeListener {
    companion object{
        const val ARG_SELECTED_FRAGMENT_NAME = "FragmentName"
        const val ARG_SELECTED_FRAGMENT_BUNDLE = "FragmentBundle"
    }
    internal var holderFragmentManager: BaseFragmentManager? = null

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.run {
            holderFragmentManager?.getCurrentFragment()?.let {
                val container = holderFragmentManager?.getFragmentContainer(it::class.java.simpleName) ?: return@let
                putString(ARG_SELECTED_FRAGMENT_NAME,container.getClassName())
                putBundle(ARG_SELECTED_FRAGMENT_BUNDLE, it.buildRecreateBundle())
            }
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
                bundle ->
            onRestoreBundle(bundle)
        }
        super.onRestoreInstanceState(savedInstanceState)
    }
    private fun onRestoreBundle(restoreBundle: Bundle){
        restoreBundle.getString(ARG_SELECTED_FRAGMENT_NAME)?.let {
            val container = holderFragmentManager?.getFragmentContainer(it) ?: return@let
            holderFragmentManager?.addFragment(container, restoreBundle.getBundle(ARG_SELECTED_FRAGMENT_BUNDLE))
        }
    }

    override fun goBack(caller: BaseFragment) {
        holderFragmentManager?.getCurrentFragment()?.let {
            if(it == caller){
                onBackPressed()
            }
        }
    }

    abstract fun getFragmentManagerImplementation(supportFragmentManager: FragmentManager, listener: BaseFragmentManagerImpl.FragmentChangeListener): BaseFragmentManager
    internal fun initFragmentManager(){
        val fragmentManager = getFragmentManagerImplementation(supportFragmentManager, this)
        initRouter(fragmentManager)
        holderFragmentManager = fragmentManager
        initRouter(fragmentManager)
        holderFragmentManager?.initFragments()
    }

    abstract fun initRouter(fragmentManager: BaseFragmentManager)


    override fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun setToolbarTitle(titleRes: Int) {
        supportActionBar?.setTitle(titleRes)
    }

    override fun onResume() {
        checkIfFragmentsAreNotDisplayed()
        super.onResume()
    }

    private fun checkIfFragmentsAreNotDisplayed(){
        if(holderFragmentManager?.getCurrentFragment() == null){
            holderFragmentManager?.resetToRoot()
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        holderFragmentManager?.canPerformTransactions = true
    }

    override fun onStop() {
        holderFragmentManager?.canPerformTransactions = false
        super.onStop()
    }

    override fun onBackPressed() {
        val manager = holderFragmentManager ?: return
        if (!manager.canPerformTransactions || isFinishing) {
            return
        }
        if(manager.isReadyToFinish()){
            finish()
        } else {
            manager.moveBack()
        }
    }
}