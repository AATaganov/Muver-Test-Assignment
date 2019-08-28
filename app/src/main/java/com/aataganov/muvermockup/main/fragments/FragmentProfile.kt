package com.aataganov.muvermockup.main.fragments

import android.arch.lifecycle.Observer
import android.os.Bundle
import com.aataganov.muvermockup.Profile
import com.aataganov.muvermockup.R
import kotlinx.android.synthetic.main.fragment_profile.*

class FragmentProfile: BaseMainActivityFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_profile
    }

    override fun buildRecreateBundle(): Bundle? {
        return null
    }

    override fun afterCreateViewForeground() {
        super.afterCreateViewForeground()
        subscribeToProfile()
        initButtons()
    }

    override fun getToolbarTitle(): String {
        return getString(R.string.fragment_profile_title)
    }

    private fun initButtons(){
        checkbox_trial_mode.setOnClickListener {
            activityViewModel.simulateTrialChangePush(checkbox_trial_mode.isChecked)
        }
    }
    private fun subscribeToProfile(){
        activityViewModel.profileLiveData.observe(this, Observer<Profile> { result ->
            result?.let {
                updateData(it)
                return@Observer
            }
        })
    }

    private fun updateData(profile: Profile) {
        txt_profile_id.text = getString(R.string.fragment_profile_id_template, profile.id)
        txt_phone.text = getString(R.string.fragment_profile_phone_template, profile.phone)
        checkbox_trial_mode.isChecked = profile.isEnabled
        when(profile.isEnabled) {
            true -> txt_enabled.setText(R.string.fragment_profile_checkboxes_enabled)
            else -> txt_enabled.setText(R.string.fragment_profile_checkboxes_disabled)
        }
    }
}