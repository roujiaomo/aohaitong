package com.aohaitong.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.ListView;
import android.widget.TextView;

import com.aohaitong.R;
import com.aohaitong.base.BaseActivity;
import com.aohaitong.constant.CommonConstant;
import com.aohaitong.ui.adapter.RingSettingAdapter;
import com.aohaitong.utils.SPUtil;

import java.util.ArrayList;
import java.util.List;

public class RingSettingActivity extends BaseActivity {

    private final ArrayList<String> ringtoneList = new ArrayList<>();
    private ListView listView;
    private final RingtoneManager manager = new RingtoneManager(this);
    private RingSettingAdapter adapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_ring_setting;
    }

    public static void startRingSettingActivity(Context context) {
        Intent intent = new Intent(context, RingSettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        loadingDialog.show();
        ((TextView) bindView(R.id.tv_title)).setText("铃声设置");
        listView = (ListView) findViewById(R.id.ringtone);
        adapter = new RingSettingAdapter(this, ringtoneList, getIndex());
        int index = SPUtil.instance.getInt(CommonConstant.SP_RING_INDEX);
        adapter.setCheckIndex(index == -1 ? 0 : index);
    }

    @Override
    protected void initData() {
        new Thread(this::getRingtone).start();
        listView.setAdapter(adapter);
        listView.setSelection(getIndex());
    }

    @Override
    protected void initEvent() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            adapter.setCheckIndex(position);
            SPUtil.instance.putValues(new SPUtil.ContentValue(CommonConstant.SP_RING_INDEX, position));
            if (position == 0) {
                // 得到系统默认的消息uri
                Uri defalutUri = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                // 通过URI获得系统默认的Ringtone发出声音
                Ringtone defalutRingtone = RingtoneManager.getRingtone(
                        RingSettingActivity.this, defalutUri);
                defalutRingtone.play();
                SPUtil.instance.putValues(new SPUtil.ContentValue(CommonConstant.SP_RING_NAME, defalutUri.toString()));
            } else {
                // 当点击的item不是第一个“跟随系统”时，获得的铃声要减一才对
                Ringtone ringtone = manager.getRingtone(position - 1);
                SPUtil.instance.putValues(new SPUtil.ContentValue(CommonConstant.SP_RING_NAME, manager.getRingtoneUri(position - 1).toString()));
                ringtone.play();
            }
            adapter.notifyDataSetChanged();

        });
    }

    //得到当前铃声的行数
    private int getIndex() {
        for (int i = 0; i < ringtoneList.size(); i++) {
            if (ringtoneList.get(i).equals(SPUtil.instance.getString(CommonConstant.SP_RING_NAME))) {
                return i;
            }
        }
        return 0;
    }


    //得到ringtone中的所有消息声音
    private void getRingtone() {
        manager.setType(RingtoneManager.TYPE_NOTIFICATION);
        Cursor cursor = manager.getCursor();
        int num = cursor.getCount();
        List<String> data = new ArrayList<>();
        for (int i = -1; i < num; i++) {
            if (i == -1) {
                data.add("跟随系统");
            } else {
                try {
                    Ringtone ringtone = manager.getRingtone(i);
                    String title = ringtone.getTitle(this);
                    data.add(title);
                } catch (Exception e) {

                }
            }
        }
        ringtoneList.clear();
        ringtoneList.addAll(data);
        loadingDialog.dismiss();
        runOnUiThread(() -> adapter.notifyDataSetChanged());
    }

}
