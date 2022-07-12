package com.aohaitong.ui.user

import android.Manifest
import android.app.Activity
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.aohaitong.MyApplication
import com.aohaitong.R
import com.aohaitong.base.BaseActivity
import com.aohaitong.business.BaseController
import com.aohaitong.business.IPController
import com.aohaitong.business.transmit.BusinessController
import com.aohaitong.business.transmit.ISendListener
import com.aohaitong.business.transmit.SendController
import com.aohaitong.business.transmit.SocketController
import com.aohaitong.constant.CommonConstant
import com.aohaitong.constant.NumConstant
import com.aohaitong.constant.StatusConstant
import com.aohaitong.databinding.ActivityNewLoadBinding
import com.aohaitong.db.DBManager
import com.aohaitong.kt.util.autoCleared
import com.aohaitong.ui.main.MainActivity
import com.aohaitong.utils.LogcatUtil
import com.aohaitong.utils.SPUtil
import com.aohaitong.utils.StatusBarUtils
import com.aohaitong.utils.dialog.MyQmuiDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

@AndroidEntryPoint
class NewLoadActivity : BaseActivity(), EasyPermissions.PermissionCallbacks {
    private val viewModel by viewModels<LoginViewModel>()
    private var binding: ActivityNewLoadBinding by autoCleared()

    override fun getLayout() = R.layout.activity_new_load

    companion object {
        const val STORAGE_PERMISSION = 123
    }

    override fun initView() {
        setTheme(R.style.Theme_Aohaitong)
        binding = DataBindingUtil.setContentView(this, layout)
        when (SPUtil.instance.getString(CommonConstant.SP_CHOOSE_SKIN)) {
            "dusk" -> {
                StatusBarUtils.setColor(this, Color.parseColor("#FAC865"))
            }
            "night" -> {
                StatusBarUtils.setColor(this, Color.parseColor("#373743"))
            }
            else -> {
                StatusBarUtils.setColor(this, resources.getColor(R.color.white))
            }
        }
    }

    override fun initData() {
        LogcatUtil.getInstance(context).start()
        bindObserver()
        if (!hasStoragePermission()) {
            requestStoragePermission()
        }
        //是否需要自动登录
        if (TextUtils.isEmpty(SPUtil.instance.getString(CommonConstant.LOGIN_TEL))
            || TextUtils.isEmpty(SPUtil.instance.getString(CommonConstant.LOGIN_PASSWORD))
        ) {
            return
        }
        if (SPUtil.instance.getInt(CommonConstant.SP_LOGIN_NETWORK_TYPE) == -1) {
            return
        }
        //已登录
        MyApplication.TEL = SPUtil.instance.getString(CommonConstant.LOGIN_TEL).toLong()
        showLoading("登录中")
        viewModel.doPingIp()
    }

