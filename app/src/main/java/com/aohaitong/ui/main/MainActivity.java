package com.aohaitong.ui.main;

import static android.app.Notification.EXTRA_CHANNEL_ID;
import static android.provider.Settings.EXTRA_APP_PACKAGE;
import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.aohaitong.MyApplication;
import com.aohaitong.R;
import com.aohaitong.base.BaseActivity;
import com.aohaitong.bean.MsgEntity;
import com.aohaitong.bean.entity.QrCodeInfo;
import com.aohaitong.broadcastReceiver.NetworkBroadcastReceiver;
import com.aohaitong.broadcastReceiver.ScreenBroadcastReceiver;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.ui.adapter.MainTabAdapter;
import com.aohaitong.ui.friend.FriendDetailActivity;
import com.aohaitong.ui.main.message.NewMessageFragment;
import com.aohaitong.ui.seachart.SeaChartActivity;
import com.aohaitong.utils.BadgeUtils;
import com.aohaitong.utils.ContactsUtil;
import com.aohaitong.utils.NoDoubleClickListener;
import com.aohaitong.utils.PermissionUtils;
import com.aohaitong.widget.NoScrollViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends BaseActivity {

    private NoScrollViewPager viewPager;
    private TabLayout tabLayout;

    private MainTabAdapter adapter;
    private List<Fragment> fragments;
    private final String[] tabTitle = new String[]{"消息", "好友",
//            "海图",
            "我的"};
    private final int[] tabImg = new int[]{R.drawable.tab_msg, R.drawable.tab_friend,
//            R.drawable.tab_seachart,
            R.drawable.tab_mine};

    private NewMessageFragment messageFragment;
    private FriendFragment friendFragment;
    private MineFragment mineFragment;
    private SeaChartFragment seaChartFragment;
    private NetworkBroadcastReceiver netWorkStateReceiver;
    private ScreenBroadcastReceiver mScreenReceiver;
    private ImageView scanButton;
    private ImageView fullScreenButton;
    private final static int REQUEST_CODE_SCAN_DEFAULT_MODE = 101;
    private final String[] permissionList = new String[]{Manifest.permission.CAMERA};
    private ConstraintLayout consHeader;

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        viewPager = bindView(R.id.vp_main);
        tabLayout = bindView(R.id.tab_main);
        bindView(R.id.img_back).setVisibility(View.GONE);
        scanButton = bindView(R.id.iv_scan);
        fullScreenButton = bindView(R.id.iv_full_screen);
        consHeader = bindView(R.id.cons_head);
        acquireWakeLock();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initData() {
        fragments = new ArrayList<>();
        messageFragment = new NewMessageFragment();
        friendFragment = new FriendFragment();
        mineFragment = new MineFragment();
//        seaChartFragment = new SeaChartFragment();
        fragments.add(messageFragment);
        fragments.add(friendFragment);
//        fragments.add(seaChartFragment);
        fragments.add(mineFragment);
        adapter = new MainTabAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.setFragments(fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(fragments.size() - 1);
        viewPager.setNoScroll(true);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View view = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
            (((ImageView) view.findViewById(R.id.img_tab))).setImageResource(tabImg[i]);
            (((TextView) view.findViewById(R.id.tv_tab))).setText(tabTitle[i]);
            view.setFocusable(true);
            tab.setCustomView(view);
            if (i == 0) {
                (((TextView) tab.getCustomView().findViewById(R.id.tv_tab))).setTextColor(getColor(R.color.blue_base));
                ((TextView) bindView(R.id.tv_title)).setText(tabTitle[0]);
                consHeader.setVisibility(View.GONE);
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.img_tab).setFocusable(true);
                ((TextView) tab.getCustomView().findViewById(R.id.tv_tab)).setTextColor(getColor(R.color.blue_base));
                TextView tvTitle = bindView(R.id.tv_title);
                tvTitle.setText(((TextView) tab.getCustomView().findViewById(R.id.tv_tab)).getText());
                switch (tab.getPosition()) {
                    case 0: {
                        consHeader.setVisibility(View.GONE);
                        scanButton.setVisibility(View.GONE);
                        fullScreenButton.setVisibility(View.GONE);
                        break;
                    }
                    case 1: {
                        consHeader.setVisibility(View.VISIBLE);
                        scanButton.setVisibility(View.VISIBLE);
                        fullScreenButton.setVisibility(View.GONE);
                        break;
                    }
                    case 2:
                        consHeader.setVisibility(View.VISIBLE);
                        scanButton.setVisibility(View.GONE);
                        fullScreenButton.setVisibility(View.GONE);
//                        consHeader.setVisibility(View.VISIBLE);
//                        scanButton.setVisibility(View.GONE);
//                        fullScreenButton.setVisibility(View.VISIBLE);
                        break;
                    case 3: {
                        consHeader.setVisibility(View.VISIBLE);
                        scanButton.setVisibility(View.GONE);
                        fullScreenButton.setVisibility(View.GONE);
                        break;
                    }
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.img_tab).setFocusable(false);
                (((TextView) tab.getCustomView().findViewById(R.id.tv_tab))).setTextColor(getColor(R.color.base_list_text_gray));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if (tabLayout.getSelectedTabPosition() == 0) {
            scanButton.setVisibility(View.GONE);
        }
        ignoreBatteryOptimization(this);
    }

    private void startScan() {
        // 扫码选项参数
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create();
        ScanUtil.startScan(
                this, REQUEST_CODE_SCAN_DEFAULT_MODE,
                options
        );
    }

    @Override
    protected void initEvent() {
//        checkNotifySetting();
        scanButton.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                //申请权限
                if (PermissionUtils.INSTANCE.checkPermissionAllGranted(MainActivity.this, permissionList)) {
                    startScan();
                } else {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            permissionList,
                            0
                    );
                }
            }
        });
        fullScreenButton.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                startActivity(new Intent(MainActivity.this, SeaChartActivity.class));
            }
        });
        registerReceivers();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MsgEntity entity) {
        if (entity.getType() != StatusConstant.TYPE_HEART) {
            messageFragment.onReceiveData(entity);
            friendFragment.onReceiveData(entity);
            mineFragment.onReceiveData(entity);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 忽略电池优化
     */
    private void ignoreBatteryOptimization(Activity activity) {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean hasIgnored = powerManager.isIgnoringBatteryOptimizations(activity.getPackageName());
        //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
        if (!hasIgnored) {
            @SuppressLint("BatteryLife")
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            startActivity(intent);
            //这里如果拒绝，后续需要加入提醒：去聊天权限设置里
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//            }).launch(intent);
        }
    }

    PowerManager.WakeLock wakeLock = null;

    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    @SuppressLint("InvalidWakeLockTag")
    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApplication");
            wakeLock.setReferenceCounted(false);
            if (null != wakeLock) {
                wakeLock.acquire(10 * 1000L /*10 minutes*/);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netWorkStateReceiver);
        unregisterReceiver(mScreenReceiver);
        if (null != wakeLock) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    private void checkNotifySetting() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        // areNotificationsEnabled方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。
        boolean isOpened = manager.areNotificationsEnabled();
        if (!isOpened) {
            new QMUIDialog.MessageDialogBuilder(MainActivity.this)
                    .setTitle("提醒")
                    .setMessage("通知权限未开启\n无法收到新消息提醒\n点击确定前往开启")
                    .setSkinManager(QMUISkinManager.defaultInstance(getContext()))
                    .setCanceledOnTouchOutside(false)
                    .addAction("取消", (dialog, index) -> dialog.dismiss())
                    .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_POSITIVE, (dialog, index) -> {
                        dialog.dismiss();
                        try {
                            // 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
                            Intent intent = new Intent();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                                intent.putExtra(EXTRA_APP_PACKAGE, getPackageName());
                                intent.putExtra(EXTRA_CHANNEL_ID, getApplicationInfo().uid);
                            } else {
                                //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
                                intent.putExtra("app_package", getPackageName());
                                intent.putExtra("app_uid", getApplicationInfo().uid);
                            }
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
                            Intent intent = new Intent();

                            //下面这种方案是直接跳转到当前应用的设置界面。
                            //https://blog.csdn.net/ysy950803/article/details/71910806
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    })
                    .create().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN_DEFAULT_MODE) {
            if (resultCode == RESULT_CANCELED) {
                return;
            }
            if (resultCode != RESULT_OK || data == null) {
                toast("扫描失败，请重试！");
                return;
            }
            HmsScan hmsScan = data.getParcelableExtra(ScanUtil.RESULT); // 获取扫码结果 ScanUtil.RESULT
            try {
                QrCodeInfo resultBean = new Gson().fromJson(hmsScan.getOriginalValue(), QrCodeInfo.class);
                FriendDetailActivity.startFriendDetailActivity(context, resultBean.getName(), resultBean.getTelPhone(),
                        ContactsUtil.getFriendNickName(this, resultBean.getTelPhone()), ContactsUtil.getIsFriend(this, resultBean.getTelPhone()));
            } catch (JsonSyntaxException e) {
                toast("无效二维码，请扫描遨海通的二维码名片！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            boolean isAllGranted = true;
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (!isAllGranted) {
                toast("请同意相机权限,否则无法使用扫描功能");
            }
        }
    }

    private void registerReceivers() {
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetworkBroadcastReceiver();
        }
        IntentFilter networkFilter = new IntentFilter();
        networkFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, networkFilter);
        if (mScreenReceiver == null) {
            mScreenReceiver = new ScreenBroadcastReceiver();
        }
        IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenFilter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenReceiver, screenFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.msgCount = 0;
        BadgeUtils.setCount(0, context, null);
    }
}
