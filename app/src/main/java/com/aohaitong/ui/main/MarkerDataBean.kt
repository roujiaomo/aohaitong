package com.aohaitong.ui.main

import com.amap.api.maps.model.Marker

data class MarkerDataBean(
    var marker: Marker,
    var mmsi: String,
    var lon: Double,
    var lat: Double,
    var shipSpeed: Double,
    var shipHeading: Int
)