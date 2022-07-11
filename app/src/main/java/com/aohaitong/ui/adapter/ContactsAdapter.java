package com.aohaitong.ui.adapter;

import static com.aohaitong.constant.StatusConstant.ITEM_TYPE_CONTENT;
import static com.aohaitong.constant.StatusConstant.ITEM_TYPE_HEADER;
import static com.aohaitong.constant.StatusConstant.TYPE_PASS;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import com.aohaitong.R;
import com.aohaitong.constant.StatusConstant;
import com.aohaitong.interfaces.MyOnClick;
import com.aohaitong.ui.model.ContactsLetterIndexModel;
import com.aohaitong.utils.NoDoubleClickListener;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.feng.skin.manager.loader.SkinManager;

public class ContactsAdapter extends BaseMultiItemQuickAdapter<ContactsLetterIndexModel, BaseViewHolder> {

    private MyOnClick onItemClickListener;

    public void setClick(MyOnClick onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ContactsAdapter() {
        super(null);
        addItemType(ITEM_TYPE_HEADER, R.layout.item_letter_index_header);
        addItemType(ITEM_TYPE_CONTENT, R.layout.item_contacts);
    }


    @SuppressLint("ResourceAsColor")
    @Override
    protected void convert(BaseViewHolder holder, ContactsLetterIndexModel model) {
        holder.setText(R.id.tvName, model.name);
        if (model.itemType == ITEM_TYPE_CONTENT) {
            if ((model.hasFriendApply == StatusConstant.TYPE_NOT_HAVE_APPLY && model.isFriend == StatusConstant.TYPE_IS_NOT_FRIEND) ||
                    (model.hasFriendApply == StatusConstant.TYPE_HAVE_APPLY && model.isFriend == StatusConstant.TYPE_IS_NOT_FRIEND &&
                            model.type == TYPE_PASS)) {
                holder.getView(R.id.tv_add).setBackground(SkinManager.getInstance().getDrawable(R.drawable.blue_5));
                ((TextView) holder.getView(R.id.tv_add)).setTextColor(SkinManager.getInstance().getColor(R.color.white));
                ((TextView) holder.getView(R.id.tv_add)).setText("添加");
                holder.getView(R.id.tv_add).setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    protected void onNoDoubleClick(View v) {
                        onItemClickListener.onItemClick(model);
                    }
                });
            } else {
                holder.getView(R.id.tv_add).setBackground(null);
                ((TextView) holder.getView(R.id.tv_add)).setTextColor(SkinManager.getInstance().getColor(R.color.base_list_text_gray));
                ((TextView) holder.getView(R.id.tv_add)).setText(model.isFriend == StatusConstant.TYPE_IS_NOT_FRIEND ? "已申请" : "已添加");
                holder.getView(R.id.tv_add).setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    protected void onNoDoubleClick(View v) {
                    }
                });
            }
        }
    }
}
