package com.test.picture.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.test.picture.R;
import com.test.picture.tool.ImageLoader;
import com.test.picture.utils.FileOperateUtil;
import com.test.picture.view.widget.CameraView;
import com.test.picture.view.widget.FocusImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by v_yanligang on 2017/3/20.
 */

public class TakePhotoActivity extends BaseActivity implements View.OnClickListener {

    private static final int PERMISSION_CAMERA_REQUEST = 1;
    private static final int PERMISSION_WRITE_REQUEST = 2;
    private static final int PERMISSION_RECORD_AUDIO_REQUEST = 3;
    private CameraView mCv;
    private ImageView mThumbnailIv;
    private List<File> files;
    private ImageView mAnimateIv;
    private ImageView mFlashIv;
    private Button mCameraBt;

    private static List<CameraView.Effect> effectList = new ArrayList<>();
    private static List<String> effectName = new ArrayList<>();
    public interface OnPictureTakeListener {
        void onPicTaking();
        void onPicTakenend();
        void onAnimend();
    }

    static {
        effectList.add(CameraView.Effect.none);
        effectName.add("none");

        effectList.add(CameraView.Effect.mono);
        effectName.add("mono");

        effectList.add(CameraView.Effect.negative);
        effectName.add("negative");

        effectList.add(CameraView.Effect.solarize);
        effectName.add("solarize");


        effectList.add(CameraView.Effect.sepia);
        effectName.add("sepia");

        effectList.add(CameraView.Effect.posetrize);
        effectName.add("posetrize");

        effectList.add(CameraView.Effect.whiteboard);
        effectName.add("whiteboard");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermission();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_takephoto;
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_REQUEST);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_REQUEST);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_RECORD_AUDIO_REQUEST);
        }
    }

    protected void initView() {
        mCv = (CameraView) findViewById(R.id.sv);
        mCameraBt = (Button) findViewById(R.id.bt_photo);
        FocusImageView fiv = (FocusImageView) findViewById(R.id.focus_iv);
        mThumbnailIv = (ImageView) findViewById(R.id.thumbnail_iv);
        mAnimateIv = (ImageView) findViewById(R.id.animate_iv);
        mAnimateIv.setVisibility(View.GONE);
        mFlashIv = (ImageView) findViewById(R.id.flash_iv);
        mCv.setmFocusImageView(fiv);
        mCv.setAnimateIv(mAnimateIv);

        Spinner balanceSpinner = (Spinner) findViewById(R.id.spinner_balance);
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, effectName);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        balanceSpinner.setAdapter(stringArrayAdapter);
        balanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCv.setEffect(effectList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void initListeners() {
        mCameraBt.setOnClickListener(this);
        mThumbnailIv.setOnClickListener(this);
        findViewById(R.id.switch_iv).setOnClickListener(this);
        mFlashIv.setOnClickListener(this);
    }

    private void showThumbnail() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),this.getPackageName());
        File temp = new File(file, "temp");
        files = FileOperateUtil.listFiles(temp.getAbsolutePath(), ".jpeg");
        if (files != null && files.size() > 0) {
            mThumbnailIv.setVisibility(View.VISIBLE);
            ImageLoader.load(mThumbnailIv,files.get(0).getAbsolutePath(),dip2px(this, 60f), dip2px(this, 60f));
        } else {
            mThumbnailIv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showThumbnail();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "获得权限", Toast.LENGTH_SHORT).show();
                mCv.openCamera();
                mCv.startPreview(mCv.getHolder());
            } else {
                Toast.makeText(this, "权限被禁止", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.thumbnail_iv:
//                Intent intent = new Intent(TakePhotoActivity.this, SelectPicActivity.class);
//                intent.putExtra(SelectPicActivity.EXTRA_TYPE, SelectPicActivity.EXTRA_CAMERA);
//                ArrayList<String> list = new ArrayList<String>();
//                for (File file: files) {
//                    list.add(file.getAbsolutePath());
//                }
//                intent.putStringArrayListExtra(SelectPicActivity.EXTRA_FILE, list);
//                startActivity(intent);
                break;
            case R.id.bt_photo:
                mCameraBt.setClickable(false);
                mAnimateIv.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       mAnimateIv.setVisibility(View.GONE);
                    }
                },200);
                mCv.takePhoto(new OnPictureTakeListener() {
                    @Override
                    public void onPicTaking() {

                    }

                    @Override
                    public void onPicTakenend() {
                        mCameraBt.setClickable(true);
                        refreshThumbnail();
                    }

                    @Override
                    public void onAnimend() {

                    }
                });
                break;
            case R.id.switch_iv:
                mCv.switchCamera();
                break;
            case R.id.flash_iv:
                switch (mCv.getFlashMode()) {
                    case ON:
                        mFlashIv.setImageResource(R.drawable.btn_flash_off);
                        mCv.setmFlashMode(CameraView.FlashMode.OFF);
                        break;
                    case OFF:
                        mFlashIv.setImageResource(R.drawable.btn_flash_auto);
                        mCv.setmFlashMode(CameraView.FlashMode.AUTO);
                        break;
                    case AUTO:
                        mFlashIv.setImageResource(R.drawable.btn_flash_torch);
                        mCv.setmFlashMode(CameraView.FlashMode.TORCH);
                        break;
                    case TORCH:
                        mFlashIv.setImageResource(R.drawable.btn_flash_on);
                        mCv.setmFlashMode(CameraView.FlashMode.ON);
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void refreshThumbnail() {
        showThumbnail();
    }

}
