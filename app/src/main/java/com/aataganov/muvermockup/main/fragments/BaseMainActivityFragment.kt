package com.aataganov.muvermockup.main.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import com.aataganov.muvermockup.base.BaseFragment
import com.aataganov.muvermockup.main.ActivityMain
import com.aataganov.muvermockup.main.ViewModelMainActivity

abstract class BaseMainActivityFragment: BaseFragment() {

    lateinit var activityViewModel: ViewModelMainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityMain) {
            activityViewModel = ViewModelProviders.of(context).get(ViewModelMainActivity::class.java)
        } else {
            throw RuntimeException(context.toString() + " must be MainActivity")
        }
    }
}