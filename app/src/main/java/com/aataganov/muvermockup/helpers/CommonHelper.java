package com.aataganov.muvermockup.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import kotlinx.coroutines.Job;

public class CommonHelper {
    private static final long SIMULATE_FAIL_RATE = DateUtils.SECOND_IN_MILLIS / 3;
    public static void updateViewVisibility(View view, boolean show){
        if(show){
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
    public static void updateVisibilityByInvisibilty(View view, boolean show){
        if(show){
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics;
    }
    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }
    public static void unsubscribeObserver(DisposableObserver observer){
        if(isDisposed(observer)){
            return;
        }
        observer.dispose();
    }
    public static void unsubscribeDisposable(@Nullable Disposable disposable){
        if(isDisposed(disposable)){
            return;
        }
        disposable.dispose();
    }
    public static void unsubscribeDisposeBag(CompositeDisposable disposable){
        if(isDisposed(disposable)){
            return;
        }
        disposable.dispose();
    }
    public static void cancelJob(Job job){
        if(job != null && !job.isCancelled()){
            job.cancel();
        }
    }
    public static boolean isDisposed(CompositeDisposable disposable) {
        return (disposable == null || disposable.isDisposed());
    }
    public static boolean isDisposed(Disposable disposable){
        return (disposable == null || disposable.isDisposed());
    }
    public static boolean isDisposed(DisposableObserver observer){
        return (observer == null || observer.isDisposed());
    }
    public static boolean isBitmapNotRecycled(Bitmap bitmap){
        return (bitmap != null && !bitmap.isRecycled());
    }
    public static boolean isRecycled(Bitmap bitmap){
        return (bitmap == null || bitmap.isRecycled());
    }
    public static void recycleBitmap(Bitmap bitmap){
        if(isBitmapNotRecycled(bitmap)){
            bitmap.recycle();
        }
    }
    public static boolean simulateFail(){
        return System.currentTimeMillis() % DateUtils.SECOND_IN_MILLIS < SIMULATE_FAIL_RATE;
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isMainThread(){
        return Looper.getMainLooper() == Looper.myLooper();
    }
}
