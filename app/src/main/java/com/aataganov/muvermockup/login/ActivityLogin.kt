package com.aataganov.muvermockup.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.aataganov.muvermockup.BackendApiImpl
import com.aataganov.muvermockup.R
import com.aataganov.muvermockup.base.BaseActivity
import com.aataganov.muvermockup.main.ActivityMain
import com.aataganov.muvermockup.singletones.UserInfoManager
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivityLogin : BaseActivity() {
    companion object{
        const val KEY_PHONE = "Phone"
    }

    private val apiManager = BackendApiImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        hideLoadingIndicator()
        initViews()
    }
    private fun initViews(){
        btn_login.isEnabled = false
        btn_login.setOnClickListener {
            login()
        }
        edit_phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                validate()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString(KEY_PHONE, edit_phone.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        edit_phone.setText(savedInstanceState?.getString(KEY_PHONE))
    }

    private fun validate(){
        btn_login.isEnabled = edit_phone.text.toString().isNotEmpty()
    }

    private fun login() {
        val phone: String = edit_phone.text.toString()
        showLoadingIndicator()
        activityScope.launch(Dispatchers.IO) {
            val token = apiManager.login(phone, "12345",1)
            userInfoManager.updateUserInfo(token.accessToken, phone)
            gotoMainActivity()
        }
    }

    private fun gotoMainActivity() {
        val intent = Intent(this, ActivityMain::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoadingIndicator(){
        progress_login.visibility = View.VISIBLE
        edit_phone.visibility = View.GONE
        btn_login.visibility = View.GONE
    }
    private fun hideLoadingIndicator(){
        progress_login.visibility = View.GONE
        edit_phone.visibility = View.VISIBLE
        btn_login.visibility = View.VISIBLE
    }
}
