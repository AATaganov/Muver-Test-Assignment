package com.aataganov.muvermockup.base

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.FragmentManager
import android.util.Log
import java.util.*

abstract class BaseFragmentManager(private val supportFragmentManager: FragmentManager, private val listener: FragmentChangeListener) {

    companion object{
        val LOG_TAG = BaseFragmentManager::class.java.simpleName
    }
    interface FragmentChangeListener {
        fun updateHolderViews(containerFields: ContainerFragment, fragment: BaseFragment)
    }

    var canPerformTransactions: Boolean = false
    var currentFragment: BaseFragment? = null
        internal set;
    private val fragmentsTagsBackStack = ArrayList<String>()

    interface ContainerFragment{
        fun getFragmentClass(): Class<*>
        fun getTag(): String
    }

    abstract fun getByTag(tag: String): ContainerFragment?
    @IdRes
    abstract fun getFragmentContainerId(): Int
    abstract fun reset(): Boolean

    fun initFragments() {
        canPerformTransactions = true
        clearAllFragments()
        currentFragment = null
    }
    private fun setFragment(newFragmentClass: ContainerFragment, bundle: Bundle?) {
        try {
            val fragmentItStack = supportFragmentManager.findFragmentByTag(newFragmentClass.getTag()) as BaseFragment?

            currentFragment?.let{
                //if the other fragment is visible, hide it.
                supportFragmentManager.beginTransaction().hide(it).commitAllowingStateLoss()
            }
            if (fragmentItStack != null) {
                //if the fragment exists, parseBundle it.
                currentFragment = fragmentItStack
                showFragment(newFragmentClass, fragmentItStack, bundle)
            } else {
                //if the fragment does not exist, add it to fragment manager.
                currentFragment = addNewFragment(newFragmentClass, bundle)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w(LOG_TAG, "setFragment", e)
        }
    }
    private fun createFragment(containerFragment: ContainerFragment, bundle: Bundle?): BaseFragment? {
        var newFragment: BaseFragment? = null
        try {
            newFragment = containerFragment.getFragmentClass().newInstance() as BaseFragment
        } catch (e: InstantiationException) {
            e.printStackTrace()
            return null
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            return null
        }
        if (bundle != null) {
            newFragment.arguments = bundle
        }
        return newFragment
    }

    private fun addNewFragment(newFragmentClass: ContainerFragment, bundle: Bundle?): BaseFragment? {
        val newFragment = createFragment(newFragmentClass, bundle)
        if (newFragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            // Replace whatever is in the fragment_container view with this fragment
            transaction.add(getFragmentContainerId(), newFragment, newFragmentClass.getTag())
            // Commit the transaction
            transaction.commitAllowingStateLoss()
            listener.updateHolderViews(newFragmentClass, newFragment)
        }
        return newFragment
    }

    private fun showFragment(newFragmentClass: ContainerFragment, fragment: BaseFragment?, bundle: Bundle?) {
        if (fragment == null) {
            return
        }
        supportFragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss()
        fragment.onReshow(bundle)
        listener.updateHolderViews(newFragmentClass, fragment)
    }
    private fun removeFragmentFromBackStack(): Boolean {
        val previousFragment = fragmentsTagsBackStack[0] ?: return false;
        fragmentsTagsBackStack.removeAt(0)
        val container =
                getByTag(
                        previousFragment
                )
        if (container == null) {
            return reset()
        }
        setFragment(container, null)
        return false
    }
    internal fun clearBackStack() {
        fragmentsTagsBackStack.clear()
    }
    internal fun clearAllFragments() {
        clearBackStack()
        val transaction = supportFragmentManager.beginTransaction()
        for (fragment in supportFragmentManager.fragments) {
            fragment?.let {
                if(it is BaseFragment){
                    it.detachActions()
                }
                transaction.remove(it)
            }
        }
        transaction.commitNow()
    }
    fun moveBack() : Boolean{
        currentFragment?.let {
            if(!it.isReadyToCloseOnBackPress()){
                return false
            }
        }
        return if (fragmentsTagsBackStack.isNotEmpty()) {
            removeFragmentFromBackStack()
        } else {
            reset()
        }
    }
    fun addFragment(newFragmentClass: ContainerFragment, bundle: Bundle?) {
        if(!canPerformTransactions){
            return
        }
        currentFragment?.tag?.let {
            tag ->
            fragmentsTagsBackStack.add(0, tag)
        }
        setFragment(newFragmentClass, bundle)
    }
    private fun isCurrentFragmentClass(value: ContainerFragment): Boolean {
        val unwrapedFragment = currentFragment ?: return false;
        return unwrapedFragment::class.java.simpleName.equals(value.getTag())
    }
    fun resetFragment(newFragmentClass: ContainerFragment, bundle: Bundle? = null) {
        if(!canPerformTransactions){
            return
        }
        if (bundle == null && isCurrentFragmentClass(newFragmentClass)) {
            currentFragment?.scrollUp()
        } else {
            clearBackStack()
            setFragment(newFragmentClass, bundle)
        }
    }
}