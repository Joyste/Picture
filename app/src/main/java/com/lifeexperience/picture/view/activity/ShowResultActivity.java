package com.lifeexperience.picture.view.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.lifeexperience.picture.R;
import com.lifeexperience.picture.view.utils.DataUtil;
import com.lifeexperience.picture.view.utils.FileUtils;
import com.lifeexperience.picture.view.utils.ToastUtil;

import java.io.File;

public class ShowResultActivity extends BaseActivity {

    private String TAG = "ShowResultActivity";
    private ImageView ivShowPhoto;
    private Button btnBack;
    private Button btnSave;
    private Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_result;
    }


    @Override
    protected void initView() {
        ivShowPhoto = findViewById(R.id.iv_show_photo);
        btnBack = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);

        photo = (Bitmap) DataUtil.getInstance().getData("photo");
        ivShowPhoto.setImageBitmap(photo);


    }

    @Override
    protected void initListeners() {
        btnBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_save:
                //地址
                String path = savePicture(photo);
                Log.d(TAG, path);
                ToastUtil.showShortToast("已保存到：" + path);
                finish();
                break;
        }
    }


    /**
     * 保存图片
     */
    private String savePicture(Bitmap bitmap) {
        String filename = File.separator + System.currentTimeMillis() + ".jpeg";
        String path = FileUtils.saveBitmap(filename, bitmap);
        return path;
    }


}