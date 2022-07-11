package com.aohaitong.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.aohaitong.R;
import com.aohaitong.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

public class RingSettingAdapter extends BaseAdapter {

    private final Context context;
    private final List<String> data;
    private int checkIndex;

    /**
     * @param context
     * @param ringtoneList 铃声列表
     * @param index        选择位置
     */
    public RingSettingAdapter(Context context, ArrayList<String> ringtoneList, int index) {
        this.context = context;
        data = ringtoneList;
        checkIndex = index;
    }

    public void setCheckIndex(int checkIndex) {
        this.checkIndex = checkIndex;
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return data != null ? data.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseViewHolder holder = BaseViewHolder.createListViewHolder(context, convertView, parent, R.layout.item_ring_setting);
        holder.setText(R.id.tv_ring, data.get(position));
        if (position == checkIndex) {
            holder.setViewVisiable(R.id.img_checked, View.VISIBLE);
        } else {
            holder.setViewVisiable(R.id.img_checked, View.GONE);
        }
        return holder.getMview();
    }
}
