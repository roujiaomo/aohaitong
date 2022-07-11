package com.aohaitong.ui.friend;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aohaitong.MyApplication;
import com.aohaitong.R;
import com.aohaitong.base.BaseActivity;
import com.aohaitong.bean.ChatMsgBean;
import com.aohaitong.bean.FriendApplyBean;
import com.aohaitong.bean.FriendBean;
import com.aohaitong.bean.MsgEntity;
import com.aohaitong.business.transmit.BusinessController;
import com.aohaitong.business.transmit.ISendListener;
import com.aohaitong.constant.NumConstant;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.db.DBManager;
import com.aohaitong.interfaces.OnItemClickListener;
import com.aohaitong.ui.adapter.FriendApplyAdapter;
import com.aohaitong.utils.ContactsUtil;
import com.aohaitong.utils.NoDoubleClickListener;
import com.aohaitong.utils.NotificationUtil;
import com.aohaitong.utils.dialog.MyQmuiDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//新的朋友
public class FriendApplyActivity extends BaseActivity {
    private final List<FriendApplyBean> data = new ArrayList<>();
    private FriendApplyAdapter adapter;
    private RecyclerView recyclerView;

    private final String[] permissionList =
            new String[]{Manifest.permission.READ_CONTACTS};

    public static void startFriendApplyActivity(Context context) {
        context.startActivity(new Intent(context, FriendApplyActivity.class));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_friend_apply;
    }

    @Override
    protected void initView() {
        ((TextView) bindView(R.id.tv_title)).setText("新的朋友");
//        ((TextView) bindView(R.id.tv_right)).setText("添加朋友");
//        bindView(R.id.tv_right).setVisibility(View.VISIBLE);
        recyclerView = bindView(R.id.recycler);
    }

    @Override
    protected void initData() {
        adapter = new FriendApplyAdapter(data, context, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showLoading();
                BusinessController.sendFriendApplyResult(data.get(position).getTelephone(), true, new ISendListener() {
                    @Override
                    public void sendSuccess() {
                        loadingDialog.dismiss();
                        DBManager.getInstance(context).insertFriend(
                                new FriendBean(MyApplication.TEL + "", data.get(position).getName(), data.get(position).getNickName(), data.get(position).getTelephone()));

                        ChatMsgBean bean = new ChatMsgBean();
                        bean.setSendType(StatusConstant.SEND_TYPE_RECEIVER);
                        bean.setNowLoginTel(MyApplication.TEL + "");
                        bean.setTime(System.currentTimeMillis() + "");
                        bean.setStatus(StatusConstant.READ_READED);
                        bean.setIsGroup(true);
                        bean.setMessageType(StatusConstant.TYPE_GROUP_NOTIFY_MESSAGE);
                        bean.setInsertTime(new Date());
                        bean.setGroupId("");
                        bean.setTelephone(data.get(position).getTelephone());
//                        bean.setMsg("您邀请了" + getGroupMember(groupMembers14) + "进入群聊");

                        MyQmuiDialog.showSuccessDialog(FriendApplyActivity.this, "添加成功");
                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_FRIEND_REFRESH));
                        EventBus.getDefault().post(new MsgEntity("", StatusConstant.TYPE_FRIEND_APPLY_REFRESH));
                    }

                    @Override
                    public void sendFail(String reason) {
                        loadingDialog.dismiss();
                        MyQmuiDialog.showErrorDialog(FriendApplyActivity.this, reason);
                    }
                }, NumConstant.getJHDNum());
            }

            @Override
            public void onItemLongClick(int position) {

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        loadData();
    }

    @Override
    protected void initEvent() {
        NotificationUtil.getInstance().cancelNotification(false, 0);
        bindView(R.id.line_friends).setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                FriendCommitActivity.startFriendCommitActivity(context, true, "", "");
            }
        });
        bindView(R.id.line_contacts).setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                boolean isAllGranted = checkPermissionAllGranted(
                        permissionList
                );
                if (isAllGranted) {
                    ContactsActivity.startContactsActivity(context);
                } else {
                    ActivityCompat.requestPermissions(
                            FriendApplyActivity.this,
                            permissionList,
                            0
                    );
                }

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MsgEntity entity) {
        if (entity.getType() == StatusConstant.TYPE_FRIEND_APPLY_REFRESH) {
            runOnUiThread(this::loadData);
        }

    }

    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
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
            if (isAllGranted) {
                new Thread(() -> ContactsUtil.getAllContacts(context)).start();
            } else {
                toast("请到【设置】里打开遨海通APP的通讯录权限,否则无法正常使用");
            }
        }
    }


    private void loadData() {
        data.clear();
        data.addAll(DBManager.getInstance(context).selectFriendApply(""));
        adapter.notifyDataSetChanged();
    }
}
