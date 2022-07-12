package com.aohaitong.ui.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aohaitong.MyApplication;
import com.aohaitong.R;
import com.aohaitong.base.BaseActivity;
import com.aohaitong.bean.GroupBean;
import com.aohaitong.business.BaseController;
import com.aohaitong.business.IPController;
import com.aohaitong.business.transmit.BusinessController;
import com.aohaitong.business.transmit.ISendListener;
import com.aohaitong.business.transmit.SendController;
import com.aohaitong.business.transmit.SocketController;
import com.aohaitong.constant.CommonConstant;
import com.aohaitong.constant.IPAddress;
import com.aohaitong.constant.NumConstant;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.db.DBManager;
import com.aohaitong.kt.util.VersionUtil;
import com.aohaitong.nettools.NetTools;
import com.aohaitong.nettools.inter.MyCallBack;
import com.aohaitong.ui.main.MainActivity;
import com.aohaitong.utils.NoDoubleClickListener;
import com.aohaitong.utils.SPUtil;
import com.aohaitong.utils.TelUtil;
import com.aohaitong.utils.dialog.MyQmuiDialog;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.AttachPopupView;
import com.lxj.xpopup.util.XPopupUtils;

import java.util.List;

import cn.feng.skin.manager.loader.SkinManager;

//用户登录注册页面
public class UserLoginActivity extends BaseActivity {
    private Button loginBtn;
    private TextView loginTv, registerTv;
    private LinearLayout telLine, psdLoginLine, psdRegisterLine, psdRegisterConfirmLine, idCardLine;
    private EditText telEdit, psdLoginEdit, psdRegisterEdit, psdRegisterConfirmEdit, idCardEdit;
    private TextView titleTv;
    private TextView forgetPsdTv;
    private TextView tvSelectNetwork;
    private ImageView ivSeeLoginPsw;
    private String type;
    public static final String loginStr = "login";
    public static final String registerStr = "register";

    boolean isLoginPwdVisible = false;

