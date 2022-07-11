package com.aohaitong.interfaces;

import android.view.View;

public interface OnChatItemClickListener {
    void onItemClick(int position, int viewId);

    void onItemLongClick(int position, View view, int sendType);
}
