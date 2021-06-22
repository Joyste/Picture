package com.test.picture.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.engine.ImageEngine;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.test.picture.R;
import com.test.picture.tool.GlideEngine;
import com.test.picture.utils.FileUtils;
import com.test.picture.view.activity.BaseActivity;
import com.test.picture.view.activity.CameraActivity;
import com.test.picture.view.activity.ShowResultActivity;
import com.test.picture.tool.PictureSelectorTool;
import com.test.picture.utils.DataUtil;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import static com.huantansheng.easyphotos.setting.Setting.imageEngine;
import static com.luck.picture.lib.config.PictureConfig.SINGLE;

public class MainActivity extends BaseActivity {


    /**
     * 相机权限请求标识
     */
    private static final int REQUEST_CAMERA_CODE = 0x100;

    private static final int REQUEST_PUZZLE_CODE = 4;

    private MainActivity context;
    private ImageView imgView;
    private Button openAblumForMultiple;//打开图库多选
    private Button openAblumForSingle;//打开图库单选
    private Button editImage;//编辑照片
    private Button mTakenPhoto;//拍摄照片用于编辑
    private int imageWidth, imageHeight;//


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
            case R.id.take_photo: {
                takePhotoClick();
                break;
            }

            case R.id.edit_image: {
                editImageClick();
                break;
            }

            case R.id.select_album_multiple: {
                selectFromAlbumMultiple();
                break;
            }
            case R.id.select_album_single: {
                selectFromAlbumSingle();
                break;
            }
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
                /**
                 * 启动拼图（最多对9张图片进行拼图）
                 *
                 * @param act            上下文
                 * @param photos         图片集合（最多对9张图片进行拼图）
                 * @param puzzleSaveDirPath    拼图完成保存的文件夹全路径
                 * @param puzzleSaveNamePrefix 拼图完成保存的文件名前缀，最终格式：前缀+默认生成唯一数字标识+.png
                 * @param requestCode    请求code
                 * @param replaceCustom  单击替换拼图中的某张图片时，是否以startForResult的方式启动你的自定义界面，该界面与传进来的act为同一界面。false则在EasyPhotos内部完成，正常需求直接写false即可。 true的情况适用于：用于拼图的图片集合中包含网络图片，是在你的act界面中获取并下载的（也可以直接用网络地址，不用下载后的本地地址，也就是可以不下载下来），而非单纯本地相册。举例：你的act中有两个按钮，一个指向本地相册，一个指向网络相册，用户在该界面任意选择，选择好图片后跳转到拼图界面，用户在拼图界面点击替换按钮，将会启动一个新的act界面，这时，act只让用户在网络相册和本地相册选择一张图片，选择好执行
                 *                       Intent intent = new Intent();
                 *                       intent.putParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS , photos);
                 *                       act.setResult(RESULT_OK,intent); 并关闭act，回到拼图界面，完成替换。
                 * @param imageEngine          图片加载引擎的具体实现
                 */

                EasyPhotos.startPuzzleWithPhotos(MainActivity.this,  getPathList(result), FileUtils.createFolders().getPath(), "puzzle", REQUEST_PUZZLE_CODE, false, new ImageEngine() {
                    @Override
                    public void loadPhoto(@NonNull @NotNull Context context, @NonNull @NotNull Uri uri, @NonNull @NotNull ImageView imageView) {

                    }

                    @Override
                    public void loadGifAsBitmap(@NonNull @NotNull Context context, @NonNull @NotNull Uri gifUri, @NonNull @NotNull ImageView imageView) {

                    }

                    @Override
                    public void loadGif(@NonNull @NotNull Context context, @NonNull @NotNull Uri gifUri, @NonNull @NotNull ImageView imageView) {

                    }

                    @Override
                    public Bitmap getCacheBitmap(@NonNull @NotNull Context context, @NonNull @NotNull Uri uri, int width, int height) throws Exception {
                        return null;
                    }
                });
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
        new PictureSelectorTool(MainActivity.this, SINGLE, new OnResultCallbackListener<LocalMedia>() {
            @Override
            public void onResult(List<LocalMedia> result) {
                Log.d("test",result.toString());
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
     * 获取地址集合
     */
    private ArrayList<Photo> getPathList(List<LocalMedia> result){
        ArrayList<Photo> photos = new ArrayList<>();
        for (LocalMedia localMedia : result) {
            String fileName = localMedia.getFileName();
            String path = localMedia.getPath();
            Uri uri = Uri.parse(path);
            int width = localMedia.getWidth();
            int height = localMedia.getHeight();
            int orientation = localMedia.getOrientation();
            long size = localMedia.getSize();
            long dateAddedTime = localMedia.getDateAddedTime();
            String mimeType = localMedia.getMimeType();


            Photo photo = new Photo(fileName,uri,path,dateAddedTime,width,height,orientation,size,0,mimeType) ;
            photos.add(photo);
        }
        return photos;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(null == data){
            //处理错误，错误原因大多因为存储空间不足而保存失败
            return;
        }

        Photo puzzlePhoto = data.getParcelableExtra(EasyPhotos.RESULT_PHOTOS); //获取拼图文件的Photo对象


        Bitmap bitmap=BitmapFactory.decodeFile(puzzlePhoto.path);
        DataUtil.getInstance().saveData("photo", bitmap);
        startActivity(ShowResultActivity.class);
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
                Toast.makeText(getApplicationContext(), R.string.toast_permissions, Toast.LENGTH_SHORT).show();
            }
        }
    }


}