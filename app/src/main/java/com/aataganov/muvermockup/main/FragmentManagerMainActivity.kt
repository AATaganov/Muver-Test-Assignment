package com.aataganov.muvermockup.main

import android.support.v4.app.FragmentManager
import com.aataganov.muvermockup.R
import com.aataganov.muvermockup.base.BaseFragmentManager
import com.aataganov.muvermockup.main.fragments.FragmentHome
import com.aataganov.muvermockup.main.fragments.FragmentProfile

class FragmentManagerMainActivity(supportFragmentManager: FragmentManager,
                                  listener: FragmentChangeListener): BaseFragmentManager(supportFragmentManager, listener) {
    companion object {
        const val BAR_INDEX_HOME: Int = 0
        const val BAR_INDEX_PROFILE: Int = 1
    }
    enum class MainActivityFragmentEnum constructor(private val fragmentClassInstance: Class<*>): ContainerFragment{
        HOME(FragmentHome::class.java),
        PROFILE(FragmentProfile::class.java);

        override fun getFragmentClass(): Class<*> {
            return fragmentClassInstance
        }

        override fun getTag(): String {
            return fragmentClassInstance.simpleName
        }

        companion object {
            val map = values().associateBy(
                MainActivityFragmentEnum::getTag)

            fun getFragmentByIndex(index: Int) : ContainerFragment?{
                return when(index){
                    BAR_INDEX_HOME -> HOME
                    BAR_INDEX_PROFILE -> PROFILE
                    else -> null
                }
            }
            fun getByTag(value: String?): ContainerFragment?{
                val tag = value?: return null
                return map[tag]
            }
        }
        fun getBottomIndex(): Int {
            return when (this){
                HOME -> BAR_INDEX_HOME
                PROFILE -> BAR_INDEX_PROFILE
            }
        }
    }

    override fun getByTag(tag: String): ContainerFragment? {
        return MainActivityFragmentEnum.map[tag]
    }

    override fun getFragmentContainerId(): Int {
        return R.id.fragment_container_main_activity
    }

    override fun reset(): Boolean {
        return true
    }

    fun resetFragmentByIndex(position: Int) {
        MainActivityFragmentEnum.getFragmentByIndex(
            position
        )?.let {
            resetFragment(it,null)
        }
    }
}