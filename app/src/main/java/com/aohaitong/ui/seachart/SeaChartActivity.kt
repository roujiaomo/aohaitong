package com.aohaitong.ui.seachart

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.LocationSource
import com.amap.api.maps.model.MyLocationStyle
import com.aohaitong.R
import com.aohaitong.base.BaseActivity
import com.aohaitong.databinding.ActivitySeaChartBinding
import com.aohaitong.kt.common.autoCleared
import com.aohaitong.utils.PermissionUtils
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

class SeaChartActivity : BaseActivity(), AMapLocationListener, EasyPermissions.PermissionCallbacks,
    LocationSource {
    private var binding: ActivitySeaChartBinding by autoCleared()

    //声明AMapLocationClient类对象，定位发起端
    private var mLocationClient: AMapLocationClient? = null
    private lateinit var mLocationOption: AMapLocationClientOption
    private var mListener: LocationSource.OnLocationChangedListener? = null
    private lateinit var aMap: AMap
    private lateinit var myLocationStyle: MyLocationStyle

    override fun getLayout() = R.layout.activity_sea_chart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layout)
        binding.mapView.onCreate(savedInstanceState)
    }

    override fun initView() {
        myLocationStyle =
            MyLocationStyle() //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)//定位一次，且将视角移动到地图中心点。
        myLocationStyle.interval(2000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.showMyLocation(true)
        aMap.setLocationSource(this)
        aMap.isMyLocationEnabled = true // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.myLocationStyle = myLocationStyle //设置定位蓝点的Style
        aMap.uiSettings.isMyLocationButtonEnabled = true

        if (hasLocationPermission()) {
            binding.mapView.onResume()
            location()
        } else {
            requestLocationPermission()
        }
    }

    override fun initData() {
    }

    override fun initEvent() {
    }

    private fun location() {
        //初始化定位
        mLocationClient = AMapLocationClient(this)
        //初始化定位参数
        mLocationOption = AMapLocationClientOption()
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.isNeedAddress = true
        //设置是否只定位一次,默认为false
        mLocationOption.isOnceLocation = false
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.isMockEnable = true
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.interval = 10 * 1000
        mLocationClient?.let {
            //设置定位回调监听
            it.setLocationListener(this)
            //给定位客户端对象设置定位参数
            it.setLocationOption(mLocationOption)
            //启动定位
            it.startLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String?>) {
        location()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String?>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).setRationale(
                when (requestCode) {
                    PermissionUtils.LOCATION_PERMISSION -> {
                        getString(R.string.location_permission_final)
                    }
                    else -> {
                        ""
                    }
                }
            )
                .setTitle(R.string.permission_title)
                .build().show()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(
            this, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }


    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            PermissionRequest.Builder(
                this,
                PermissionUtils.LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
                .setRationale(R.string.location_permission_deny)
                .setPositiveButtonText(R.string.chat_permission_ok)
                .setNegativeButtonText(R.string.chat_permission_cancel)
                .build()
        )
    }


    /**
     * 定位回调
     */
    override fun onLocationChanged(aMapLocation: AMapLocation?) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.errorCode === 0) {
                mListener?.onLocationChanged(aMapLocation) // 显示系统小蓝点
                Log.d("wwwww", "地址: " + aMapLocation.address)
                Log.d("wwwww", "经纬度: " + aMapLocation.longitude)
                Log.d("wwwww", "经纬度: " + aMapLocation.latitude)
            } else {
                val errText = "定位失败," + aMapLocation.errorCode
                    .toString() + ": " + aMapLocation.errorInfo
                Log.e("wwwww", errText)
            }
        }
    }

    override fun activate(onLocationChangeListener: LocationSource.OnLocationChangedListener?) {
        mListener = onLocationChangeListener
        location()
    }

    override fun deactivate() {
        mListener = null
        if (mLocationClient != null) {
            mLocationClient?.stopLocation()
            mLocationClient?.onDestroy()
        }
        mLocationClient = null
    }

    override fun isDataBinding() = true

}