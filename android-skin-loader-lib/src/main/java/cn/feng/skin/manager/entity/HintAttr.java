package cn.feng.skin.manager.entity;

import android.view.View;
import android.widget.EditText;

import cn.feng.skin.manager.loader.SkinManager;

public class HintAttr extends SkinAttr {

    public int dividerHeight = 1;

    @Override
    public void apply(View view) {
        if (view instanceof EditText) {
            EditText tv = (EditText) view;
            if (RES_TYPE_NAME_COLOR.equals(attrValueTypeName)) {
                tv.setHintTextColor(SkinManager.getInstance().getColor(attrValueRefId));
            }
        }
    }
}
