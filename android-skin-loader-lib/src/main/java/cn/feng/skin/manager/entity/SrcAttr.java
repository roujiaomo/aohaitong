package cn.feng.skin.manager.entity;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import cn.feng.skin.manager.loader.SkinManager;

public class SrcAttr extends SkinAttr {

    public int dividerHeight = 1;

    @Override
    public void apply(View view) {
        if (view instanceof ImageView) {
            ImageView tv = (ImageView) view;
            if (RES_TYPE_NAME_MIPMAP.equals(attrValueTypeName)) {
                Drawable bg = SkinManager.getInstance().getMipmap(attrValueRefId);
                tv.setImageDrawable(bg);
            } else if (RES_TYPE_NAME_DRAWABLE.equals(attrValueTypeName)) {
                Drawable bg = SkinManager.getInstance().getDrawable(attrValueRefId);
                tv.setImageDrawable(bg);
            }
        }
    }
}
