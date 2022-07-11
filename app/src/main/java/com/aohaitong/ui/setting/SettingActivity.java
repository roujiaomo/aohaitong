package com.aohaitong.ui.setting;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aohaitong.R;
import com.aohaitong.base.BaseActivity;
import com.aohaitong.constant.CommonConstant;
import com.aohaitong.utils.SPUtil;
import com.aohaitong.utils.StatusBarUtils;
import com.aohaitong.utils.dialog.MyQmuiDialog;

import java.io.File;

import cn.feng.skin.manager.listener.ILoaderListener;
import cn.feng.skin.manager.loader.SkinManager;

public class SettingActivity extends BaseActivity {

    private static final String NIGHT_SKIN_NAME = "night";
    private static final String DUSK_SKIN_NAME = "dusk";

    private LinearLayout default_skin, dusk_skin, night_skin;
    private ImageView default_img, dusk_img, night_img;

    private String chooseSkin = "default";

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
        chooseSkin = SPUtil.instance.getString(CommonConstant.SP_CHOOSE_SKIN);
        initCheckView();
    }

    @Override
    protected void initEvent() {
        default_skin.setOnClickListener(v -> onSkinResetClick());
        dusk_skin.setOnClickListener(v -> onSkinSetClick(DUSK_SKIN_NAME));
        night_skin.setOnClickListener(v -> onSkinSetClick(NIGHT_SKIN_NAME));
    }

    protected void onSkinResetClick() {
        if ("default".equals(chooseSkin) || TextUtils.isEmpty(chooseSkin)) {
            return;
        }
        SkinManager.getInstance().restoreDefaultTheme();
        MyQmuiDialog.showSuccessDialog(SettingActivity.this, "切换成功");
        chooseSkin = "default";
        SPUtil.instance.putValues(new SPUtil.ContentValue(CommonConstant.SP_CHOOSE_SKIN, chooseSkin));
        initCheckView();
    }

    private void onSkinSetClick(String name) {
        if (name.equals(chooseSkin)) {
            return;
        }
        File dataFile = new File(getCacheDir(), name + ".skin");
        if (!dataFile.exists()) {
            MyQmuiDialog.showErrorDialog(SettingActivity.this, "皮肤文件不存在");
            return;
        }
        SkinManager.getInstance().load(dataFile.getAbsolutePath(),
                new ILoaderListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess() {
                        chooseSkin = name;
                        initCheckView();
                        SPUtil.instance.putValues(new SPUtil.ContentValue(CommonConstant.SP_CHOOSE_SKIN, name));
                        MyQmuiDialog.showSuccessDialog(SettingActivity.this, "切换成功");
                    }

                    @Override
                    public void onFailed() {
                        MyQmuiDialog.showErrorDialog(SettingActivity.this, "切换失败");
                    }
                });
    }

    private void initCheckView() {
        StatusBarUtils.setColor(SettingActivity.this, SkinManager.getInstance().getColor(R.color.blue_base));
        default_img.setVisibility(View.GONE);
        dusk_img.setVisibility(View.GONE);
        night_img.setVisibility(View.GONE);
        if ("default".equals(chooseSkin) || TextUtils.isEmpty(chooseSkin)) {
            default_img.setVisibility(View.VISIBLE);
        }
        if ("dusk".equals(chooseSkin)) {
            dusk_img.setVisibility(View.VISIBLE);
        }
        if ("night".equals(chooseSkin)) {
            night_img.setVisibility(View.VISIBLE);
        }

    }
}
