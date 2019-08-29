package com.aataganov.muvermockup.base

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.FragmentManager
import android.util.Log
import java.util.*

abstract class BaseFragmentManagerImpl(private val supportFragmentManager: FragmentManager, private val listener: FragmentChangeListener) : BaseFragmentManager {

    companion object{
        val LOG_TAG = BaseFragmentManagerImpl::class.java.simpleName
    }
    interface FragmentChangeListener {
        fun updateHolderViews(containerFields: ContainerFragment, fragment: BaseFragment)
    }

    override var canPerformTransactions: Boolean = false
    internal var currentFragment: BaseFragment? = null
    private val fragmentsTagsBackStack = ArrayList<String>()


    private var conteinersMap: Map<String, ContainerFragment> = initFragmentsMap()
    abstract fun initFragmentsMap(): Map<String, ContainerFragment>

    private fun getByClassName(tag: String): ContainerFragment?{
        return conteinersMap[tag]
    }

    @IdRes
    abstract fun getFragmentContainerId(): Int

    private fun setFragment(newFragmentClass: ContainerFragment, bundle: Bundle?) {
        try {
            val fragmentItStack = supportFragmentManager.findFragmentByTag(newFragmentClass.getClassName()) as BaseFragment?

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
        val newFragment: BaseFragment?
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
            transaction.add(getFragmentContainerId(), newFragment, newFragmentClass.getClassName())
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
    private fun removeFragmentFromBackStack(){
        val previousFragment = fragmentsTagsBackStack.getOrNull(0) ?: return
        fragmentsTagsBackStack.removeAt(0)
        val container = getByClassName(previousFragment) ?: return resetToRoot()
        setFragment(container, null)
        return
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
    internal fun isCurrentFragmentClass(value: ContainerFragment): Boolean {
        val strongFragment = currentFragment ?: return false
        return strongFragment::class.java.simpleName == value.getClassName()
    }

    override fun initFragments() {
        canPerformTransactions = true
        clearAllFragments()
        currentFragment = null
    }

    override fun moveBack(){
        currentFragment?.let {
            if(!it.isReadyToCloseOnBackPress()){
                return
            }
        }
        return if (fragmentsTagsBackStack.isNotEmpty()) {
            removeFragmentFromBackStack()
        } else {
            resetToRoot()
        }
    }

    override fun addFragment(newFragmentClass: ContainerFragment, bundle: Bundle?) {
        if(!canPerformTransactions){
            return
        }
        currentFragment?.tag?.let {
                tag ->
            fragmentsTagsBackStack.add(0, tag)
        }
        setFragment(newFragmentClass, bundle)
    }

    override fun resetFragment(newFragmentClass: ContainerFragment, bundle: Bundle?) {
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

    override fun getFragmentContainer(className: String): ContainerFragment? {
        return getByClassName(className)
    }

    override fun getCurrentFragment(): BaseFragment? {
        return currentFragment
    }

    override fun resetFragmentByIndex(position: Int) {
        getFragmentByIndex(position)?.let {
            resetFragment(it,null)
        }
    }

    abstract fun getFragmentByIndex(index: Int): ContainerFragment?
}

interface ContainerFragment{
    fun getFragmentClass(): Class<*>
    fun getClassName(): String{
        return getFragmentClass().simpleName
    }
}
interface BaseFragmentManager{
    var canPerformTransactions: Boolean
    fun resetToRoot()
    fun resetFragmentByIndex(position: Int)
    fun resetFragment(newFragmentClass: ContainerFragment, bundle: Bundle? = null)
    fun addFragment(newFragmentClass: ContainerFragment, bundle: Bundle?)
    fun moveBack()
    fun isReadyToFinish():Boolean
    fun initFragments()
    fun getCurrentFragment(): BaseFragment?
    fun getFragmentContainer(className: String): ContainerFragment?
}