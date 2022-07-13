package com.aohaitong.ui.chat

import android.graphics.Color
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.aohaitong.R
import com.aohaitong.base.BaseActivity
import com.aohaitong.bean.entity.PhotoDetailParams
import com.aohaitong.databinding.ActivityPhotoDetailBinding
import com.aohaitong.interfaces.OnChatItemClickListener
import com.aohaitong.kt.common.autoCleared
import com.aohaitong.ui.chat.adapter.PhotoDetailRvAdapter
import com.aohaitong.utils.StatusBarUtils

class PhotoDetailActivity : BaseActivity(), OnChatItemClickListener {

    companion object {
        const val PHOTO_PATH_LIST = "photo_path_list"
        const val CURRENT_PHOTO_PATH = "current_photo_path"
    }

    private var binding: ActivityPhotoDetailBinding by autoCleared()
    private val photoPathList = mutableListOf<String?>()
    private var currentPhotoPath: String? = null
    private lateinit var photoDetailRvAdapter: PhotoDetailRvAdapter
    override fun getLayout() = R.layout.activity_photo_detail

    override fun initView() {
        binding = DataBindingUtil.setContentView(this, layout)
        StatusBarUtils.setColor(this, Color.parseColor("#373743"))
        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    override fun initData() {
        val intentPhotoPathList =
            intent.getSerializableExtra(PHOTO_PATH_LIST) as MutableList<PhotoDetailParams>
        currentPhotoPath = intent.getStringExtra(CURRENT_PHOTO_PATH)
        if (intentPhotoPathList.isNotEmpty()) {
            intentPhotoPathList.forEach {
                photoPathList.add(it.filePath)
            }
        }
        var currentPosition = photoPathList.indexOf(currentPhotoPath)
        photoDetailRvAdapter =
            PhotoDetailRvAdapter(this, R.layout.item_chat_photo, intentPhotoPathList)
        binding.vpPhoto.adapter = photoDetailRvAdapter
        photoDetailRvAdapter.setOnItemClickListener(this)
        binding.vpPhoto.setCurrentItem(currentPosition, false)
        binding.vpPhoto.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                photoDetailRvAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun initEvent() {
    }

    override fun isDataBinding() = true
    override fun onItemClick(position: Int, viewId: Int) {
        finish()
    }

    override fun onItemLongClick(position: Int, view: View?, sendType: Int) {
    }

}