package com.aohaitong.ui.user.login

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import com.aohaitong.R
import com.aohaitong.base.BaseActivity
import com.aohaitong.databinding.ActivityNewLoginBinding
import com.aohaitong.kt.common.autoCleared
import com.aohaitong.ui.adapter.LOGIN_PAGE_INDEX
import com.aohaitong.ui.adapter.REGISTER_PAGE_INDEX
import com.aohaitong.ui.adapter.UserLoginPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewLoginActivity : BaseActivity() {

    private var binding: ActivityNewLoginBinding by autoCleared()

    override fun getLayout() = R.layout.activity_new_login

    override fun initView() {
        binding = DataBindingUtil.setContentView(this, layout)
        binding.vpLogin.adapter = UserLoginPagerAdapter(this)
        TabLayoutMediator(binding.tabLogin, binding.vpLogin) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
        val tabIndex = intent.getIntExtra(INTENT_TAB_TYPE, 0)
        binding.vpLogin.currentItem = tabIndex
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    override fun isDataBinding() = true

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            LOGIN_PAGE_INDEX -> getString(R.string.user_login_login_tab)
            REGISTER_PAGE_INDEX -> getString(R.string.user_login_register_tab)
            else -> null
        }
    }

    companion object {
        const val INTENT_TAB_TYPE = "intent_tab_type"
        const val INTENT_TAB_LOGIN = 0
        const val INTENT_TAB_REGISTER = 1

        fun getActivityIntent(context: Context, tabType: Int): Intent {
            val intent = Intent(context, NewLoginActivity::class.java)
            intent.putExtra(INTENT_TAB_TYPE, tabType)
            return intent
        }
    }
}