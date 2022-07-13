package com.aohaitong.ui.user.login

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.aohaitong.MyApplication
import com.aohaitong.R
import com.aohaitong.base.BaseFragment
import com.aohaitong.bean.MsgEntity
import com.aohaitong.business.BaseController
import com.aohaitong.business.IPController
import com.aohaitong.business.transmit.BusinessController
import com.aohaitong.business.transmit.ISendListener
import com.aohaitong.constant.CommonConstant
import com.aohaitong.constant.NumConstant
import com.aohaitong.constant.StatusConstant
import com.aohaitong.databinding.FragmentLoginBinding
import com.aohaitong.domain.connect.MQConnectController
import com.aohaitong.domain.connect.MQConnectListener
import com.aohaitong.kt.common.autoCleared
import com.aohaitong.kt.common.onClickWithAvoidRapidAction
import com.aohaitong.ui.main.MainActivity
import com.aohaitong.ui.user.LoginViewModel
import com.aohaitong.utils.SPUtil
import com.aohaitong.utils.SPUtil.ContentValue
import com.aohaitong.utils.TelUtil
import com.aohaitong.utils.dialog.MyQmuiDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private val viewModel by viewModels<LoginViewModel>()
    private var binding: FragmentLoginBinding by autoCleared()
    private var telephone: String? = ""
    private var password: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, setLayout(), container, false)
        return binding.root
    }

    override fun setLayout() = R.layout.fragment_login

    override fun initView() {
        binding.btnLogin.onClickWithAvoidRapidAction {
            telephone = binding.telephone
            password = binding.password
            checkLogin()
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        lifecycleScope.launch {
            viewModel.pingIp.collect {
                when (IPController.CONNECT_TYPE) {
                    StatusConstant.CONNECT_MQ -> {
                        MQConnectController.newInstance()
                            .initMQConnect(object : MQConnectListener {
                                override fun connectSuccess() {
                                    doMQLogin()
                                }

                                override fun connectFailed() {
                                    MyQmuiDialog.showErrorDialog(
                                        requireActivity(),
                                        getString(R.string.user_login_service_unable)
                                    )
                                    //停止服务
                                    MQConnectController.newInstance().stopConnect()
                                }
                            })
                    }
                    StatusConstant.CONNECT_SOCKET -> {

                    }
                    else -> {
                        loadingDialog.dismiss()
                        MyQmuiDialog.showErrorDialog(
                            requireActivity(),
                            getString(R.string.user_login_net_unable)
                        )
                    }
                }
            }
        }
    }

    private fun doMQLogin() {
        BusinessController.sendLogin(
            telephone,
            password,
            object : ISendListener {
                override fun sendSuccess() {
                    Log.e(CommonConstant.LOGCAT_TAG, "MQ登录成功")
                    loadingDialog.dismiss()
                    MyApplication.isHaveLogin = true
                    MyApplication.TEL = telephone!!.toLong()
                    MyApplication.PASSWORD = password
                    SPUtil.instance.putValues(
                        ContentValue(
                            CommonConstant.LOGIN_TEL,
                            MyApplication.TEL.toString() + ""
                        )
                    )
                    SPUtil.instance.putValues(
                        ContentValue(
                            CommonConstant.LOGIN_PASSWORD,
                            MyApplication.PASSWORD
                        )
                    )
                    BaseController.startHeartBeat()
                    BusinessController.sendGetFriendList(
                        SPUtil.instance.getLong(MyApplication.TEL.toString() + CommonConstant.LAST_UPDATE_TIME)
                            .toString() + "", null, NumConstant.getJHDNum()
                    )
                    BusinessController.sendGetGroupList(null, NumConstant.getJHDNum())
                    MainActivity.startMainActivity(requireContext())
                    requireActivity().finish()
                }

                override fun sendFail(reason: String) {
                    Log.e(CommonConstant.LOGCAT_TAG, "MQ登录失败：$reason")
                    loadingDialog.dismiss()
                    MyApplication.isHaveLogin = true
                    MyQmuiDialog.showErrorDialog(context as Activity, reason)
                    BaseController.logOut()
                }
            },
            NumConstant.getJHDNum()
        )
    }

    private fun checkLogin() {
        if (telephone.isNullOrEmpty()) {
            MyQmuiDialog.showErrorDialog(
                requireActivity(),
                getString(R.string.user_login_check_telephone_empty)
            )
            return
        }
        if (!TelUtil.isChinaPhoneLegal(telephone)) {
            MyQmuiDialog.showErrorDialog(
                requireActivity(),
                getString(R.string.user_login_check_telephone_normal)
            )
            return
        }
        if (password.isNullOrEmpty()) {
            MyQmuiDialog.showErrorDialog(
                requireActivity(),
                getString(R.string.user_login_check_password_empty)
            )
            return
        }
        telephone?.let {
            MyApplication.TEL = it.toLong()
        }
        showLoading()
        viewModel.doPingIp()
    }

    override fun onReceiveData(msgEntity: MsgEntity?) {

    }


    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}