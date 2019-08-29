package com.aataganov.muvermockup.login

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.aataganov.muvermockup.R
import com.aataganov.muvermockup.base.*
import com.aataganov.muvermockup.login.fragments.LoginFragmentHolder
import com.aataganov.muvermockup.main.ActivityMain

class ActivityLogin : BaseFragmentHolderActivity(), LoginFragmentHolder, FragmentsHolder {

    override fun getFragmentManagerImplementation(
        supportFragmentManager: FragmentManager,
        listener: BaseFragmentManagerImpl.FragmentChangeListener
    ): BaseFragmentManager {
        return FragmentManagerLogin(supportFragmentManager, listener)
    }

    override fun initRouter(fragmentManager: BaseFragmentManager) {

    }

    override fun updateHolderViews(containerFields: ContainerFragment, fragment: BaseFragment) {

    }

    override fun goBack(caller: BaseFragment) {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initFragmentManager()
    }

    override fun gotoMainActivity() {
        val intent = Intent(this, ActivityMain::class.java)
        startActivity(intent)
        finish()
    }

}
