package com.aohaitong.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.aohaitong.R
import com.aohaitong.base.BaseFragment
import com.aohaitong.bean.MsgEntity
import com.aohaitong.databinding.FragmentSeaChartBinding
import com.baidu.mapapi.map.BaiduMap

class SeaChartFragment : BaseFragment() {

    private lateinit var mBinding: FragmentSeaChartBinding
    private lateinit var mBaiduMap: BaiduMap
    override fun setLayout() = R.layout.fragment_sea_chart
    private val permissionList = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, setLayout(), container, false)
        return mBinding.root
    }

    override fun initView() {
        val isAllGranted = checkPermissionAllGranted(
            permissionList
        )
        if (isAllGranted) {
            return
        }
        requestPermissions(
            permissionList,
            1
        )
        //船端或者没有网络加载离线地图,否则加载在线地图
//        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun initData() {
    }

    override fun initEvent() {
    }

    override fun onReceiveData(msgEntity: MsgEntity?) {

    }

    companion object {
        fun getInstance() = SeaChartFragment()
    }


    private fun checkPermissionAllGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == 1) {
//            var isAllGranted = true
//            for (grant in grantResults) {
//                if (grant != PackageManager.PERMISSION_GRANTED) {
//                    isAllGranted = false
//                    break
//                }
//            }
//            if (!isAllGranted) {
//                QMUIDialog.MessageDialogBuilder(requireActivity())
//                    .setTitle("请允许遨海通APP的定位权限,否则无法正常使用")
//                    .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
//                    .addAction(
//                        0,
//                        "确定",
//                        QMUIDialogAction.ACTION_PROP_NEGATIVE
//                    ) { dialog: QMUIDialog, index: Int ->
//                        dialog.dismiss()
//                    }.setCanceledOnTouchOutside(false)
//                    .setCancelable(false)
//                    .create().show()
//            }
//        }
    }
}