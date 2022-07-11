package com.aohaitong.ui.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.aohaitong.MyApplication;
import com.aohaitong.R;
import com.aohaitong.base.BaseActivity;
import com.aohaitong.constant.CommonConstant;
import com.aohaitong.db.DBManager;
import com.aohaitong.ui.user.EditPasswordActivity;
import com.aohaitong.ui.user.impermission.IMPermissionActivity;
import com.aohaitong.utils.FileUtils;
import com.aohaitong.utils.NoDoubleClickListener;
import com.aohaitong.utils.SPUtil;
import com.aohaitong.utils.audio.RecordManager;
import com.aohaitong.utils.dialog.MyQmuiDialog;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

//系统设置页面
public class SystemSettingActivity extends BaseActivity {

    private Switch ringSwitch, verSwitch;

    public static void startSystemSettingActivity(Context context) {
        Intent intent = new Intent(context, SystemSettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_system_setting;
    }

    @Override
    protected void initView() {
        ((TextView) bindView(R.id.tv_title)).setText("系统设置");

        ringSwitch = bindView(R.id.switch_ring);
        verSwitch = bindView(R.id.switch_vibrator);
        ringSwitch.setChecked(SPUtil.instance.getBoolean(CommonConstant.SP_RING_OPEN));
        verSwitch.setChecked(SPUtil.instance.getBoolean(CommonConstant.SP_VIBRATOR_OPEN));
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        bindView(R.id.line_clear_chat).setOnClickListener(v ->
                new QMUIDialog.MessageDialogBuilder(context)
                        .setTitle("是否清空所有聊天记录")
                        .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
                        .addAction("取消", (dialog, index) -> dialog.dismiss())
                        .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_NEGATIVE, (dialog, index) -> {
                            dialog.dismiss();
                            //清空聊天记录表及音频文件
                            DBManager.getInstance(MyApplication.getContext()).delAllMsg();
                            FileUtils.deleteAllFile(RecordManager.getInstance().RECORD_FILE_PATH);
                            MyQmuiDialog.showSuccessDialog((Activity) context, "清空成功");
                        })
                        .create().show());

        bindView(R.id.line_change_password).setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                EditPasswordActivity.startEditPasswordActivity(context);
            }
        });
        bindView(R.id.line_change_ring).setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                RingSettingActivity.startRingSettingActivity(context);
            }
        });
        bindView(R.id.line_skin_change).setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                Intent intent = new Intent(context, SettingActivity.class);
                startActivity(intent);
            }
        });
        bindView(R.id.line_im_permission).setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                Intent intent = new Intent(context, IMPermissionActivity.class);
                startActivity(intent);
            }
        });
        ringSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SPUtil.instance.putValues(new SPUtil.ContentValue(CommonConstant.SP_RING_OPEN, isChecked));
            MyApplication.PLAY_RING = isChecked;
        });
        verSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SPUtil.instance.putValues(new SPUtil.ContentValue(CommonConstant.SP_VIBRATOR_OPEN, isChecked));
            MyApplication.PLAY_VIBRATOR = isChecked;
        });
    }
}
