package com.zhaoss.weixinrecorded.activity;

import static com.zhaoss.weixinrecorded.activity.RecordedActivity.INTENT_PATH;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhaoss.weixinrecorded.R;

public class EditPhotoActivity extends BaseActivity {

    private TextView tv_finish_video;
    private ImageView ivPhoto;
    private String filePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_photo);
        filePath = getIntent().getStringExtra(INTENT_PATH);
        tv_finish_video = findViewById(R.id.tv_finish_video);
        ivPhoto = findViewById(R.id.iv_photo);
        Glide.with(this).load(filePath).into(ivPhoto);

        tv_finish_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(INTENT_PATH, filePath);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }
}