package com.aohaitong.ui.friend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
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
import com.aohaitong.utils.CommonUtil;
import com.aohaitong.utils.DateUtil;
import com.aohaitong.utils.NoDoubleClickListener;
import com.aohaitong.utils.dialog.MyQmuiDialog;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import org.greenrobot.eventbus.EventBus;

public class FriendDetailActivity extends BaseActivity {

    private String tel;

    private TextView nameTv, telTv, backupTv, tvAddFriend;
    private LinearLayout backupLy;
    private boolean isFriend = false;
    private String name;

    public static void startFriendDetailActivity(Context context, String name, String tel, String backUp, boolean isFriend) {
        Intent intent = new Intent(context, FriendDetailActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("tel", tel);
        intent.putExtra("backup", backUp);
        intent.putExtra("isFriend", isFriend);
        context.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_friend_detail;
    }

    @Override
    protected void initView() {
        nameTv = bindView(R.id.tv_head);
        telTv = bindView(R.id.tv_tel);
        tvAddFriend = bindView(R.id.tv_add_friend);
        backupTv = bindView(R.id.tv_nickname);
        backupLy = bindView(R.id.line_nickname_item);
    }

    @Override
    protected void initData() {
        tel = getIntent().getStringExtra("tel");
        name = getIntent().getStringExtra("name");
        nameTv.setText(name);
        backupTv.setText(getIntent().getStringExtra("backup"));
        telTv.setText(tel);
        isFriend = getIntent().getBooleanExtra("isFriend", false);
        tvAddFriend.setVisibility(isFriend ? View.GONE : View.VISIBLE);
        backupLy.setVisibility(isFriend ? View.VISIBLE : View.GONE);
        nameTv.setVisibility(TextUtils.isEmpty(name) ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void initEvent() {
        backupLy.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (!isFriend) {
                    MyQmuiDialog.showErrorDialog(FriendDetailActivity.this, "无法修改非好友的备注");
                    return;
                }
                final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(context);
                builder.setTitle("修改备注")
                        .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
                        .setPlaceholder("在此输入新的备注")
                        .setInputType(InputType.TYPE_CLASS_TEXT)
                        .addAction("取消", (dialog, index) -> dialog.dismiss())
                        .addAction("确定", (dialog, index) -> {
                            CharSequence text = builder.getEditText().getText();
                            if (CommonUtil.containsEmoji(text.toString())) {
                                MyQmuiDialog.showErrorDialog(FriendDetailActivity.this, "备注中不可包含特殊字符");
                            } else {
                                showLoading();
                                BusinessController.sendEditFriendNickName(tel, text.toString(), new ISendListener() {
                                    @Override
                                    public void sendSuccess() {
                                        MyQmuiDialog.showSuccessDialog((Activity) context, "修改成功");
                                        dialog.dismiss();
                                        loadingDialog.dismiss();
                                        DBManager.getInstance(context).updateFriendNickName(tel, text.toString());
                                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_FRIEND_REFRESH));
                                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_FRIEND_APPLY_REFRESH));
                                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_CHAT_REFRESH));
                                        runOnUiThread(() -> backupTv.setText(text));
                                    }

                                    @Override
                                    public void sendFail(String reason) {
                                        MyQmuiDialog.showErrorDialog((Activity) context, reason);
                                        loadingDialog.dismiss();
                                    }
                                }, NumConstant.getJHDNum());
                            }
                        })
                        .create().show();
            }
        });

        tvAddFriend.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                showLoading();
                BusinessController.sendFriendApply(tel, "", new ISendListener() {
                    @Override
                    public void sendSuccess() {
                        loadingDialog.dismiss();
                        DBManager.getInstance(context).insertFriendApply
                                (new FriendApplyBean(name.isEmpty() ? name : tel, "", "",
                                        tel,
                                        DateUtil.getInstance().getTime() + "",
                                        StatusConstant.TYPE_UN_PASS, MyApplication.TEL + "", StatusConstant.SEND_TYPE_SENDER, StatusConstant.READ_UNREAD));
                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_FRIEND_APPLY_REFRESH));
                        MyQmuiDialog.showSuccessDialog((Activity) context, "申请成功", FriendDetailActivity.this::finish);
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
}
