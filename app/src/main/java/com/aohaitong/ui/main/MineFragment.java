package com.aohaitong.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aohaitong.MyApplication;
import com.aohaitong.R;
import com.aohaitong.base.BaseFragment;
import com.aohaitong.bean.MsgEntity;
import com.aohaitong.business.BaseController;
import com.aohaitong.business.transmit.BusinessController;
import com.aohaitong.business.transmit.ISendListener;
import com.aohaitong.constant.CommonConstant;
import com.aohaitong.constant.NumConstant;
import com.aohaitong.ui.setting.SystemSettingActivity;
import com.aohaitong.ui.user.QRCardActivity;
import com.aohaitong.ui.user.UserLoginActivity;
import com.aohaitong.utils.CommonUtil;
import com.aohaitong.utils.NoDoubleClickListener;
import com.aohaitong.utils.SPUtil;
import com.aohaitong.utils.dialog.MyQmuiDialog;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

public class MineFragment extends BaseFragment {
    private LinearLayout nickNameLy, systemLy, qrCodeLy;
    private TextView logoutTv;
    private TextView nickNameTv, telTv;
    private ImageView ivIcon;

    @Override
    protected int setLayout() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView() {
        nickNameLy = bindView(R.id.line_nickname_item);
        systemLy = bindView(R.id.line_system_item);
        qrCodeLy = bindView(R.id.line_qr_code);
        logoutTv = bindView(R.id.tv_log_out);
        nickNameTv = bindView(R.id.tv_nickname);
        telTv = bindView(R.id.tv_tel);
        ivIcon = bindView(R.id.iv_icon);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        String name = SPUtil.instance.getString(MyApplication.TEL + "");
        nickNameTv.setText(TextUtils.isEmpty(name) ? MyApplication.TEL + "" : name);
        telTv.setText(MyApplication.TEL + "");
    }

    @Override
    protected void initEvent() {
        nickNameLy.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                showEditTextDialog();
            }
        });
        systemLy.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                SystemSettingActivity.startSystemSettingActivity(context);
            }
        });
        logoutTv.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                showLoading();
                BusinessController.sendLogOut(new ISendListener() {
                    @Override
                    public void sendSuccess() {
                        new Thread(BaseController::logOut).start();
                        loadingDialog.dismiss();
                        SPUtil.instance.putValues(new SPUtil.ContentValue(CommonConstant.LOGIN_PASSWORD, ""));
                        UserLoginActivity.startUserLoginActivity(getContext(), UserLoginActivity.loginStr);
                        requireActivity().finish();
                    }

                    @Override
                    public void sendFail(String reason) {
                        loadingDialog.dismiss();
                        MyQmuiDialog.showErrorDialog((Activity) context, reason);
                    }
                }, NumConstant.getJHDNum());
            }
        });
        qrCodeLy.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                requireActivity().startActivity(new Intent(requireActivity(), QRCardActivity.class));
            }
        });
    }


    private void showEditTextDialog() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
        builder.setTitle("修改昵称")
                .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
                .setPlaceholder("在此输入您的新昵称")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", (dialog, index) -> {
                    CharSequence text = builder.getEditText().getText();
                    if (text != null && text.length() > 0) {
                        if (CommonUtil.containsEmoji(text.toString())) {
                            MyQmuiDialog.showErrorDialog(getActivity(), "昵称中不可包含特殊字符");
                        } else {
                            showLoading();
                            BusinessController.sendRename(text.toString(), new ISendListener() {
                                @Override
                                public void sendSuccess() {
                                    loadingDialog.dismiss();
                                    dialog.dismiss();
                                    SPUtil.instance.putValues(new SPUtil.ContentValue(MyApplication.TEL + "", text.toString()));
                                    MyQmuiDialog.showSuccessDialog(getActivity(), "修改成功");
                                    getActivity().runOnUiThread(() -> nickNameTv.setText(text.toString()));
                                }

                                @Override
                                public void sendFail(String reason) {
                                    loadingDialog.dismiss();
                                    MyQmuiDialog.showErrorDialog((Activity) context, reason);
                                }
                            }, NumConstant.getJHDNum());
                        }
                    } else {
                        MyQmuiDialog.showErrorDialog(getActivity(), "请输入新昵称");
                    }
                })
                .create().show();
    }

    @Override
    public void onReceiveData(MsgEntity msgEntity) {

    }

}
