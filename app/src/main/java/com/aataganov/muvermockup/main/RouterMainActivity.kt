package com.aataganov.muvermockup.main

import android.os.Bundle
import com.aataganov.muvermockup.base.BaseFragmentManager
import java.lang.ref.WeakReference

class RouterMainActivityImpl(fragmentManager: BaseFragmentManager): RouterMainActivity {
    private val weakFragmentManager = WeakReference(fragmentManager)

    override fun openProfile(bundle: Bundle?) {
        weakFragmentManager.get()?.resetFragment(MainActivityFragmentsEnum.PROFILE,bundle)
    }

    override fun openHome(bundle: Bundle?) {
        weakFragmentManager.get()?.resetFragment(MainActivityFragmentsEnum.HOME,bundle)
    }
}

interface RouterMainActivity {
    fun openProfile(bundle: Bundle? = null)
    fun openHome(bundle: Bundle? = null)
}