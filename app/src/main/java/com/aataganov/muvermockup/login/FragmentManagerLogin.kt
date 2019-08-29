package com.aataganov.muvermockup.login

import android.support.v4.app.FragmentManager
import com.aataganov.muvermockup.R
import com.aataganov.muvermockup.base.BaseFragmentManagerImpl
import com.aataganov.muvermockup.base.ContainerFragment
import com.aataganov.muvermockup.login.fragments.FragmentLogin

class FragmentManagerLogin(supportFragmentManager: FragmentManager, listener: FragmentChangeListener) :
    BaseFragmentManagerImpl(supportFragmentManager, listener) {
    override fun getFragmentByIndex(index: Int): ContainerFragment? {
        return null
    }

    override fun initFragmentsMap(): Map<String, ContainerFragment> {
        return LoginActivityFragmentsEnum.values().associateBy { it.getClassName() }
    }

    override fun getFragmentContainerId(): Int {
        return R.id.fragment_container_login
    }

    override fun isReadyToFinish(): Boolean {
        return true
    }

    override fun resetToRoot() {
        resetFragment(LoginActivityFragmentsEnum.LOGIN)
    }
}

enum class LoginActivityFragmentsEnum(private val fragmentClassInstance: Class<*>): ContainerFragment{

    LOGIN(FragmentLogin::class.java);

    override fun getFragmentClass(): Class<*> {
        return fragmentClassInstance
    }
}