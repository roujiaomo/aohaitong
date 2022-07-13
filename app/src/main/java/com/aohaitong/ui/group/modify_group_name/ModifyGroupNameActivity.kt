package com.aohaitong.ui.group.modify_group_name

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import com.aohaitong.MyApplication
import com.aohaitong.R
import com.aohaitong.base.BaseActivity
import com.aohaitong.business.transmit.BusinessController
import com.aohaitong.business.transmit.ISendListener
import com.aohaitong.constant.NumConstant
import com.aohaitong.databinding.ActivityModifyGroupNameBinding
import com.aohaitong.db.DBManager
import com.aohaitong.kt.common.autoCleared
import com.aohaitong.kt.common.onClickWithAvoidRapidAction
import com.aohaitong.ui.group.detail.GroupDetailActivity
import com.aohaitong.utils.CommonUtil

class ModifyGroupNameActivity : BaseActivity() {

    private var binding: ActivityModifyGroupNameBinding by autoCleared()
    private var groupName: String = ""
    private var groupId: String = ""

    companion object {
        const val GROUP_ID = "group_id"
        const val GROUP_NAME = "group_name"
        const val REQUEST_CODE_MODIFY_GROUP_NAME = 992

        fun startModifyGroupNameActivity(
            context: Context,
            groupId: String,
            groupName: String,
        ) {
            val intent = Intent(context, ModifyGroupNameActivity::class.java)
            intent.putExtra(GROUP_ID, groupId)
            intent.putExtra(GROUP_NAME, groupName)
            (context as Activity).startActivityForResult(intent, REQUEST_CODE_MODIFY_GROUP_NAME)
        }
    }


    override fun getLayout() = R.layout.activity_modify_group_name

    override fun initView() {
        binding = DataBindingUtil.setContentView(this, layout)
        binding.etModifyGroupName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                var editStr = editable.toString().trim()
                binding.ivClearInput.visibility =
                    if (editStr.isNotEmpty()) View.VISIBLE else View.GONE
            }
        })
    }

    override fun initData() {
        groupId = intent.getStringExtra(GROUP_ID)
        groupName = intent.getStringExtra(GROUP_NAME)
        binding.etModifyGroupName.setText(groupName)
    }

    override fun initEvent() {
        binding.ivClearInput.onClickWithAvoidRapidAction {
            binding.etModifyGroupName.setText("")
        }
        binding.btnConfirm.onClickWithAvoidRapidAction {
            if (binding.etModifyGroupName.text.toString().trim().isEmpty()) {
                toast(getString(R.string.group_toast_group_name_empty))
                return@onClickWithAvoidRapidAction
            }
            if (CommonUtil.containsEmoji(binding.etModifyGroupName.text.toString())) {
                toast(getString(R.string.group_toast_group_name_not_match))
                return@onClickWithAvoidRapidAction
            }
            groupName = binding.etModifyGroupName.text.toString().trim()
            showLoading()
            BusinessController.sendModifyGroupName(
                groupId.toLong(), groupName, NumConstant.getJHDNum(),
                object : ISendListener {
                    override fun sendSuccess() {
                        hideLoading()
                        //更新本地群聊表信息
                        DBManager.getInstance(MyApplication.getContext())
                            .updateGroupName(groupId, groupName)
                        val resultIntent = Intent()
                        resultIntent.putExtra(GROUP_NAME, groupName)
                        setResult(GroupDetailActivity.RESUlT_CODE_MODIFY_GROUP_NAME, resultIntent)
                        finish()
                    }

                    override fun sendFail(reason: String?) {
                        runOnUiThread {
                            hideLoading()
                            toast(reason)
                        }

                    }
                }
            )
        }
        binding.imgBack.onClickWithAvoidRapidAction {
            finish()
        }
    }

    override fun isDataBinding() = true
}