    private fun bindObserver() {
        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.pingIp.collect {
                Thread {
                    when (IPController.CONNECT_TYPE) {
                        StatusConstant.CONNECT_MQ -> {
                            doLoginMQ()
                        }
                        StatusConstant.CONNECT_SOCKET -> {
                            doLoginSocket()
                        }
                        else -> {
                            runOnUiThread {
                                loadingDialog.dismiss()
                                //主线程需要
                                MyQmuiDialog.showErrorDialog(this@NewLoadActivity, "当前网络不佳，请检查您的网络")
                            }
                        }
                    }
                }.start()
            }
//            }
        }


    }

    private fun doLoginSocket() {
        BaseController.startConnect()
        checkIsSocketConnect()
    }

    var socketFailTime = 0
    private fun checkIsSocketConnect() {
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d(
                CommonConstant.LOGCAT_TAG,
                "socket连接状态: " + SocketController.getInstance().isSocketStart
            )
            if (SocketController.getInstance().isSocketStart) {
                socketFailTime = 0
                BusinessController.sendLogin(
                    SPUtil.instance.getString(CommonConstant.LOGIN_TEL),
                    SPUtil.instance.getString(CommonConstant.LOGIN_PASSWORD),
                    object : ISendListener {
                        override fun sendSuccess() {
                            loadingDialog.dismiss()
                            MyApplication.TEL =
                                SPUtil.instance.getString(CommonConstant.LOGIN_TEL).toLong()
                            MyApplication.PASSWORD =
                                SPUtil.instance.getString(CommonConstant.LOGIN_PASSWORD)
                            MyApplication.isHaveLogin = true
                            SendController.groupIdList.clear()

                            val groupList = DBManager.getInstance(context)
                                .getGroupListByTel(MyApplication.TEL.toString())
                            groupList.map {
                                SendController.groupIdList.add(it.groupId)
                            }

                            BaseController.startHeartBeat()
                            MainActivity.startMainActivity(this@NewLoadActivity)
                            this@NewLoadActivity.finish()
                        }

                        override fun sendFail(reason: String) {
                            loadingDialog.dismiss()
                            MyApplication.isHaveLogin = false
                            MyQmuiDialog.showErrorDialog(context as Activity, reason)
                            //登录失败停止心跳与可能已连接的socket
                            BaseController.stopHeartBeat()
                            SocketController.getInstance().stopConnect()
                        }
                    },
                    NumConstant.getJHDNum()
                )
            } else {
                socketFailTime++
                if (socketFailTime < NumConstant.SOCKET_CONNECT_CHECK_COUNT) {
                    checkIsSocketConnect()
                } else {
                    loadingDialog.dismiss()
                    MyApplication.isHaveLogin = false
                    MyQmuiDialog.showErrorDialog(context as Activity, "网络异常")
                    BaseController.stopHeartBeat()
                    socketFailTime = 0
                }
            }
        }, 1000)
    }

    private fun doLoginMQ() {
        BaseController.startConnect()
        BusinessController.sendLogin(
            SPUtil.instance.getString(CommonConstant.LOGIN_TEL),
            SPUtil.instance.getString(CommonConstant.LOGIN_PASSWORD),
            object : ISendListener {
                override fun sendSuccess() {
                    hideLoading()
                    MyApplication.TEL = SPUtil.instance.getString(CommonConstant.LOGIN_TEL).toLong()
                    MyApplication.PASSWORD =
                        SPUtil.instance.getString(CommonConstant.LOGIN_PASSWORD)
                    BaseController.startHeartBeat()
                    BusinessController.sendGetFriendList(
                        SPUtil.instance.getLong(MyApplication.TEL.toString() + CommonConstant.LAST_UPDATE_TIME)
                            .toString() + "", null, NumConstant.getJHDNum()
                    )
                    BusinessController.sendGetGroupList(null, NumConstant.getJHDNum())


                    MyApplication.isHaveLogin = true
                    MainActivity.startMainActivity(this@NewLoadActivity)
                    this@NewLoadActivity.finish()
                }

                override fun sendFail(reason: String) {
                    hideLoading()
                    MyApplication.isHaveLogin = false
                    BaseController.logOut()
                    MyQmuiDialog.showErrorDialog(context as Activity, reason)
                }
            },
            NumConstant.getJHDNum()
        )
    }

    override fun initEvent() {
        binding.btnLogin.setOnClickListener { v: View? ->
            if (!hasStoragePermission()) {
                requestStoragePermission()
            } else {
                UserLoginActivity.startUserLoginActivity(
                    this@NewLoadActivity,
                    UserLoginActivity.loginStr
                )
            }

        }
        binding.btnRegister.setOnClickListener { v: View? ->
            if (!hasStoragePermission()) {
                requestStoragePermission()
            } else {
                UserLoginActivity.startUserLoginActivity(
                    this@NewLoadActivity,
                    UserLoginActivity.registerStr
                )
            }
        }
    }

    private fun hasStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(
            this, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private fun requestStoragePermission() {
        EasyPermissions.requestPermissions(
            PermissionRequest.Builder(
                this,
                STORAGE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
                .setRationale(R.string.storage_permission_deny)
                .setPositiveButtonText(R.string.chat_permission_ok)
                .setNegativeButtonText(R.string.chat_permission_cancel)
                .build()
        )
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
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String?>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).setRationale(R.string.storage_permission_final)
                .setTitle(R.string.permission_title)
                .build().show()
        }
    }

    override fun isDataBinding() = true
}