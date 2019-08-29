package com.aataganov.muvermockup.tutorial

import android.content.Intent
import android.os.Bundle
import com.aataganov.muvermockup.R
import com.aataganov.muvermockup.base.BaseActivity
import com.aataganov.muvermockup.main.ActivityMain
import com.aataganov.muvermockup.singletones.UserInfoManagerImpl
import kotlinx.android.synthetic.main.activity_tutorial.*

class ActivityTutorial : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(UserInfoManagerImpl.getInstance(this).isLoggedIn()){
            return gotoMainActivity()
        }
        setContentView(R.layout.activity_tutorial)
        initButtons()
    }

    private fun initButtons(){
        btn_continue.setOnClickListener {
            gotoLogin()
        }
    }

    private fun gotoMainActivity() {
        val intent = Intent(this, ActivityMain::class.java)
        startActivity(intent)
        finish()
    }
}
