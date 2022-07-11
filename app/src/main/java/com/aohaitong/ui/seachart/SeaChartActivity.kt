package com.aohaitong.ui.seachart

import androidx.databinding.DataBindingUtil
import com.aohaitong.R
import com.aohaitong.base.BaseActivity
import com.aohaitong.databinding.ActivitySeaChartBinding
import com.aohaitong.kt.util.autoCleared

class SeaChartActivity : BaseActivity() {
    private var binding: ActivitySeaChartBinding by autoCleared()

    override fun getLayout() = R.layout.activity_sea_chart


    override fun initView() {
        binding = DataBindingUtil.setContentView(this, layout)
    }

    override fun initData() {
    }

    override fun initEvent() {
    }

    override fun isDataBinding() = true

}