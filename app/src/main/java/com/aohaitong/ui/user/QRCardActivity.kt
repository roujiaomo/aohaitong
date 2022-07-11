package com.aohaitong.ui.user

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.aohaitong.MyApplication
import com.aohaitong.R
import com.aohaitong.base.BaseActivity
import com.aohaitong.bean.entity.QrCodeInfo
import com.aohaitong.databinding.ActivityQrcardBinding
import com.aohaitong.kt.util.autoCleared
import com.aohaitong.kt.util.onClickWithAvoidRapidAction
import com.aohaitong.utils.CommonUtil
import com.aohaitong.utils.SPUtil
import com.google.gson.Gson
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.hmsscankit.WriterException
import com.huawei.hms.ml.scan.HmsBuildBitmapOption
import com.huawei.hms.ml.scan.HmsScan
import com.qmuiteam.qmui.util.QMUIDisplayHelper


class QRCardActivity : BaseActivity() {

    private var binding: ActivityQrcardBinding by autoCleared()
    private var name: String? = ""
    private var telephone: String? = ""

    override fun getLayout() = R.layout.activity_qrcard

    override fun initView() {
        binding = DataBindingUtil.setContentView(this, layout)
    }

    override fun initData() {
        binding.includeHead.tvTitle.text = "二维码名片"
        telephone = MyApplication.TEL.toString()
        name = SPUtil.instance.getString(MyApplication.TEL.toString() + "")
        binding.isShowName = !name.isNullOrEmpty()
        name?.let {
            binding.tvName.text = it
        }
        binding.telephone = telephone
        generateQrCode()
    }

    override fun initEvent() {
        binding.includeHead.imgBack.onClickWithAvoidRapidAction {
            finish()
        }
    }

    private fun generateQrCode() {
        val qrCodeInfo =
            QrCodeInfo(name = if (!name.isNullOrEmpty()) name else "", telPhone = telephone)
        val content = Gson().toJson(qrCodeInfo)
        val type = HmsScan.QRCODE_SCAN_TYPE
        val width = QMUIDisplayHelper.dp2px(context, 400)
        val height = QMUIDisplayHelper.dp2px(context, 400)
        val drawable: Drawable? = ContextCompat.getDrawable(this, R.mipmap.icon_app)
        val options =
            HmsBuildBitmapOption.Creator()
                .setQRLogoBitmap(CommonUtil.drawableToBitmap(drawable))
                .setBitmapColor(ContextCompat.getColor(this, R.color.blue_base))
                .setBitmapMargin(QMUIDisplayHelper.dp2px(context, 3)).create()
        try {
            val qrBitmap: Bitmap = ScanUtil.buildBitmap(content, type, width, height, options)
            binding.ivQrCode.setImageBitmap(qrBitmap)
        } catch (e: WriterException) {
            Log.w("buildBitmap", e)
        }
    }

    override fun isDataBinding() = true
}