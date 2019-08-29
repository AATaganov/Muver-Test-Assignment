package com.aataganov.muvermockup.main.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import com.aataganov.muvermockup.base.BaseFragment
import com.aataganov.muvermockup.base.FragmentsHolder
import com.aataganov.muvermockup.main.ActivityMain
import com.aataganov.muvermockup.main.RouterMainActivity
import com.aataganov.muvermockup.main.ViewModelMainActivity
import com.aataganov.muvermockup.main.ViewModelMainActivityImpl

abstract class BaseMainActivityFragment: BaseFragment() {

    internal var fragmentsRouter: RouterMainActivity?= null
    lateinit var activityViewModel: ViewModelMainActivity
    internal var fragmentsHolder: MainActivityFragmentHolder? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityMain) {
            activityViewModel = ViewModelProviders.of(context).get(ViewModelMainActivityImpl::class.java)
            fragmentsHolder = context
            fragmentsRouter = context.fragmentsRouter
        } else {
            throw RuntimeException(context.toString() + " must be MainActivity")
        }
    }

    override fun getFragmentsHolder(): FragmentsHolder? {
        return fragmentsHolder
    }

    override fun clearFragmentsHolder() {
        fragmentsHolder = null
    }

    override fun detachActions() {
        super.detachActions()
        fragmentsRouter = null
    }
}

interface MainActivityFragmentHolder: FragmentsHolder {
    fun logOut()
}