package com.test.picture.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.test.picture.view.activity.CameraActivity;
import com.test.picture.view.activity.CustomPuzzleActivity;
import com.test.picture.view.activity.TakePhotoActivity;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.test.picture.R;
import com.test.picture.tool.PhotoTool;
import com.test.picture.utils.FileUtils;
import com.test.picture.utils.ToastUtil;
import com.test.picture.view.activity.BaseActivity;
import com.test.picture.view.activity.Camera2Activity;
import com.test.picture.view.activity.ShowResultActivity;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends BaseActivity {


    /**
     * 相机权限请求标识
     */
    public static final int REQUEST_CAMERA_CODE = 0x100;//拍照

    public static final int REQUEST_PUZZLE_CODE = 4;  //拼图

    public static final int REQUEST_SINGLE_CODE = 5; //图库单选

    public static final int REQUEST_MULTIPLE_CODE_FOR_PUZZLE = 6; //多选

    public static final int REQUEST_CUSTOM_PUZZLE_CODE = 7; //自定义拼图

    public static final int REQUEST_EDITIMAGE_CODE = 8;//编辑图片

    public static final int ACTION_REQUEST_EDITIMAGE = 9;

    private MainActivity context;
    private Button openAblumForMultiple;//打开图库多选
    private Button openAblumForSingle;//打开图库单选
    private Button editImage;//编辑照片
    private Button mTakenPhoto;//拍摄照片用于编辑
    private Button customPuzzle;//自定义拼图
    private int imageWidth, imageHeight;//
    private ArrayList<Photo> selectedPhotoList = new ArrayList<>();


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

        openAblumForMultiple = findViewById(R.id.select_album_multiple_for_puzzle);
        openAblumForSingle = findViewById(R.id.select_album_single);
        editImage = findViewById(R.id.edit_image);
        mTakenPhoto = findViewById(R.id.take_photo);
        customPuzzle = findViewById(R.id.custom_puzzle);

    }

    @Override
    protected void initListeners() {
        openAblumForMultiple.setOnClickListener(this);
        openAblumForSingle.setOnClickListener(this);
        editImage.setOnClickListener(this);
        mTakenPhoto.setOnClickListener(this);
        customPuzzle.setOnClickListener(this);
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

            case R.id.select_album_multiple_for_puzzle: {
                selectFromAlbumMultipleForPuzzle();
                break;
            }
            case R.id.select_album_single: {
                selectFromAlbumSingle();
                break;
            }
            case R.id.custom_puzzle: {
                startCustomPuzzle();
                break;
            }
        }//end switch
    }

    /**
     * 修改图片
     */
    private void editImageClick() {
        PhotoTool.getInstance().openAlbumForSingle(this, ACTION_REQUEST_EDITIMAGE);
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
    private void selectFromAlbumMultipleForPuzzle() {
        PhotoTool.getInstance().openAlbumForMultiple(this, 9, selectedPhotoList, REQUEST_MULTIPLE_CODE_FOR_PUZZLE);
    }

    /**
     * 从图库中选择图片 单选
     */
    private void selectFromAlbumSingle() {
        PhotoTool.getInstance().openAlbumForSingle(this, REQUEST_SINGLE_CODE);
    }

    /**
     * 自定义拼图
     */
    private void startCustomPuzzle() {
        PhotoTool.getInstance().openAlbumForMultiple(this, 9, selectedPhotoList, REQUEST_CUSTOM_PUZZLE_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SINGLE_CODE: {
                    //获取单选的返回数据
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                    showResult(resultPhotos.get(0).path);
                    break;
                }
                case REQUEST_MULTIPLE_CODE_FOR_PUZZLE: {
                    //获取多选的数据
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                    selectedPhotoList.clear();
                    selectedPhotoList.addAll(resultPhotos);

                    if (resultPhotos.size() == 1) {
                        //如果图片数量为1，则提示用户并重新选择。
                        ToastUtil.showShortToast(this.getString(R.string.puzzle_toast));
                        showResult(resultPhotos.get(0).path);
                    } else {
                        //大于1，则进入拼图界面
                        PhotoTool.getInstance().startPuzzleWithPhotos(MainActivity.this, resultPhotos, REQUEST_PUZZLE_CODE);
                    }

                    break;
                }
                case REQUEST_PUZZLE_CODE: {
                    //拼图返回结果
                    if (null == data) {
                        return;
                    }
                    Photo puzzlePhoto = data.getParcelableExtra(EasyPhotos.RESULT_PHOTOS); //获取拼图文件的Photo对象
                    showResult(puzzlePhoto.path);
                    break;
                }
                case REQUEST_CUSTOM_PUZZLE_CODE: {
                    //获取多选的数据
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);

                    ArrayList<String> resultPhotosPath = new ArrayList<>();

                    for (Photo photo : resultPhotos) {
                        resultPhotosPath.add(photo.path);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("pathList", resultPhotosPath);
                    startActivity(CustomPuzzleActivity.class, bundle);
                    break;
                }
                case ACTION_REQUEST_EDITIMAGE: {
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                    File outputFile = FileUtils.getEditFile();
                    EditImageActivity.start(this, resultPhotos.get(0).path, outputFile.getAbsolutePath(), REQUEST_EDITIMAGE_CODE);
                    break;
                }
                case REQUEST_EDITIMAGE_CODE: {
                    handleEditorImage(data);
                    break;
                }

            }
        }
    }


    private void handleEditorImage(Intent data) {
        String newFilePath = data.getStringExtra(EditImageActivity.EXTRA_OUTPUT);
        boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IMAGE_IS_EDIT, false);

        if (!isImageEdit) {//未编辑  还是用原来的图片
            newFilePath = data.getStringExtra(EditImageActivity.FILE_PATH);
        }
        showResult(newFilePath);
    }

    /**
     * 传图片地址用于显示结果
     *
     * @param path
     */
    private void showResult(String path) {
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        startActivity(ShowResultActivity.class, bundle);
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
//            startActivity(TakePhotoActivity.class);

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
                startActivity(Camera2Activity.class);
            } else {
                // 权限拒绝
                Toast.makeText(getApplicationContext(), R.string.toast_permissions, Toast.LENGTH_SHORT).show();
            }
        }
    }


}