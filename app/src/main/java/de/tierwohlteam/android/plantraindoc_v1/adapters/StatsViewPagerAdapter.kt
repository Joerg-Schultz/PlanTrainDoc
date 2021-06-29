package de.tierwohlteam.android.plantraindoc_v1.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import de.tierwohlteam.android.plantraindoc_v1.fragments.TabLayoutFragments
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class StatsViewPagerAdapter(fragmentManager: FragmentManager,
                            lifeCycle: Lifecycle,
                            private val fragments:ArrayList<TabLayoutFragments>):
    FragmentStateAdapter(fragmentManager, lifeCycle) {

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

}