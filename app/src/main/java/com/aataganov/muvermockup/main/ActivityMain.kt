package com.aataganov.muvermockup.main

import android.os.Bundle
import com.aataganov.muvermockup.R
import com.aataganov.muvermockup.base.BaseActivity
import com.aataganov.muvermockup.base.BaseFragmentManager
import com.aataganov.muvermockup.base.BaseFragment
import com.aataganov.muvermockup.base.FragmentsHolder
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import kotlinx.android.synthetic.main.activity_main.*

class ActivityMain : BaseActivity(), FragmentsHolder, BaseFragmentManager.FragmentChangeListener {
    companion object{
        const val ARG_SELECTED_FRAGMENT_NAME = "FragmentName"
        const val ARG_SELECTED_FRAGMENT_BUNDLE = "FragmentBundle"
    }

    private var myFragmentManager: FragmentManagerMainActivity? = null
    private lateinit var mainActivityViewModel: ViewModelMainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initTabs()
        initFragmentManager()
        initToolbar()
    }

    override fun goBack(caller: BaseFragment) {
        myFragmentManager?.currentFragment?.let {
            if(it == caller){
                onBackPressed()
            }
        }
    }

    override fun onResume() {
        checkIfFragmentsAreNotDisplayed()
        super.onResume()
    }

    private fun initToolbar() {
        setSupportActionBar(main_toolbar)
    }

    private fun initFragmentManager(){
        myFragmentManager =
            FragmentManagerMainActivity(supportFragmentManager, this)
        myFragmentManager?.initFragments()
    }
    private fun checkIfFragmentsAreNotDisplayed(){
        if(myFragmentManager?.currentFragment == null){
            myFragmentManager?.resetFragmentByIndex(0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.run {
            myFragmentManager?.currentFragment?.let {
                val container = FragmentManagerMainActivity.MainActivityFragmentEnum.getByTag(it.tag) ?: return@let
                putString(ARG_SELECTED_FRAGMENT_NAME,container.getTag())
                putBundle(ARG_SELECTED_FRAGMENT_BUNDLE, it.buildRecreateBundle())
            }
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
                bundle ->
            onRestoreBundle(bundle)
        }
        super.onRestoreInstanceState(savedInstanceState)
    }
    private fun onRestoreBundle(restoreBundle: Bundle){
        restoreBundle.getString(ARG_SELECTED_FRAGMENT_NAME)?.let {
            val container = FragmentManagerMainActivity.MainActivityFragmentEnum.getByTag(it) ?: return@let
            myFragmentManager?.addFragment(container, restoreBundle.getBundle(ARG_SELECTED_FRAGMENT_BUNDLE))
        }
    }

    override fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun setToolbarTitle(titleRes: Int) {
        supportActionBar?.setTitle(titleRes)
    }

    private fun initTabs() {
        bottom_bar.setMode(BottomNavigationBar.MODE_SHIFTING)
        bottom_bar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
        bottom_bar.setActiveColor(R.color.white)
            .setBarBackgroundColor(R.color.colorPrimary)
        bottom_bar
            .addItem(
                BottomNavigationItem(
                    R.drawable.ic_home,
                    R.string.main_activity_bottom_bar_home
                )
            )
            .addItem(
                BottomNavigationItem(
                    R.drawable.ic_person,
                    R.string.main_activity_bottom_bar_profile
                )
            )
            .initialise()
        bottom_bar.selectTab(0)
        initBottomBarSelectionListener()
    }

    private fun initBottomBarSelectionListener(){
        bottom_bar.setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
            override fun onTabSelected(position: Int) {
                onSelectionClicked(position)
            }

            override fun onTabUnselected(position: Int) {}

            override fun onTabReselected(position: Int) {
                onSelectionClicked(position)
            }
        })
    }

    private fun selectBottomBarIndexWithoutAction(index: Int){
        bottom_bar.setTabSelectedListener(null)
        bottom_bar.selectTab(index)
        initBottomBarSelectionListener()
    }

    private fun onSelectionClicked(position: Int) {
        myFragmentManager?.resetFragmentByIndex(position)
    }

    //function to show/hide toolbars.
    override fun updateItemsVisibility(
        containerFields: BaseFragmentManager.ContainerFragment,
        fragment: BaseFragment
    ) {

    }
}
