package com.aohaitong.ui.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.aohaitong.ui.user.login.LoginFragment
import com.aohaitong.ui.user.login.RegisterFragment

const val LOGIN_PAGE_INDEX = 0
const val REGISTER_PAGE_INDEX = 1

class UserLoginPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        LOGIN_PAGE_INDEX to { LoginFragment() },
        REGISTER_PAGE_INDEX to { RegisterFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}
