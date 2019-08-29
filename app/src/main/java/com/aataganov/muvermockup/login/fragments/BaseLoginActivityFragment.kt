package com.aataganov.muvermockup.login.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import com.aataganov.muvermockup.base.BaseFragment
import com.aataganov.muvermockup.base.FragmentsHolder
import com.aataganov.muvermockup.login.ActivityLogin
import com.aataganov.muvermockup.login.ViewModelLoginActivity
import com.aataganov.muvermockup.login.ViewModelLoginActivityImpl

abstract class BaseLoginActivityFragment: BaseFragment() {

    lateinit var activityViewModel: ViewModelLoginActivity
    internal var loginFragmentHolder: LoginFragmentHolder? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityLogin) {
            activityViewModel = ViewModelProviders.of(context).get(ViewModelLoginActivityImpl::class.java)
            loginFragmentHolder = context
        } else {
            throw RuntimeException(context.toString() + " must be ${ActivityLogin::class.java.simpleName}")
        }
    }

    override fun getFragmentsHolder(): FragmentsHolder? {
        return loginFragmentHolder
    }

    override fun clearFragmentsHolder() {
        loginFragmentHolder = null
    }
}
interface LoginFragmentHolder: FragmentsHolder {
    fun gotoMainActivity()
}