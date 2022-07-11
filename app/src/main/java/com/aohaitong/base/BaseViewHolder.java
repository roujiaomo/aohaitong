package com.aohaitong.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spanned;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

/**
 * recyclerview的通用viewholder
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private final View mview;

    private final SparseArray<View> mSpareArray;

    private final Context context;

    public BaseViewHolder(View itemView, Context context) {
        super(itemView);
        mview = itemView;
        mSpareArray = new SparseArray<>();
        this.context = context;
    }

    //针对RV的初始化操作
    public static BaseViewHolder createRvViewHolder(Context context, ViewGroup group, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, group, false);
        return new BaseViewHolder(itemView, context);
    }

    //针对ListView的初始化操作
    public static BaseViewHolder createListViewHolder(Context context, View view, ViewGroup group, int layoutId) {
        BaseViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(layoutId, group, false);
            holder = new BaseViewHolder(view, context);
            view.setTag(holder);
        } else {
            holder = (BaseViewHolder) view.getTag();
        }
        return holder;
    }

    public <T extends View> T getView(int id) {
        View view = mSpareArray.get(id);
        if (view == null) {
            view = mview.findViewById(id);
            mSpareArray.put(id, view);
        }
        return (T) view;
    }


    public BaseViewHolder setText(int id, String s) {
        TextView textView = getView(id);
        if (s != null) {
            textView.setText(s);
        } else {
            textView.setText("");
        }
        return this;
    }

    public BaseViewHolder setText(int id, Spanned s) {
        TextView textView = getView(id);
        if (s != null) {
            textView.setText(s);
        }
        return this;
    }

    public BaseViewHolder setChecked(int id, Boolean i) {
        RadioButton textView = getView(id);
        textView.setChecked(i);
        return this;
    }

    public BaseViewHolder setCheckBoxChecked(int id, Boolean i) {
        CheckBox textView = getView(id);
        textView.setChecked(i);
        return this;
    }

    public ImageView getImg(int id) {
        return getView(id);
    }

    public BaseViewHolder setViewVisiable(int id, int type) {
        View view = getView(id);
        if (type == View.INVISIBLE) {
            view.setVisibility(View.INVISIBLE);
        } else if (type == View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        return this;
    }

    public BaseViewHolder setTextColor(int id, int color) {
        TextView textView = getView(id);
        textView.setTextColor(color);
        return this;
    }


    //设置文字右边的图片
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseViewHolder setTextImg(int id, int resouce) {
        TextView textView = getView(id);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable nav_up = context.getDrawable(resouce);
        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
        textView.setCompoundDrawables(null, null, nav_up, null);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseViewHolder setTextleftImg(int id, int resouce) {
        TextView textView = getView(id);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable nav_up = context.getDrawable(resouce);
        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
        textView.setCompoundDrawables(nav_up, null, null, null);
        return this;
    }

    public View getMview() {
        return mview;
    }


}
