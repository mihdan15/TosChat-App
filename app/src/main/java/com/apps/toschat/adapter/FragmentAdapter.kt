package com.apps.toschat.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.apps.toschat.ProfileActivityOld
import com.apps.toschat.fragment.chatFragment
import com.apps.toschat.fragment.comunityFragment
import com.apps.toschat.fragment.ProfileFragment

class FragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0->{
                comunityFragment()
            }
            1->{
                chatFragment()
            }
            2->{
                ProfileFragment()

            }
            else->{
                chatFragment()
            }
        }
    }


}