package com.aataganov.muvermockup.main

import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.aataganov.muvermockup.R
import com.aataganov.muvermockup.base.*
import com.aataganov.muvermockup.main.fragments.MainActivityFragmentHolder
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import kotlinx.android.synthetic.main.activity_main.*

class ActivityMain : BaseFragmentHolderActivity(), MainActivityFragmentHolder {
    override fun getFragmentManagerImplementation(
        supportFragmentManager: FragmentManager,
        listener: BaseFragmentManagerImpl.FragmentChangeListener
    ): BaseFragmentManager {
        return FragmentManagerMainActivityImpl(supportFragmentManager, listener)
    }

    var fragmentsRouter: RouterMainActivity? = null
    private set
    private lateinit var mainActivityViewModel: ViewModelMainActivity
    override fun initRouter(fragmentManager: BaseFragmentManager) {
        fragmentsRouter = RouterMainActivityImpl(fragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initTabs()
        initFragmentManager()
        initToolbar()
    }

    private fun initToolbar() {
        setSupportActionBar(main_toolbar)
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
        if(index >= 0) {
            bottom_bar.setTabSelectedListener(null)
            bottom_bar.selectTab(index)
            initBottomBarSelectionListener()
        }
    }

    private fun onSelectionClicked(position: Int) {
        holderFragmentManager?.resetFragmentByIndex(position)
    }

    //function to show/hide toolbars.
    override fun updateHolderViews(
        containerFields: ContainerFragment,
        fragment: BaseFragment
    ) {
        if(containerFields is MainActivityFragmentsEnum){
            selectBottomBarIndexWithoutAction(containerFields.bottomIndex)
        }
    }
}
