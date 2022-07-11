package com.aohaitong.ui.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aohaitong.R;
import com.aohaitong.base.BaseActivity;
import com.aohaitong.constant.IPAddress;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.nettools.NetTools;
import com.aohaitong.nettools.inter.MyCallBack;
import com.aohaitong.utils.NoDoubleClickListener;
import com.aohaitong.utils.TelUtil;
import com.aohaitong.utils.dialog.MyQmuiDialog;


//忘记密码页面
public class ForgetPasswordActivity extends BaseActivity {
    private EditText telEdit, psdEdit, psdCheckEdit, idCardEdit;
    private Button btn;

    public static void startForgetPasswordActivity(Context context) {
        Intent intent = new Intent(context, ForgetPasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_forget_password;
    }

    @Override
    protected void initView() {
        ((TextView) bindView(R.id.tv_title)).setText("忘记密码");
        telEdit = bindView(R.id.edit_tel);
        psdEdit = bindView(R.id.edit_psd);
        idCardEdit = bindView(R.id.edit_id_card);
        psdCheckEdit = bindView(R.id.edit_psd_check);
        btn = bindView(R.id.btn_sure);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        btn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                String tel = telEdit.getText().toString().trim();
                String psd = psdEdit.getText().toString().trim();
                String psdCheck = psdCheckEdit.getText().toString().trim();
                String idCard = idCardEdit.getText().toString().trim();
                if (!TelUtil.isChinaPhoneLegal(tel)) {
                    MyQmuiDialog.showErrorDialog((Activity) context, "请输入正确手机号");
                    return;
                }
                if (TextUtils.isEmpty(idCard) || !TelUtil.isIdCardNum(idCard)) {
                    MyQmuiDialog.showErrorDialog((Activity) context, "请输入正确格式的身份证号");
                    return;
                }
                if (TextUtils.isEmpty(psd) || psd.length() < 8) {
                    MyQmuiDialog.showErrorDialog((Activity) context, "请输入正确的8-16位密码");
                    return;
                }
                if (TextUtils.isEmpty(psdCheck) || psdCheck.length() < 8 || !psdCheck.equals(psd)) {
                    MyQmuiDialog.showErrorDialog((Activity) context, "两次密码不一致，请重试");
                    return;
                }
                showLoading();
                NetTools.getInstance().startRequest(IPAddress.forgetPassword
                        + "?phoneNumber=" + tel +
                        "&password=" + psd +
                        "&identityCode=" + idCard, String.class, new MyCallBack<String>() {
                    @Override
                    public void success(String response) {
                        loadingDialog.dismiss();
                        if (response.equals(StatusConstant.SUCCESS)) {
                            MyQmuiDialog.showSuccessDialog((Activity) context, "修改成功");
                            UserLoginActivity.startUserLoginActivity(ForgetPasswordActivity.this,
                                    UserLoginActivity.loginStr);
                        } else {
                            MyQmuiDialog.showErrorDialog((Activity) context, "修改失败");
                        }
                    }

                    @Override
                    public void error(Throwable throwable) {
                        loadingDialog.dismiss();
                        MyQmuiDialog.showErrorDialog((Activity) context, "网络异常");
                    }
                });
            }
        });
    }
}
