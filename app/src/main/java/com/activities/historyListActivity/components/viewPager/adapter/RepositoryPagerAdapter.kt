package com.activities.historyListActivity.components.viewPager.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * @author Игорь Гулькин 23.05.2018.
 *
 * Класс RepositoryPagerAdapter "устаревший" стандартный адаптер,
 * который отслеживает переключение между вкладками на HistoryListActivity
 */

class RepositoryPagerAdapter(fragmentManager: FragmentManager
                             , private val fragments: List<Fragment>
                             , private val titles: List<String>)
    : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment = this.fragments[position]

    override fun getCount(): Int = this.fragments.size

    override fun getPageTitle(position: Int): CharSequence? = this.titles[position]
}