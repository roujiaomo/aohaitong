package com.aohaitong.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.aohaitong.bean.MsgEntity
import com.aohaitong.business.BaseController
import com.aohaitong.constant.CommonConstant
import com.aohaitong.constant.StatusConstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus


@FlowPreview
class NetworkBroadcastReceiver :
    BroadcastReceiver() {

    init {

        CoroutineScope(Dispatchers.Main).launch {
            networkChange
                .collect { isConnected ->
                    //重连
                    Log.d(CommonConstant.LOGCAT_TAG, "网络监听状态：$isConnected")
                    BaseController.isNetWorkEnable = isConnected
                    if (!isConnected) {
                        EventBus.getDefault().post(MsgEntity("", StatusConstant.TYPE_NO_NET))

                    }
//                    BaseController.onConnectStop()
                }
        }
    }

    private var networkChange =
        MutableSharedFlow<Boolean>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun onReceive(context: Context?, intent: Intent?) {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var isConnected = false
        connectivityManager.allNetworks.forEach {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(it)
            if (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                isConnected = true
            }
        }
        networkChange.tryEmit(isConnected)

    }
}