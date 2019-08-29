package com.aataganov.muvermockup.singletones

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class UserInfoManagerImpl private constructor(context: Context): UserInfoManager{
    companion object {
        private const val PREFS_NAME = "userInfoPrefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_PHONE = "phone"
        private const val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm"

        fun isTrialEnabledToken(token: String): Boolean {
            return token.length % 2 == 0
        }

        private fun getRandomCharForToken(): Char {
            val random = Random()
            return ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)]
        }

        private var instance: UserInfoManagerImpl? = null
        fun getInstance(context: Context): UserInfoManager {
            instance?.let {
                return it
            }
            val newInstance = UserInfoManagerImpl(context)
            instance = newInstance
            return newInstance
        }
    }

    private val prefs: SharedPreferences

    init {
        prefs = context.getSharedPreferences(PREFS_NAME,0)
    }

    override fun clearPrefs(){
        prefs.edit().clear().apply()
    }

    override fun updateUserInfo(token: String, phone: String){
        prefs.edit().putString(KEY_TOKEN,token).putString(KEY_PHONE,phone).apply()
    }
    private fun updateToken(newToken: String){
        prefs.edit().putString(KEY_TOKEN, newToken).apply()
    }

    override fun changeTokenTrial(trialEnabled: Boolean){
        val currentToken = getToken()
        if(isTrialEnabledToken(currentToken) == trialEnabled){
            return
        } else {
            updateToken("${getRandomCharForToken()}$currentToken")
        }
    }

    override fun getToken(): String{
        return prefs.getString(KEY_TOKEN, "") ?: ""
    }

    override fun getPhone(): String{
        return prefs.getString(KEY_PHONE, "") ?: ""
    }

    override fun isLoggedIn(): Boolean{
        return getToken().isNotBlank()
    }
}
interface UserInfoManager{
    fun isLoggedIn(): Boolean
    fun getToken(): String
    fun clearPrefs()
    fun changeTokenTrial(trialEnabled: Boolean)
    fun updateUserInfo(token: String, phone: String)
    fun getPhone(): String
}