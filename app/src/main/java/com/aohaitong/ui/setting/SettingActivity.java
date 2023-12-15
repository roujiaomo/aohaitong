package com.aohaitong.ui.setting;

import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aohaitong.R;
import com.aohaitong.base.BaseActivity;
import com.aohaitong.utils.dialog.MyQmuiDialog;

import java.io.File;

public class SettingActivity extends BaseActivity {

    private static final String NIGHT_SKIN_NAME = "night";
    private static final String DUSK_SKIN_NAME = "dusk";

    private LinearLayout default_skin, dusk_skin, night_skin;
    private ImageView default_img, dusk_img, night_img;


    @Override
    protected int getLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        default_img = bindView(R.id.img_default);
        dusk_img = bindView(R.id.img_dusk);
        night_img = bindView(R.id.img_night);
        default_skin = bindView(R.id.line_default);
        dusk_skin = bindView(R.id.line_dusk);
        night_skin = bindView(R.id.line_night);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initEvent() {
        default_skin.setOnClickListener(v -> onSkinResetClick());
        dusk_skin.setOnClickListener(v -> onSkinSetClick(DUSK_SKIN_NAME));
        night_skin.setOnClickListener(v -> onSkinSetClick(NIGHT_SKIN_NAME));
    }

    protected void onSkinResetClick() {
        MyQmuiDialog.showSuccessDialog(SettingActivity.this, "切换成功");
    }

    private void onSkinSetClick(String name) {
        File dataFile = new File(getCacheDir(), name + ".skin");
        if (!dataFile.exists()) {
            MyQmuiDialog.showErrorDialog(SettingActivity.this, "皮肤文件不存在");
            return;
        }
    }

}
