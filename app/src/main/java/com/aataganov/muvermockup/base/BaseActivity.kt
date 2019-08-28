package com.aataganov.muvermockup.base

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.aataganov.muvermockup.helpers.CommonHelper
import com.aataganov.muvermockup.login.ActivityLogin
import com.aataganov.muvermockup.singletones.UserInfoManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BaseActivity : AppCompatActivity() {
    internal var defaultBag: CompositeDisposable = CompositeDisposable()
    private lateinit var connectivityManager:ConnectivityManager
    private val activityJob = Job()
    internal lateinit var userInfoManager: UserInfoManager
    var activityScope = CoroutineScope(Dispatchers.Main + activityJob)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfoManager = UserInfoManager.getInstance(this)
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onDestroy() {
        super.onDestroy()
        activityJob.cancel()
        CommonHelper.unsubscribeDisposeBag(defaultBag)
    }

    fun showToast(@StringRes resId: Int) {
        activityScope.launch {
            Toast.makeText(this@BaseActivity, resId, Toast.LENGTH_LONG).show()
        }
    }

    fun showToast(message: String) {
        activityScope.launch {
            Toast.makeText(this@BaseActivity, message, Toast.LENGTH_LONG).show()
        }
    }

    fun showOkCancelDialog(@StringRes messageId: Int, @StringRes okId: Int, @StringRes cancelId: Int, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener) {
        defaultBag.add(Single.fromCallable<AlertDialog.Builder> { buildDialog(messageId, okId, cancelId, okListener, cancelListener) }.observeOn(AndroidSchedulers.mainThread()).subscribe { dialog, error ->
            if (!isFinishing) {
                dialog?.show()
            }
            error?.printStackTrace()
        })
    }

    fun show3ButtonsDialog(@StringRes messageId: Int, @StringRes okId: Int, @StringRes cancelId: Int, @StringRes neutralId: Int, okListener: DialogInterface.OnClickListener,
                           cancelListener: DialogInterface.OnClickListener,
                           neutralListener: DialogInterface.OnClickListener) {
        defaultBag.add(Single.fromCallable<AlertDialog.Builder> { buildDialog3Buttons(messageId, okId, cancelId, neutralId, okListener, cancelListener, neutralListener) }.observeOn(AndroidSchedulers.mainThread()).subscribe { dialog, error ->
            if (!isFinishing) {
                dialog?.show()
            }
            error?.printStackTrace()
        })
    }

    fun show3ButtonsDialog(message: String, @StringRes okId: Int, @StringRes cancelId: Int, @StringRes neutralId: Int, okListener: DialogInterface.OnClickListener,
                           cancelListener: DialogInterface.OnClickListener,
                           neutralListener: DialogInterface.OnClickListener) {
        defaultBag.add(Single.fromCallable<AlertDialog.Builder> { buildDialog3Buttons(message, okId, cancelId, neutralId, okListener, cancelListener, neutralListener) }.observeOn(AndroidSchedulers.mainThread()).subscribe { dialog, error ->
            if (!isFinishing) {
                dialog?.show()
            }
            error?.printStackTrace()
        })
    }

    fun showOkCancelDialog(message: String, @StringRes okId: Int, @StringRes cancelId: Int, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener) {
        defaultBag.add(Single.fromCallable<AlertDialog.Builder> { buildDialog(message, okId, cancelId, okListener, cancelListener) }.observeOn(AndroidSchedulers.mainThread()).subscribe { dialog, error ->
            if (!isFinishing) {
                dialog?.show()
            }
            error?.printStackTrace()
        })
    }

    private fun buildDialog(@StringRes messageId: Int, @StringRes okId: Int, @StringRes cancelId: Int, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener): AlertDialog.Builder {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage(messageId)
        dialog.setPositiveButton(okId, okListener)
        dialog.setNegativeButton(cancelId, cancelListener)
        return dialog
    }

    private fun buildDialog(message: String, @StringRes okId: Int, @StringRes cancelId: Int, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener): AlertDialog.Builder {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage(message)
        dialog.setPositiveButton(okId, okListener)
        dialog.setNegativeButton(cancelId, cancelListener)
        return dialog
    }

    private fun buildDialog3Buttons(@StringRes messageId: Int, @StringRes okId: Int, @StringRes cancelId: Int, @StringRes neutralId: Int,
                                    okListener: DialogInterface.OnClickListener,
                                    cancelListener: DialogInterface.OnClickListener,
                                    neutralListener: DialogInterface.OnClickListener): AlertDialog.Builder {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage(messageId)
        dialog.setPositiveButton(okId, okListener)
        dialog.setNegativeButton(cancelId, cancelListener)
        dialog.setNeutralButton(neutralId, neutralListener)
        return dialog
    }

    private fun buildDialog3Buttons(message: String, @StringRes okId: Int, @StringRes cancelId: Int, @StringRes neutralId: Int,
                                    okListener: DialogInterface.OnClickListener,
                                    cancelListener: DialogInterface.OnClickListener,
                                    neutralListener: DialogInterface.OnClickListener): AlertDialog.Builder {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage(message)
        dialog.setPositiveButton(okId, okListener)
        dialog.setNegativeButton(cancelId, cancelListener)
        dialog.setNeutralButton(neutralId, neutralListener)
        return dialog
    }

    open fun logOut() {
        userInfoManager.clearPrefs()
        gotoLogin()
    }

    fun gotoLogin(){
        val intent = Intent(this, ActivityLogin::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

}
