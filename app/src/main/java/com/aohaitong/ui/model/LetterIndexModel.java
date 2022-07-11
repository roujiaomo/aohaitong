package com.aohaitong.ui.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class LetterIndexModel implements MultiItemEntity {

    public String name;
    public String pinyin;
    public int itemType;
    public String tel;

    @Override
    public int getItemType() {
        return itemType;
    }
}
