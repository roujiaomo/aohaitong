package com.aohaitong.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aohaitong.R;
import com.aohaitong.base.BaseViewHolder;
import com.aohaitong.bean.FriendApplyBean;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.interfaces.OnItemClickListener;
import com.aohaitong.utils.NoDoubleClickListener;
import com.aohaitong.utils.StringUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class FriendApplyAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private final List<FriendApplyBean> data;
    private final Context context;
    private final OnItemClickListener clickListener;

    public FriendApplyAdapter(List<FriendApplyBean> data, Context context, OnItemClickListener clickListener) {
        this.data = data;
        this.context = context;
        this.clickListener = clickListener;
    }


    @NonNull
    @NotNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return BaseViewHolder.createRvViewHolder(context, parent, R.layout.item_friend_apply);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BaseViewHolder holder, int position) {
        if (data.size() < position)
            return;
        holder.setText(R.id.tvName, StringUtil.getFirstNotNullString(new String[]{data.get(position).getNickName(), data.get(position).getName(), data.get(position).getTelephone()}));
        holder.setText(R.id.tv_des, data.get(position).getDescription());
        holder.setText(R.id.tv_des_detail, data.get(position).getDescription());
        TextView tvDes = holder.getView(R.id.tv_des);
        TextView tvDesDetail = holder.getView(R.id.tv_des_detail);

        if (TextUtils.isEmpty(data.get(position).getDescription())) {
            tvDes.setVisibility(View.GONE);
        } else {
            tvDes.setVisibility(View.VISIBLE);
        }
        tvDesDetail.setVisibility(View.GONE);
        tvDes.setOnClickListener(v -> {
            tvDes.setVisibility(View.GONE);
            tvDesDetail.setVisibility(View.VISIBLE);
        });
        tvDesDetail.setOnClickListener(v -> {
            tvDesDetail.setVisibility(View.GONE);
            tvDes.setVisibility(View.VISIBLE);
        });
        if (data.get(position).getType() == StatusConstant.TYPE_UN_PASS && data.get(position).getSendType() == StatusConstant.SEND_TYPE_RECEIVER) {
            holder.getView(R.id.tv_add).setBackground(ContextCompat.getDrawable(context, R.drawable.blue_5));
            ((TextView) holder.getView(R.id.tv_add)).setTextColor(Color.WHITE);
            ((TextView) holder.getView(R.id.tv_add)).setText("同意");
            holder.getView(R.id.tv_add).setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    clickListener.onItemClick(position);
                }
            });
        } else {
            holder.getView(R.id.tv_add).setBackground(null);
            ((TextView) holder.getView(R.id.tv_add)).setText(data.get(position).getType() == StatusConstant.TYPE_UN_PASS ? "等待中" : data.get(position).getType() == StatusConstant.TYPE_FORBIDDEN ? "被拒绝" : "已通过");
            ((TextView) holder.getView(R.id.tv_add)).setTextColor(ContextCompat.getColor(context, R.color.base_list_text_gray));
            holder.getView(R.id.tv_add).setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }
}