    /**
     * 跳转至当前页面
     *
     * @param type login 或者 register
     */
    public static void startUserLoginActivity(Context context, String type) {
        Intent intent = new Intent(context, UserLoginActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_user_login;
    }

    @Override
    protected void initView() {
        type = getIntent().getStringExtra("type");
        titleTv = bindView(R.id.tv_title);

        loginBtn = bindView(R.id.btn_register);
        loginTv = bindView(R.id.tv_login_tip);
        registerTv = bindView(R.id.tv_register_tip);
        telLine = bindView(R.id.line_tel);
        idCardLine = bindView(R.id.line_id_card);
        psdLoginLine = bindView(R.id.line_password_lock);
        psdRegisterLine = bindView(R.id.line_password_unlock);
        psdRegisterConfirmLine = bindView(R.id.line_confirm_password_unlock);
        telEdit = bindView(R.id.edit_tel);
        idCardEdit = bindView(R.id.edit_id_card);
        psdLoginEdit = bindView(R.id.edit_psd_lock);
        psdRegisterEdit = bindView(R.id.edit_psd_unlock);
        psdRegisterConfirmEdit = bindView(R.id.edit_confirm_psd_unlock);
        forgetPsdTv = bindView(R.id.tv_forget_psd);
        tvSelectNetwork = bindView(R.id.tv_select_network);
        ivSeeLoginPsw = bindView(R.id.iv_login_see);
        changeView();
        if (VersionUtil.INSTANCE.isTestVersion()) {
            tvSelectNetwork.setVisibility(View.VISIBLE);
        } else {
            tvSelectNetwork.setVisibility(View.GONE);
        }
        if (SPUtil.instance.getInt(CommonConstant.SP_LOGIN_NETWORK_TYPE) == StatusConstant.CONNECT_MQ) {
            tvSelectNetwork.setText("已选择:宽频");
        } else if (SPUtil.instance.getInt(CommonConstant.SP_LOGIN_NETWORK_TYPE) == StatusConstant.CONNECT_SOCKET) {
            tvSelectNetwork.setText("已选择:窄频");
        } else {
            tvSelectNetwork.setText("选择网络");
        }
    }

    private void changeView() {
        if (loginStr.equals(type)) {
            initLoginView();
        } else {
            initRegisterView();
        }
    }

    @Override
    protected void initData() {
        telEdit.setText(SPUtil.instance.getString(CommonConstant.LOGIN_TEL));
    }

    @Override
    protected void initEvent() {
        loginTv.setOnClickListener(v -> {
            type = loginStr;
            changeView();
        });
        registerTv.setOnClickListener(v -> {
            type = registerStr;
            changeView();
        });
        loginBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (VersionUtil.INSTANCE.isTestVersion()) {
                    if (SPUtil.instance.getInt(CommonConstant.SP_LOGIN_NETWORK_TYPE) == -1) {
                        toast("请选择网络!");
                        return;
                    }
                }
                doLogin();
            }
        });
        forgetPsdTv.setOnClickListener(v -> ForgetPasswordActivity.startForgetPasswordActivity(UserLoginActivity.this));

        tvSelectNetwork.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                AttachPopupView attachPopupView = new XPopup.Builder(getContext())
                        .hasShadowBg(false)
                        .isClickThrough(true)
                        .popupWidth(XPopupUtils.dp2px(context, 80))
                        .popupHeight(XPopupUtils.dp2px(context, 100))
                        .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                        .asAttachList(new String[]{"宽频", "窄频"
                        }, null, (position, text) -> {
                            if (position == 0) {
                                SPUtil.instance.putValues(new SPUtil.ContentValue(CommonConstant.SP_LOGIN_NETWORK_TYPE,
                                        StatusConstant.CONNECT_MQ));
                                tvSelectNetwork.setText("已选择:宽频");
                            } else {
                                SPUtil.instance.putValues(new SPUtil.ContentValue(CommonConstant.SP_LOGIN_NETWORK_TYPE,
                                        StatusConstant.CONNECT_SOCKET));
                                tvSelectNetwork.setText("已选择:窄频");
                            }
                        });
                attachPopupView.show();
            }
        });

        psdRegisterConfirmEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains(" ")) {
                    psdRegisterConfirmEdit.setText(s.toString().trim());
                    psdRegisterConfirmEdit.setSelection(s.toString().length() - 1);
                }
            }
        });


        psdRegisterEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains(" ")) {
                    psdRegisterEdit.setText(s.toString().trim());
                    psdRegisterEdit.setSelection(s.toString().length() - 1);
                }
            }
        });

        idCardEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains(" ")) {
                    idCardEdit.setText(s.toString().trim());
                    idCardEdit.setSelection(s.toString().length() - 1);
                }
            }
        });


        psdLoginEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains(" ")) {
                    psdLoginEdit.setText(s.toString().trim());
                    psdLoginEdit.setSelection(s.toString().length() - 1);
                }
            }
        });
        ivSeeLoginPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (ivVisible.isSelected) {
//                    etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
//                } else {
//                    etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
//                }
//                if(!isLoginPwdVisible){
//
//                }
                isLoginPwdVisible = !isLoginPwdVisible;

            }
        });
    }

    private void doLogin() {
        if (checkTelOk()) {
            if (loginStr.equals(type)) {
                showLoading("登录中");
                MyApplication.TEL = Long.parseLong(telEdit.getText().toString());
                new Thread(() -> {
                    if (VersionUtil.INSTANCE.isTestVersion()) {
                        IPController.loadTestIp();
                    } else {
                        IPController.loadIp();
                    }
                    if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_MQ) {
                        BaseController.startConnect();
                        doLoginMQ();
                    } else if (IPController.CONNECT_TYPE == StatusConstant.CONNECT_SOCKET) {
                        BaseController.startConnect();
                        doLoginSocket();
                    } else {
                        loadingDialog.dismiss();
                        MyQmuiDialog.showErrorDialog(UserLoginActivity.this, "当前网络不佳，请检查您的网络");
                    }
                }).start();
            } else {
                doRegister();
            }
        } else {
            MyQmuiDialog.showErrorDialog((Activity) context, "请输入正确手机号以及密码");
        }
    }


    /**
     * 检验手机号是否有问题,以及密码是否有问题
     */
    private boolean checkTelOk() {
        boolean isOk = true;
        if (loginStr.equals(type)) {
            String tel = telEdit.getText().toString();
            String psd = psdLoginEdit.getText().toString();
            if (TextUtils.isEmpty(tel.trim()) || TextUtils.isEmpty(psd.trim())) {
                isOk = false;
            }
            if (!TelUtil.isChinaPhoneLegal(tel)) {
                isOk = false;
            }
        }
//        else {
//            String tel = telEdit.getText().toString();
//            String psd = psdRegisterEdit.getText().toString();
//            if (!TelUtil.isChinaPhoneLegal(tel)) {
//                isOk = false;
//            }
//            if (TextUtils.isEmpty(tel.trim()) || TextUtils.isEmpty(psd.trim()) || psd.length() < 8) {
//                isOk = false;
//            }
//        }
        return isOk;
    }

    private void doRegister() {
        String tel = telEdit.getText().toString().trim();
        String psd = psdRegisterEdit.getText().toString().trim();
        String idCard = idCardEdit.getText().toString().trim();
        String confirmPsd = psdRegisterConfirmEdit.getText().toString().trim();

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
        if (TextUtils.isEmpty(confirmPsd) || confirmPsd.length() < 8 || !confirmPsd.equals(psd)) {
            MyQmuiDialog.showErrorDialog((Activity) context, "两次密码不一致，请重试");
            return;
        }

        showLoading("注册中");
        NetTools.getInstance().startRequest(IPAddress.registerUrl
                + "?phoneNumber=" + tel +
                "&password=" + psd +
                "&identityCode=" + idCard, String.class, new MyCallBack<String>() {
            @Override
            public void success(String respomse) {
                loadingDialog.dismiss();
                if (respomse.equals(StatusConstant.SUCCESS)) {
                    MyQmuiDialog.showSuccessDialog((Activity) context, "注册成功", () -> {
                        psdLoginEdit.setText(psdRegisterEdit.getText().toString());
                        type = loginStr;
                        doLogin();
                    });
                } else {
                    MyQmuiDialog.showErrorDialog((Activity) context, "当前用户已注册");
                }
            }

            @Override
            public void error(Throwable throwable) {
                loadingDialog.dismiss();
                MyQmuiDialog.showErrorDialog((Activity) context, "网络异常");
            }
        });
    }

    private void doLoginSocket() {
        checkIsSocketConnect();
    }

    private void checkIsSocketConnect() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Log.d(CommonConstant.LOGCAT_TAG, "socket连接状态" + SocketController.getInstance().getIsSocketStart());
            if (SocketController.getInstance().getIsSocketStart()) {
                BusinessController.sendLogin(telEdit.getText().toString(), psdLoginEdit.getText().toString(), new ISendListener() {
                    @Override
                    public void sendSuccess() {
                        Log.e(CommonConstant.LOGCAT_TAG, "Socket登录成功");
                        loadingDialog.dismiss();
                        MyApplication.TEL = Long.parseLong(telEdit.getText().toString());
                        MyApplication.PASSWORD = psdLoginEdit.getText().toString();
                        List<GroupBean> groupList = DBManager.getInstance(MyApplication.getContext())
                                .getGroupListByTel(MyApplication.TEL + "");
                        for (GroupBean groupBean : groupList) {
                            SendController.groupIdList.add(groupBean.getGroupId());
                        }
                        MyApplication.isHaveLogin = true;
                        SPUtil.instance.putValues(new SPUtil.ContentValue(CommonConstant.LOGIN_TEL, telEdit.getText().toString()));
                        SPUtil.instance.putValues(new SPUtil.ContentValue(CommonConstant.LOGIN_PASSWORD, MyApplication.PASSWORD));
                        BaseController.startHeartBeat();
                        MainActivity.startMainActivity(UserLoginActivity.this);
                        UserLoginActivity.this.finish();
                    }

                    @Override
                    public void sendFail(String reason) {
                        Log.e(CommonConstant.LOGCAT_TAG, "Socket登录失败：" + reason);
                        loadingDialog.dismiss();
                        MyApplication.isHaveLogin = false;
                        MyQmuiDialog.showErrorDialog((Activity) context, reason);
                        BaseController.stopHeartBeat();
                        SocketController.getInstance().stopConnect();
                    }
                }, NumConstant.getJHDNum());
            } else {
                socketFailTime++;
                if (socketFailTime < NumConstant.SOCKET_CONNECT_CHECK_COUNT) {
                    checkIsSocketConnect();
                } else {
                    loadingDialog.dismiss();
                    MyApplication.isHaveLogin = false;
                    BaseController.stopHeartBeat();
                    SocketController.getInstance().stopConnect();
                    MyQmuiDialog.showErrorDialog((Activity) context, "网络异常");
                    socketFailTime = 0;
                }
            }
        }, 1000);
    }

    private int socketFailTime = 0;

    private void doLoginMQ() {
        BusinessController.sendLogin(telEdit.getText().toString(), psdLoginEdit.getText().toString(), new ISendListener() {
            @Override
            public void sendSuccess() {
                Log.e(CommonConstant.LOGCAT_TAG, "MQ登录成功");
                loadingDialog.dismiss();
                MyApplication.isHaveLogin = true;
                MyApplication.TEL = Long.parseLong(telEdit.getText().toString());
                MyApplication.PASSWORD = psdLoginEdit.getText().toString();
                SPUtil.instance.putValues(new SPUtil.ContentValue(CommonConstant.LOGIN_TEL, MyApplication.TEL + ""));
                SPUtil.instance.putValues(new SPUtil.ContentValue(CommonConstant.LOGIN_PASSWORD, MyApplication.PASSWORD));
                BaseController.startHeartBeat();
                BusinessController.sendGetFriendList(SPUtil.instance.getLong(MyApplication.TEL + CommonConstant.LAST_UPDATE_TIME) + "", null, NumConstant.getJHDNum());
                BusinessController.sendGetGroupList(null, NumConstant.getJHDNum());
                MainActivity.startMainActivity(UserLoginActivity.this);
                UserLoginActivity.this.finish();
            }

            @Override
            public void sendFail(String reason) {
                Log.e(CommonConstant.LOGCAT_TAG, "MQ登录失败：" + reason);
                loadingDialog.dismiss();
                MyApplication.isHaveLogin = true;
                MyQmuiDialog.showErrorDialog((Activity) context, reason);
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        BaseController.logOut();
                    }
                }.start();
            }
        }, NumConstant.getJHDNum());
    }


    /**
     * 初始化登录布局
     */
    private void initLoginView() {
        titleTv.setText(getResources().getString(R.string.login));
        loginTv.setTextColor(SkinManager.getInstance().getColor(R.color.blue_base));
        loginTv.setTextSize(16);
        loginTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        registerTv.setTextColor(SkinManager.getInstance().getColor(R.color.black));
        registerTv.setTextSize(14);
        registerTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        loginBtn.setText(getResources().getString(R.string.login_now));
        psdRegisterLine.setVisibility(View.GONE);
        idCardLine.setVisibility(View.GONE);
        psdRegisterConfirmLine.setVisibility(View.GONE);
        forgetPsdTv.setVisibility(View.VISIBLE);
        psdLoginLine.setVisibility(View.VISIBLE);

    }

    /**
     * 初始化注册布局
     */
    private void initRegisterView() {
        titleTv.setText(getResources().getString(R.string.register));
        registerTv.setTextColor(SkinManager.getInstance().getColor(R.color.blue_base));
        registerTv.setTextSize(16);
        registerTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        loginTv.setTextColor(SkinManager.getInstance().getColor(R.color.black));
        loginTv.setTextSize(14);
        loginTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        loginBtn.setText(getResources().getString(R.string.register_now));
        psdRegisterLine.setVisibility(View.VISIBLE);
        idCardLine.setVisibility(View.VISIBLE);
        psdRegisterConfirmLine.setVisibility(View.VISIBLE);
        psdLoginLine.setVisibility(View.GONE);
        forgetPsdTv.setVisibility(View.GONE);
    }
}
