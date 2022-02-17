package com.example.writableapp.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.writableapp.Fragments.SettingsFragment
import com.example.writableapp.Fragments.SprintFragment
import com.example.writableapp.Fragments.WorksFragment

class MainMenuViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    private val numberOfTabs = 3

    override fun getItemCount(): Int {
        return numberOfTabs
    }

    override fun createFragment(position: Int): Fragment {
        when(position) {
            0 -> return SprintFragment()
            1 -> return WorksFragment()
            2 -> return SettingsFragment()
        }
        return SprintFragment() // default fragment, but this part should never be reached anyway
    }
}