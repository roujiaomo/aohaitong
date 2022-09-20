package com.aohaitong.ui.main

import android.Manifest
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.AMap.InfoWindowAdapter
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.LocationSource.OnLocationChangedListener
import com.amap.api.maps.model.*
import com.aohaitong.R
import com.aohaitong.base.BaseFragment
import com.aohaitong.bean.MsgEntity
import com.aohaitong.business.IPController
import com.aohaitong.constant.StatusConstant
import com.aohaitong.databinding.FragmentSeaChartBinding
import com.aohaitong.utils.PermissionUtils
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest


class SeaChartFragment : BaseFragment(), AMapLocationListener, EasyPermissions.PermissionCallbacks,
    LocationSource {

    private lateinit var mBinding: FragmentSeaChartBinding
    private lateinit var aMap: AMap
    private lateinit var myLocationStyle: MyLocationStyle

    //声明AMapLocationClient类对象，定位发起端
    private var mLocationClient: AMapLocationClient? = null
    private lateinit var mLocationOption: AMapLocationClientOption
    private var mListener: OnLocationChangedListener? = null
    private var markerList = mutableListOf<Marker>()


    override fun setLayout() = R.layout.fragment_sea_chart


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, setLayout(), container, false)
        mBinding.mapView.onCreate(savedInstanceState)
        return mBinding.root
    }

    override fun initView() {
        aMap = mBinding.mapView.map
        myLocationStyle =
            MyLocationStyle() //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)//定位一次，且将视角移动到地图中心点。
        myLocationStyle.interval(5000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.showMyLocation(false)
        aMap.setLocationSource(this)
//        aMap.isMyLocationEnabled = true // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.myLocationStyle = myLocationStyle //设置定位蓝点的Style
        aMap.uiSettings.isMyLocationButtonEnabled = true
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    override fun onReceiveData(msgEntity: MsgEntity?) {

    }

    private fun location() {
        //初始化定位
        mLocationClient = AMapLocationClient(requireContext())
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

    companion object {
        fun getInstance() = SeaChartFragment()
    }

    override fun onResume() {
        super.onResume()
        if (hasLocationPermission()) {
            mBinding.mapView.onResume()
            showMarker()
        } else {
            requestLocationPermission()
        }
    }

    private fun showMarker() {
        var latLng: LatLng
        var title = ""
        if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_SOCKET) {
            latLng = LatLng(38.7188, 121.3806)
            title = "船"
        } else {
            latLng = LatLng(38.8738, 120.632)
            title = "岸"
        }
        val marker: Marker =
            aMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory
                                .decodeResource(resources, R.drawable.ic_ship_png)
                        )
                    )
                    .title("mmsi")
                    .snippet("DefaultMarker")
            )
        var cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition(latLng, 8F, 0F, 0F))
        aMap.moveCamera(cameraUpdate) //地图移向指定区域
        val mMarkerListener =
            AMap.OnMarkerClickListener { marker ->
                if (marker.isInfoWindowShown) {
                    marker.hideInfoWindow()
                } else {
                    marker.showInfoWindow()
                }
                true // 返回:true 表示点击marker 后marker 不会移动到地图中心；返回false 表示点击marker 后marker 会自动移动到地图中心
            }
        aMap.setOnMarkerClickListener(mMarkerListener)

        val mAMapSpotAdapter: InfoWindowAdapter = object : InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                if ("" == marker.title || marker.title == null) {
                    return null
                }
                val infoContent: View =
                    layoutInflater.inflate(R.layout.layout_map_marker_detail, null)
                handleMarkerDetail(marker, infoContent)
                return infoContent
            }

            override fun getInfoContents(marker: Marker): View? {
                if ("" == marker.title || marker.title == null) {
                    return null
                }
                val infoContent: View =
                    layoutInflater.inflate(R.layout.layout_map_marker_detail, null)
                handleMarkerDetail(marker, infoContent)
                return infoContent
            }

            private fun handleMarkerDetail(marker: Marker, view: View) {
                Log.d("wwwww", "经度: ${marker.position}")
                val tvMmsi = view.findViewById<TextView>(R.id.tv_mmsi)
                val tvLon = view.findViewById<TextView>(R.id.tv_lon)
                val tvLat = view.findViewById<TextView>(R.id.tv_lat)
                val tvShipSpeed = view.findViewById<TextView>(R.id.tv_ship_speed)
                val tvShipHeading = view.findViewById<TextView>(R.id.tv_ship_heading)
                tvMmsi.text = getString(R.string.sea_map_mmsi) + 412225667
                tvLon.text = getString(R.string.sea_map_lon) + 38.7188
                tvLat.text = getString(R.string.sea_map_lat) + 121.3806
                tvShipSpeed.text = getString(R.string.sea_map_ship_speed) + 9.9
                tvShipHeading.text = getString(R.string.sea_map_ship_heading) + 94
            }
        }
        aMap.setInfoWindowAdapter(mAMapSpotAdapter)

    }

    override fun onPause() {
        super.onPause()
        mBinding.mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mBinding.mapView.onSaveInstanceState(outState)
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
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION,
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
                var latLng: LatLng
                var title = ""
                if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_SOCKET) {
                    latLng = LatLng(38.7188, 121.3806)
                    title = "船"
                } else {
                    latLng = LatLng(38.8738, 120.632)
                    title = "岸"
                }
                val marker: Marker =
                    aMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    BitmapFactory
                                        .decodeResource(resources, R.drawable.ic_ship_png)
                                )
                            )
                            .title(title)
                            .snippet("DefaultMarker")
                    )
//                marker.showInfoWindow()
                var cameraUpdate =
                    CameraUpdateFactory.newCameraPosition(CameraPosition(latLng, 12F, 0F, 0F))
                aMap.moveCamera(cameraUpdate) //地图移向指定区域
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

    override fun activate(onLocationChangeListener: OnLocationChangedListener?) {
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
}