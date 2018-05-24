package com.activities.historyListActivity.components.viewPager.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * Created by StarKRE on 23.05.2018.
 */

class RepositoryPagerAdapter(fragmentManager: FragmentManager
                             , private val fragments: List<Fragment>
                             , private val titles: List<String>)
    : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return this.fragments[position]
    }

    override fun getCount(): Int {
        return this.fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return this.titles[position]
    }
}