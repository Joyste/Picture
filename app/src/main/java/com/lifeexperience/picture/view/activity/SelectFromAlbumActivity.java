package com.lifeexperience.picture.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.lifeexperience.picture.R;
import com.lifeexperience.picture.view.MainActivity;
import com.lifeexperience.picture.view.tool.GlideEngine;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;

import java.util.List;

public class SelectFromAlbumActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_from_album;
    }

    @Override
    protected void initView() {
        PictureSelector.create(SelectFromAlbumActivity.this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                .isNotPreviewDownload(true)// 预览图片长按是否可以下载
                .theme(R.style.picture_QQ_style)
                .maxSelectNum(9)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        // 结果回调
                    }

                    @Override
                    public void onCancel() {
                        // 取消
                    }
                });
    }

    @Override
    protected void initListeners() {

    }

    @Override
    public void onClick(View v) {

    }
}