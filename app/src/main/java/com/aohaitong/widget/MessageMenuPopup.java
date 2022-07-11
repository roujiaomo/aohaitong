package com.aohaitong.widget;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aohaitong.R;
import com.aohaitong.interfaces.OnItemClickListener;
import com.lxj.xpopup.core.BubbleAttachPopupView;

public class MessageMenuPopup extends BubbleAttachPopupView {
    private OnItemClickListener mListener;

    public MessageMenuPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_message_menu;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        TextView tvAddFriend = findViewById(R.id.tv_add_friend);
        TextView tvAddGroup = findViewById(R.id.tv_add_group);
        tvAddFriend.setOnClickListener(v -> {
            mListener.onItemClick(0);
        });
        tvAddGroup.setOnClickListener(v -> {
            mListener.onItemClick(1);
        });
    }

    public void setOnItemListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
