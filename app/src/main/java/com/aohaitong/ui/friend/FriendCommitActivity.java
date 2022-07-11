package com.aohaitong.ui.friend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aohaitong.MyApplication;
import com.aohaitong.R;
import com.aohaitong.base.BaseActivity;
import com.aohaitong.bean.FriendApplyBean;
import com.aohaitong.bean.MsgEntity;
import com.aohaitong.business.transmit.BusinessController;
import com.aohaitong.business.transmit.ISendListener;
import com.aohaitong.constant.NumConstant;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.db.DBManager;
import com.aohaitong.utils.DateUtil;
import com.aohaitong.utils.NoDoubleClickListener;
import com.aohaitong.utils.TelUtil;
import com.aohaitong.utils.dialog.MyQmuiDialog;

import org.greenrobot.eventbus.EventBus;

public class FriendCommitActivity extends BaseActivity {
    private String name = "";
    private boolean type;
    private EditText telEdit, reasonEdit, backUpEdit;
    private Button button;

    /**
     * @param showTel 是否显示输入电话号
     */
    public static void startFriendCommitActivity(Context context, boolean showTel, String name, String tel) {
        Intent intent = new Intent(context, FriendCommitActivity.class);
        intent.putExtra("type", showTel);
        intent.putExtra("name", name);
        intent.putExtra("tel", tel);
        context.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_friend_commit;
    }

    @Override
    protected void initView() {
        ((TextView) bindView(R.id.tv_title)).setText("申请添加好友");
    }

    @Override
    protected void initData() {
        name = getIntent().getStringExtra("name");
        type = getIntent().getBooleanExtra("type", true);
        telEdit = bindView(R.id.edit_tel);
        reasonEdit = bindView(R.id.edit_text);
        backUpEdit = bindView(R.id.edit_backup);
        button = bindView(R.id.btn_sure);
        if (!type) {
            bindView(R.id.tv_tel).setVisibility(View.GONE);
            telEdit.setVisibility(View.GONE);
        }
        telEdit.setText(getIntent().getStringExtra("tel"));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initEvent() {
        ((TextView) bindView(R.id.tv_title)).setText("申请添加" + name + "好友");
        button.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                String relTel = telEdit.getText().toString().trim().replaceAll(" ", "");
                if (!TelUtil.isChinaPhoneLegal(relTel)) {
                    MyQmuiDialog.showErrorDialog((Activity) context, type ? "请输入正确手机号" : "请选择标准手机号码的联系人");
                    return;
                }
                if ((MyApplication.TEL + "").equals(relTel)) {
                    MyQmuiDialog.showErrorDialog((Activity) context, "不能添加自己为好友");
                    return;
                }
                if (checkInFriendList(relTel)) {
                    MyQmuiDialog.showErrorDialog((Activity) context, "已有该好友或已有好友申请");
                    return;
                }
                showLoading();
                BusinessController.sendFriendApply(relTel, reasonEdit.getText().toString(), new ISendListener() {
                    @Override
                    public void sendSuccess() {
                        loadingDialog.dismiss();
                        DBManager.getInstance(context).insertFriendApply
                                (new FriendApplyBean(name, backUpEdit.getText().toString(), reasonEdit.getText().toString(),
                                        relTel,
                                        DateUtil.getInstance().getTime() + "",
                                        StatusConstant.TYPE_UN_PASS, MyApplication.TEL + "", StatusConstant.SEND_TYPE_SENDER, StatusConstant.READ_UNREAD));
                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_FRIEND_APPLY_REFRESH));
                        MyQmuiDialog.showSuccessDialog((Activity) context, "申请成功", FriendCommitActivity.this::finish);
                    }

                    @Override
                    public void sendFail(String reason) {
                        loadingDialog.dismiss();
                        MyQmuiDialog.showErrorDialog((Activity) context, reason);
                    }
                }, NumConstant.getJHDNum());
            }
        });
    }

    /**
     * 检查是否已经在好友列表或者好友申请列表中
     */
    private boolean checkInFriendList(String relTel) {
        return DBManager.getInstance(context).checkInFriendList(relTel) != 0;
    }
}
