package com.aohaitong.ui.seachart

import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.aohaitong.R
import com.aohaitong.base.BaseActivity
import com.aohaitong.databinding.ActivityShipDetailBinding
import com.aohaitong.kt.common.autoCleared
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShipDetailActivity : BaseActivity() {
    private var binding: ActivityShipDetailBinding by autoCleared()
    private var groupieAdapter: GroupAdapter<GroupieViewHolder> by autoCleared()
    private lateinit var section: Section
    override fun getLayout() = R.layout.activity_ship_detail

    override fun initView() {
        binding = DataBindingUtil.setContentView(this, layout)
        initRecyclerView()
    }

    override fun initData() {
        val dataList = mutableListOf<String>()
        dataList.add("13112123993")
        dataList.add("15112164566")
        dataList.add("15112164886")
        dataList.add("18798556411")
        dataList.add("15112144776")

        section.update(
            dataList.map { userTel ->
                ShipMemberItem(this@ShipDetailActivity, userTel)
            })
    }

    override fun initEvent() {
    }

    private fun initRecyclerView() {
        groupieAdapter = GroupAdapter<GroupieViewHolder>()
        val rvLayoutManager = LinearLayoutManager(context)
        binding.rvContent.apply {
            layoutManager = rvLayoutManager
            adapter = groupieAdapter
            itemAnimator = null
        }
        section = Section()
        groupieAdapter.add(section)
    }

}