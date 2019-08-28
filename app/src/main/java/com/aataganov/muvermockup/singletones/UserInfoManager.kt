package com.aataganov.muvermockup.singletones

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class UserInfoManager private constructor(context: Context) {
    companion object{
        private const val PREFS_NAME = "userInfoPrefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_PHONE = "phone"
        private const val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm"
        private var instance: UserInfoManager? = null

        fun getInstance(context: Context): UserInfoManager{
            instance?.let {
                return it
            }
            val newInstance = UserInfoManager(context)
            instance = newInstance
            return newInstance
        }

        fun isTrialEnabledToken(token: String): Boolean{
            return token.length % 2 == 0
        }
        private fun getRandomCharForToken(): Char {
            val random = Random()
            return ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)]
        }
    }

    private val prefs: SharedPreferences

    init {
        prefs = context.getSharedPreferences(PREFS_NAME,0)
    }

    fun clearPrefs(){
        prefs.edit().clear().apply()
    }

    fun updateToken(newToken: String){
        prefs.edit().putString(KEY_TOKEN, newToken).apply()
    }

    fun changeTokenTrial(trialEnabled: Boolean){
        val currentToken = getToken()
        if(isTrialEnabledToken(currentToken) == trialEnabled){
            return
        } else {
            updateToken("${getRandomCharForToken()}$currentToken")
        }
    }

    fun getToken(): String{
        return prefs.getString(KEY_TOKEN, "") ?: ""
    }

    fun isLoggedIn(): Boolean{
        return getToken().isNotBlank()
    }


}