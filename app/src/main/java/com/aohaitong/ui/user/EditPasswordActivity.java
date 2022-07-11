package com.aohaitong.ui.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aohaitong.MyApplication;
import com.aohaitong.R;
import com.aohaitong.base.BaseActivity;
import com.aohaitong.business.BaseController;
import com.aohaitong.business.transmit.BusinessController;
import com.aohaitong.business.transmit.ISendListener;
import com.aohaitong.constant.CommonConstant;
import com.aohaitong.constant.NumConstant;
import com.aohaitong.utils.NoDoubleClickListener;
import com.aohaitong.utils.SPUtil;
import com.aohaitong.utils.dialog.MyQmuiDialog;

//修改密码页面
public class EditPasswordActivity extends BaseActivity {
    private EditText oldEdit, newEdit, checkEdit;
    private Button button;

    public static void startEditPasswordActivity(Context context) {
        Intent intent = new Intent(context, EditPasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_edit_password;
    }

    @Override
    protected void initView() {
        ((TextView) bindView(R.id.tv_title)).setText("修改密码");
        oldEdit = bindView(R.id.edit_old_psd);
        newEdit = bindView(R.id.edit_new_psd);
        checkEdit = bindView(R.id.edit_psd_check);
        button = bindView(R.id.btn_sure);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        button.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (checkInput()) {
                    showLoading();
                    BusinessController.sendResetPassword(newEdit.getText().toString(), new ISendListener() {
                        @Override
                        public void sendSuccess() {
                            loadingDialog.dismiss();
                            MyQmuiDialog.showSuccessDialog((Activity) context, "修改成功", new MyQmuiDialog.onDismiss() {
                                @Override
                                public void onDismiss() {
                                    new Thread(BaseController::logOut).start();
                                    UserLoginActivity.startUserLoginActivity(context, UserLoginActivity.loginStr);
                                    EditPasswordActivity.this.finish();
                                }
                            });
                            MyApplication.PASSWORD = newEdit.getText().toString();
                            SPUtil.instance.putValues(new SPUtil.ContentValue(CommonConstant.LOGIN_PASSWORD, ""));
                        }

                        @Override
                        public void sendFail(String reason) {
                            loadingDialog.dismiss();
                            MyQmuiDialog.showErrorDialog((Activity) context, reason);
                        }
                    }, NumConstant.getJHDNum());
                } else {
                    MyQmuiDialog.showErrorDialog((Activity) context, "请输入正确密码并保证两次输入密码相同");
                }

            }
        });

        newEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains(" ")) {
                    newEdit.setText(s.toString().trim());
                    newEdit.setSelection(s.toString().length() - 1);
                }
            }
        });

        checkEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains(" ")) {
                    checkEdit.setText(s.toString().trim());
                    checkEdit.setSelection(s.toString().length() - 1);
                }
            }
        });
    }


    private boolean checkInput() {
        boolean isOk = true;
        if (!oldEdit.getText().toString().equals(MyApplication.PASSWORD))
            isOk = false;
        if (TextUtils.isEmpty(newEdit.getText().toString().trim()) || newEdit.getText().toString().length() < 8)
            isOk = false;
        if (!newEdit.getText().toString().trim().equals(checkEdit.getText().toString().trim()))
            isOk = false;

        return isOk;
    }

}
