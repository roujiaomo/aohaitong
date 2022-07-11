package com.aohaitong.ui.adapter;

import static com.aohaitong.constant.StatusConstant.ITEM_TYPE_CONTENT;
import static com.aohaitong.constant.StatusConstant.ITEM_TYPE_HEADER;

import com.aohaitong.R;
import com.aohaitong.ui.model.FriendLetterIndexModel;
import com.aohaitong.utils.StringUtil;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class FriendAdapter extends BaseMultiItemQuickAdapter<FriendLetterIndexModel, BaseViewHolder> {

    public FriendAdapter() {
        super(null);
        addItemType(ITEM_TYPE_HEADER, R.layout.item_letter_index_header);
        addItemType(ITEM_TYPE_CONTENT, R.layout.item_friend_list);
    }

    @Override
    protected void convert(BaseViewHolder holder, FriendLetterIndexModel model) {
        holder.setText(R.id.tvName, StringUtil.getFirstNotNullString(new String[]{model.nickName, model.name, model.tel}));
    }
}
