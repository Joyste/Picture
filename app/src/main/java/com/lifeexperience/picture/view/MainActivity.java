package com.lifeexperience.picture.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.lifeexperience.picture.view.activity.ShowResultActivity;
import com.lifeexperience.picture.view.tool.PictureSelectorTool;
import com.lifeexperience.picture.view.utils.DataUtil;
import com.lifeexperience.picture.view.utils.ToastUtil;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;

import java.io.File;
import java.util.List;

import static com.luck.picture.lib.config.PictureConfig.SINGLE;

public class MainActivity extends BaseActivity {


    /**
     * 相机权限请求标识
     */
    private static final int REQUEST_CAMERA_CODE = 0x100;

    private MainActivity context;
    private ImageView imgView;
    private Button openAblumForMultiple;//打开图库多选
    private Button openAblumForSingle;//打开图库单选
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
        openAblumForMultiple = findViewById(R.id.select_album_multiple);
        openAblumForSingle = findViewById(R.id.select_album_single);
        editImage = findViewById(R.id.edit_image);
        mTakenPhoto = findViewById(R.id.take_photo);

    }

    @Override
    protected void initListeners() {
        openAblumForMultiple.setOnClickListener(this);
        openAblumForSingle.setOnClickListener(this);
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
            case R.id.select_album_multiple:
                selectFromAlbumMultiple();
                break;
            case R.id.select_album_single:
                selectFromAlbumSingle();
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
     * 从图库中选择图片 多选
     */
    private void selectFromAlbumMultiple() {
        new PictureSelectorTool(MainActivity.this, new OnResultCallbackListener<LocalMedia>() {
            @Override
            public void onResult(List<LocalMedia> result) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    /**
     * 从图库中选择图片 单选
     */
    private void selectFromAlbumSingle() {
        new PictureSelectorTool(MainActivity.this,SINGLE, new OnResultCallbackListener<LocalMedia>() {
            @Override
            public void onResult(List<LocalMedia> result) {
                //Intent 无法传过大的数据，将数据转存进hashmap中。
                String path = result.get(0).getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                DataUtil.getInstance().saveData("photo", bitmap);
                startActivity(ShowResultActivity.class);
            }

            @Override
            public void onCancel() {

            }
        });
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