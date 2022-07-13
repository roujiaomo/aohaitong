package com.aohaitong.ui.user.impermission

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.aohaitong.R
import com.aohaitong.base.BaseActivity
import com.aohaitong.databinding.ActivityImpermissionBinding
import com.aohaitong.kt.common.autoCleared
import com.aohaitong.kt.common.onClickWithAvoidRapidAction
import com.qmuiteam.qmui.skin.QMUISkinManager
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.MessageDialogBuilder
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import java.util.*


class IMPermissionActivity : BaseActivity() {

    companion object {
        const val PERMISSION_ALLOW = -1
    }

    private var binding: ActivityImpermissionBinding by autoCleared()
    private var isIgnoreBattery = false

    override fun getLayout() = R.layout.activity_impermission

    override fun initView() {
        binding = DataBindingUtil.setContentView(this, layout)
    }

    override fun initData() {
    }

    override fun initEvent() {
        binding.imgBack.onClickWithAvoidRapidAction {
            finish()
        }

        binding.tvSubTitle2Set.onClickWithAvoidRapidAction {
            requestIgnoreBattery()
        }

        binding.tvSubTitle3Set.onClickWithAvoidRapidAction {
            if (Build.BRAND.lowercase(Locale.getDefault()) == "huawei" || Build.BRAND.lowercase(
                    Locale.getDefault()
                ) == "honor"
            ) {
                goHuaweiSetting()
            } else {
                showAllowBackgroundDialog()
            }
        }
    }


    private fun goHuaweiSetting() {
        try {
            showActivity("com.huawei.systemmanager/com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")
        } catch (e: Exception) {
            showAllowBackgroundDialog()
        }
    }

    private fun showAllowBackgroundDialog() {
        val livePermissionDialog = MessageDialogBuilder(this)
            .setTitle(getString(R.string.im_permission_background_dialog_title))
            .setMessage(getString(R.string.im_permission_background_dialog_content))
            .setSkinManager(QMUISkinManager.defaultInstance(this))
            .addAction(
                0,
                "确定",
                QMUIDialogAction.ACTION_PROP_NEGATIVE
            ) { dialog: QMUIDialog, _: Int ->
                dialog.dismiss()
            }
            .create()
        livePermissionDialog.show()
    }

    private fun showActivity(permissionActivity: String) {
        val intent = Intent()
        var componentName: ComponentName? = null
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        componentName = ComponentName.unflattenFromString(permissionActivity) //跳自启动管理
        intent.component = componentName
        startActivity(intent)
    }

    @SuppressLint("BatteryLife")
    private fun requestIgnoreBattery() {
        if (!isIgnoreBattery) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        isIgnoreBattery = resultCode == PERMISSION_ALLOW
        setIgnoreBatteryStatus()
    }

    /**
     * 每次可见获取一遍电池权限
     */
    override fun onResume() {
        super.onResume()
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        isIgnoreBattery = powerManager.isIgnoringBatteryOptimizations(packageName)
        setIgnoreBatteryStatus()
    }

    private fun setIgnoreBatteryStatus() {
        if (isIgnoreBattery) {
            binding.tvSubTitle2Set.isEnabled = false
            binding.tvSubTitle2Set.background = null
            binding.tvSubTitle2Set.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.broadcast_text
                )
            )
            binding.tvSubTitle2Set.text = getString(R.string.im_permission_have_set)
        } else {
            binding.tvSubTitle2Set.isEnabled = true
            binding.tvSubTitle2Set.background =
                ContextCompat.getDrawable(this, R.drawable.shape_permission_set)
            binding.tvSubTitle2Set.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.tvSubTitle2Set.text = getString(R.string.im_permission_set)
        }
    }

    override fun isDataBinding() = true
}