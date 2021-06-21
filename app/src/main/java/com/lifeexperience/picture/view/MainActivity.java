package com.lifeexperience.picture.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lifeexperience.picture.R;
import com.lifeexperience.picture.view.activity.BaseActivity;
import com.lifeexperience.picture.view.activity.CameraActivity;

public class MainActivity extends BaseActivity {


    /**
     * 相机权限请求标识
     */
    private static final int REQUEST_CAMERA_CODE = 0x100;

    private MainActivity context;
    private ImageView imgView;
    private Button openAblum;//打开图库
    private Button editImage;//编辑照片
    private Button mTakenPhoto;//拍摄照片用于编辑
    private Bitmap mainBitmap;
    private int imageWidth, imageHeight;//
    private String path;


    private Uri photoURI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        context = this;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels;
        imageHeight = metrics.heightPixels;

        imgView = (ImageView) findViewById(R.id.img);
        openAblum = findViewById(R.id.select_album);
        editImage = findViewById(R.id.edit_image);
        mTakenPhoto = findViewById(R.id.take_photo);

    }

    @Override
    protected void initListeners() {
        openAblum.setOnClickListener(this);
        editImage.setOnClickListener(this);
        mTakenPhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_photo:
                takePhotoClick();
                break;
            case R.id.edit_image:
                editImageClick();
                break;
            case R.id.select_album:
                selectFromAlbum();
                break;
        }//end switch
    }

    /**
     * 修改图片
     */
    private void editImageClick() {

    }


    /**
     * 拍照
     */
    private void takePhotoClick() {
        checkPermissions();

    }

    /**
     * 从图库中选择图片
     */
    private void selectFromAlbum() {

    }


    /**
     * 申请权限
     */
    private void checkPermissions() {
        // 拍照及文件权限申请
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 权限还没有授予，需要在这里写申请权限的代码
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_CODE);
        } else {
            // 权限已经申请，直接拍照
            startActivity(CameraActivity.class);

        }
    }


    /**
     * 查看申请结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CAMERA_CODE == requestCode) {
            // 权限允许
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(CameraActivity.class);
            } else {
                // 权限拒绝
                Toast.makeText(getApplicationContext(), "无权限，请前往设置打开权限", Toast.LENGTH_SHORT).show();
            }
        }
    }


}