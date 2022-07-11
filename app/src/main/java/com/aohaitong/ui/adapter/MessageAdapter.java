package com.aohaitong.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aohaitong.R;
import com.aohaitong.base.BaseViewHolder;
import com.aohaitong.bean.MessageBean;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.interfaces.OnItemClickListener;
import com.aohaitong.utils.StringUtil;
import com.aohaitong.utils.TimeUtil;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final Context context;
    private final OnItemClickListener itemClickListener;
    private final List<MessageBean> data;

    public MessageAdapter(Context context, List<MessageBean> data, OnItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.data = data;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.createRvViewHolder(context, parent, R.layout.item_message);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.setText(R.id.tv_name, StringUtil.getFirstNotNullString(new String[]{data.get(position).getNickName(),
                data.get(position).getName(), data.get(position).getTelephone()}));
        String showContent = data.get(position).getMessage();
        int messageType = data.get(position).getMessageType();
        if (messageType == StatusConstant.TYPE_RECORD_MESSAGE) {
            showContent = "[语音]";
        } else if (messageType == StatusConstant.TYPE_PHOTO_MESSAGE) {
            showContent = "[图片]";
        } else if (messageType == StatusConstant.TYPE_VIDEO_MESSAGE) {
            showContent = "[视频]";
        }
        holder.setText(R.id.tv_message, showContent);
        holder.setText(R.id.tv_time, TimeUtil.stampToDateMinWithOutDay(data.get(position).getTime()));
        holder.getView(R.id.line).setOnClickListener(view -> itemClickListener.onItemClick(position));
        holder.getView(R.id.line).setOnLongClickListener(v -> {
            itemClickListener.onItemLongClick(position);
            return false;
        });

        if (TextUtils.isEmpty(data.get(position).getUnReadCount())) {
            holder.setViewVisiable(R.id.tv_unread, View.GONE);
            holder.setViewVisiable(R.id.img_unread, View.GONE);
        } else {
            holder.setViewVisiable(R.id.tv_unread, View.VISIBLE);
            holder.setViewVisiable(R.id.img_unread, View.VISIBLE);
            holder.setText(R.id.tv_unread, data.get(position).getUnReadCount().length() > 2 ? "99" : data.get(position).getUnReadCount());
        }

    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }
}
