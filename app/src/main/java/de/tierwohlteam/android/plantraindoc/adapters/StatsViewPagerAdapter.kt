package de.tierwohlteam.android.plantraindoc.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class StatsViewPagerAdapter(fragmentManager: FragmentManager,
                            lifeCycle: Lifecycle,
                            private val fragments:ArrayList<Fragment>):
    FragmentStateAdapter(fragmentManager, lifeCycle) {

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

}