package com.aataganov.muvermockup.main

import android.support.v4.app.FragmentManager
import com.aataganov.muvermockup.R
import com.aataganov.muvermockup.base.BaseFragmentManagerImpl
import com.aataganov.muvermockup.base.ContainerFragment
import com.aataganov.muvermockup.main.fragments.FragmentHome
import com.aataganov.muvermockup.main.fragments.FragmentProfile

class FragmentManagerMainActivityImpl(supportFragmentManager: FragmentManager, listener: FragmentChangeListener):
                            BaseFragmentManagerImpl(supportFragmentManager, listener) {
    override fun isReadyToFinish(): Boolean {
        return isCurrentFragmentClass(MainActivityFragmentsEnum.HOME)
    }

    companion object {
        const val BAR_INDEX_HOME: Int = 0
        const val BAR_INDEX_PROFILE: Int = 1
    }
    override fun initFragmentsMap(): Map<String, ContainerFragment> {
        return MainActivityFragmentsEnum.values().associateBy { it.getClassName() }
    }

    override fun getFragmentContainerId(): Int {
        return R.id.fragment_container_main_activity
    }

    override fun resetToRoot() {
        resetFragment(MainActivityFragmentsEnum.HOME)
    }

    override fun getFragmentByIndex(index: Int) : ContainerFragment?{
        return when(index){
            BAR_INDEX_HOME -> MainActivityFragmentsEnum.HOME
            BAR_INDEX_PROFILE -> MainActivityFragmentsEnum.PROFILE
            else -> null
        }
    }
}

enum class MainActivityFragmentsEnum(private val fragmentClassInstance: Class<*>, val bottomIndex: Int): ContainerFragment{

    HOME(FragmentHome::class.java, FragmentManagerMainActivityImpl.BAR_INDEX_HOME),
    PROFILE(FragmentProfile::class.java, FragmentManagerMainActivityImpl.BAR_INDEX_PROFILE);

    override fun getFragmentClass(): Class<*> {
        return fragmentClassInstance
    }
}