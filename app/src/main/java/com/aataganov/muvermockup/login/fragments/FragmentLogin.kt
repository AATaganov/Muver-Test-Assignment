package com.aataganov.muvermockup.login.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.aataganov.muvermockup.R
import com.aataganov.muvermockup.helpers.CommonHelper
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentLogin : BaseLoginActivityFragment() {
    companion object{
        const val KEY_PHONE = "Phone"
        fun buildBundle(phone: String?): Bundle {
            val bundle = Bundle()
            bundle.putString(KEY_PHONE,phone)
            return bundle
        }
    }
    private var phoneInBundle: String = ""

    override fun getLayoutId(): Int {
        return R.layout.fragment_login
    }

    override fun parseBundle(bundle: Bundle?) {
        super.parseBundle(bundle)
        bundle?.let {
            phoneInBundle = it.getString(KEY_PHONE) ?: ""
        }
    }

    override fun afterCreateViewForeground() {
        super.afterCreateViewForeground()
        initViews()
        hideLoadingIndicator()
    }

    override fun refreshAfterBundle() {
        super.refreshAfterBundle()
        edit_phone.setText(phoneInBundle)
    }

    override fun buildRecreateBundle(): Bundle? {
        return buildBundle(edit_phone.text.toString())
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

    private fun validate(){
        btn_login.isEnabled = edit_phone.text.toString().isNotEmpty()
    }

    private fun login() {
        val phone: String = edit_phone.text.toString()
        activity?.let {
            CommonHelper.hideKeyboard(it)
        }
        showLoadingIndicator()
        fragmentScope.launch(Dispatchers.IO) {
            val result = activityViewModel.tryToLogin(phone)
            if(result){
                loginFragmentHolder?.gotoMainActivity()
            } else {
                withContext(Dispatchers.Main){
                    showToast(R.string.fragment_login_fail_to_login)
                    hideLoadingIndicator()
                }
            }
        }
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