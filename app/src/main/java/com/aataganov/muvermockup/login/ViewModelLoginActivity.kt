package com.aataganov.muvermockup.login

import android.app.Application
import android.text.format.DateUtils
import com.aataganov.muvermockup.BackendApiImpl
import com.aataganov.muvermockup.base.BaseViewModel
import com.aataganov.muvermockup.helpers.CommonHelper
import com.aataganov.muvermockup.singletones.UserInfoManager
import com.aataganov.muvermockup.singletones.UserInfoManagerImpl

class ViewModelLoginActivityImpl(application: Application): BaseViewModel(application), ViewModelLoginActivity {
    private val userInfoManager: UserInfoManager = UserInfoManagerImpl.getInstance(application)
    private val apiManager = BackendApiImpl()

    override suspend fun tryToLogin(phone: String): Boolean {
        val token = apiManager.login(phone, "12345",1)
        if(CommonHelper.simulateFail()){
            return false
        }
        userInfoManager.updateUserInfo(token.accessToken, phone)
        return true
    }
}
interface ViewModelLoginActivity {
    suspend fun tryToLogin(phone: String): Boolean
}