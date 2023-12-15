package com.aohaitong.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.aohaitong.MyApplication;
import com.aohaitong.R;
import com.aohaitong.bean.BroadBean;
import com.aohaitong.bean.MsgEntity;
import com.aohaitong.constant.CommonConstant;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.db.DBManager;
import com.aohaitong.ui.user.NewLoadActivity;
import com.aohaitong.utils.NoDoubleClickListener;
import com.aohaitong.utils.StatusBarUtils;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public abstract class BaseActivity extends FragmentActivity {
    protected Context context;
    protected String TAG;
    protected QMUITipDialog loadingDialog;
    private List<BroadBean> broadBeans = new ArrayList<>();
    private ViewFlipper vifper;
    private View view;
    private ImageView img;

    protected void showLoading(String msg) {
        if (loadingDialog.isShowing())
            return;
        loadingDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(msg)
                .create();
        loadingDialog.show();
    }

    protected void showLoading() {
        if (!loadingDialog.isShowing())
            loadingDialog.show();
    }

    public void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //严谨点的话 应该需要一起判断权限是否改变
        if (savedInstanceState != null) {
            Intent intent = new Intent(this, NewLoadActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        context = BaseActivity.this;
        TAG = getClass().getSimpleName();
        if (!isDataBinding()) {
            setContentView(getLayout());
        }
        EventBus.getDefault().register(this);
        StatusBarUtils.setTextDark(this, true);
        try {
            bindView(R.id.img_back).setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    BaseActivity.this.finish();
                }
            });
        } catch (Exception ignore) {
        }
        loadingDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在请求")
                .create();
        if (!getClass().getSimpleName().contains("NewLoadActivity")) {
            StatusBarUtils.setColor(this, getColor(R.color.blue_base));
        }
        initView();
        initData();
        initEvent();
        Log.d(CommonConstant.LOGCAT_TAG, "onCreate: " + getClass().getSimpleName());
        if (!getClass().getSimpleName().contains("UserLogin")) {
            getBroadMsg();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvents(MsgEntity entity) {
        if (entity.getType() == StatusConstant.TYPE_BROADCAST) {
            runOnUiThread(this::getBroadMsg);
        }
    }

    protected abstract int getLayout();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initEvent();

    protected <T extends View> T bindView(int resId) {
        return findViewById(resId);
    }

    protected void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.hide();
        }
        loadingDialog = null;
//        releaseWakeLock();
    }


    private void getBroadMsg() {
        try {
            vifper = bindView(R.id.tv_board);
            view = bindView(R.id.view_broad);
            img = bindView(R.id.img_broad);
            if (vifper != null) {
                broadBeans = DBManager.getInstance(MyApplication.getContext()).selectBroadList();
                if (broadBeans != null && broadBeans.size() > 0) {
                    addView();
                    vifper.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);
                    img.setVisibility(View.VISIBLE);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scroll();
                        }
                    }, 2000);
                } else {
                    vifper.setVisibility(View.GONE);
                    img.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                }
            }
        } catch (Exception ignore) {

        }
    }

    private void addView() {
        for (int i = 0; i < broadBeans.size(); i++) {
            final String info = broadBeans.get(i).getContent();
            View view = View.inflate(this, R.layout.item_view, null);
            TextView tv = view.findViewById(R.id.text);
            tv.setText(info);
            int finalI = i;
            tv.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    new QMUIDialog.MessageDialogBuilder(context)
                            .setTitle(broadBeans.get(finalI).getBusinessType() == 0 ? "天气预报" : broadBeans.get(finalI).getBusinessType() == 1 ? "航行预警" : "广告消息")
                            .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
                            .setMessage(broadBeans.get(finalI).getContent())
                            .addAction("取消", (dialog1, index) -> dialog1.dismiss())
                            .create().show();
                }
            });
            vifper.addView(view);
        }
    }

    private void scroll() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> moveTonext());
            }
        }, 0, 3000);
    }

    /**
     * 设置出现和消失时的动画效果
     */
    private void moveTonext() {
        vifper.setInAnimation(this, R.anim.in_bottomtop);
        vifper.setOutAnimation(this, R.anim.out_bottomtop);
        vifper.showNext();//切换到下一个文字
    }

    protected Boolean isDataBinding() {
        return false;
    }

    private void showAllBroadCast() {
        final String[] items = new String[broadBeans.size()];
        for (int i = 0; i < broadBeans.size(); i++) {
            items[i] = broadBeans.get(i).getContent();
        }
        new QMUIDialog.MenuDialogBuilder(context)
                .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
                .addItems(items, (dialog, which) -> {
                    new QMUIDialog.MessageDialogBuilder(context)
                            .setTitle(broadBeans.get(which).getBusinessType() == 0 ? "天气预报" : broadBeans.get(which).getBusinessType() == 1 ? "航行预警" : "广告消息")
                            .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
                            .setMessage(broadBeans.get(which).getContent())
                            .addAction("取消", (dialog1, index) -> dialog1.dismiss())
                            .create().show();
                    dialog.dismiss();
                })
                .create().show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && loadingDialog != null && loadingDialog.isShowing()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
