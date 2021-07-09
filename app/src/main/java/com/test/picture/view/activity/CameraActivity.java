package com.test.picture.view.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.huantansheng.easyphotos.EasyPhotos;
import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.camera.CameraEngine;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.helper.SavePictureTask;
import com.seu.magicfilter.utils.MagicParams;
import com.seu.magicfilter.widget.MagicCameraView;
import com.seu.magicfilter.widget.base.MagicBaseView;
import com.test.picture.R;
import com.test.picture.adapter.FilterAdapter;
import com.test.picture.utils.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.seu.magicfilter.camera.CameraEngine.releaseCamera;
import static com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils.getViewBitmap;

/**
 * Created by why8222 on 2016/3/17.
 */
public class CameraActivity extends BaseActivity implements SensorEventListener {
    private LinearLayout mFilterLayout;
    private RecyclerView mFilterListView;
    private FilterAdapter mAdapter;
    private MagicEngine magicEngine;
    private MagicCameraView cameraView;
    private boolean isRecording = false;
    private final int MODE_PIC = 1;
    private final int MODE_VIDEO = 2;
    private int mode = MODE_PIC;

    private ImageView btn_shutter;
    private ImageView btn_mode;

    private ObjectAnimator animator;
    private int mSensorRotation;

    /**
     * 延时秒数
     */
    private int mDelayTime = DELAY_ZERO;
    /**
     * 无延时
     */
    private static final int DELAY_ZERO = 0;
    /**
     * 延时3秒
     */
    private static final int DELAY_THREE = 3;
    /**
     * 延时5秒
     */
    private static final int DELAY_FIVE = 5;
    /**
     * 延时10秒
     */
    private static final int DELAY_TEN = 10;

    private int DELAY_NUMBER = 0;
    private Button btnDelay;
    private TextView txtDelayTime;


    private final MagicFilterType[] types = new MagicFilterType[]{
            MagicFilterType.NONE,
            MagicFilterType.FAIRYTALE,
            MagicFilterType.SUNRISE,
            MagicFilterType.SUNSET,
            MagicFilterType.WHITECAT,
            MagicFilterType.BLACKCAT,
            MagicFilterType.SKINWHITEN,
            MagicFilterType.HEALTHY,
            MagicFilterType.SWEETS,
            MagicFilterType.ROMANCE,
            MagicFilterType.SAKURA,
            MagicFilterType.WARM,
            MagicFilterType.ANTIQUE,
            MagicFilterType.NOSTALGIA,
            MagicFilterType.CALM,
            MagicFilterType.LATTE,
            MagicFilterType.TENDER,
            MagicFilterType.COOL,
            MagicFilterType.EMERALD,
            MagicFilterType.EVERGREEN,
            MagicFilterType.CRAYON,
            MagicFilterType.SKETCH,
            MagicFilterType.AMARO,
            MagicFilterType.BRANNAN,
            MagicFilterType.BROOKLYN,
            MagicFilterType.EARLYBIRD,
            MagicFilterType.FREUD,
            MagicFilterType.HEFE,
            MagicFilterType.HUDSON,
            MagicFilterType.INKWELL,
            MagicFilterType.KEVIN,
            MagicFilterType.LOMO,
            MagicFilterType.N1977,
            MagicFilterType.NASHVILLE,
            MagicFilterType.PIXAR,
            MagicFilterType.RISE,
            MagicFilterType.SIERRA,
            MagicFilterType.SUTRO,
            MagicFilterType.TOASTER2,
            MagicFilterType.VALENCIA,
            MagicFilterType.WALDEN,
            MagicFilterType.XPROII
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MagicEngine.Builder builder = new MagicEngine.Builder();
        magicEngine = builder.build((MagicCameraView) findViewById(R.id.glsurfaceview_camera));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera;
    }

    protected void initView() {
        mFilterLayout = (LinearLayout) findViewById(R.id.layout_filter);
        mFilterListView = (RecyclerView) findViewById(R.id.filter_listView);
        btn_shutter = (ImageView) findViewById(R.id.btn_camera_shutter);
        btn_mode = (ImageView) findViewById(R.id.btn_camera_mode);
        btnDelay = findViewById(R.id.btn_delay);
        txtDelayTime = findViewById(R.id.txt_delay_time);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFilterListView.setLayoutManager(linearLayoutManager);

        mAdapter = new FilterAdapter(this, types);
        mFilterListView.setAdapter(mAdapter);
        mAdapter.setOnFilterChangeListener(onFilterChangeListener);

        animator = ObjectAnimator.ofFloat(btn_shutter, "rotation", 0, 360);
        animator.setDuration(500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        cameraView = (MagicCameraView) findViewById(R.id.glsurfaceview_camera);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
        params.width = screenSize.x;
        params.height = screenSize.x * 4 / 3;
        cameraView.setLayoutParams(params);
    }

    @Override
    protected void initListeners() {
        findViewById(R.id.btn_camera_filter).setOnClickListener(this);
        findViewById(R.id.btn_camera_closefilter).setOnClickListener(this);
        findViewById(R.id.btn_camera_shutter).setOnClickListener(this);
        findViewById(R.id.btn_camera_switch).setOnClickListener(this);
        findViewById(R.id.btn_camera_mode).setOnClickListener(this);
        findViewById(R.id.btn_camera_beauty).setOnClickListener(this);
        btnDelay.setOnClickListener(this);
    }

    private FilterAdapter.onFilterChangeListener onFilterChangeListener = new FilterAdapter.onFilterChangeListener() {

        @Override
        public void onFilterChanged(MagicFilterType filterType) {
            magicEngine.setFilter(filterType);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (grantResults.length != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (mode == MODE_PIC)
                takePhoto();
            else
                takeVideo();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void switchMode() {
        if (mode == MODE_PIC) {
            mode = MODE_VIDEO;
            btn_mode.setImageResource(R.drawable.icon_camera);
        } else {
            mode = MODE_PIC;
            btn_mode.setImageResource(R.drawable.icon_video);
        }
    }

    private void takePhoto() {
        //设置拍照按键不可点
        findViewById(R.id.btn_camera_shutter).setOnClickListener(null);

        if (mDelayTime != DELAY_ZERO) {
            // 定时器
            new CountDownTimer(mDelayTime * 1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    txtDelayTime.setVisibility(View.VISIBLE);
                    txtDelayTime.setText(String.valueOf(millisUntilFinished / 1000 + 1));
                }

                @Override
                public void onFinish() {
                    txtDelayTime.setVisibility(View.GONE);
                    showResult();
                }
            }.start();
        } else {
            showResult();
        }

        findViewById(R.id.btn_camera_shutter).setOnClickListener(this);

    }

    /**
     * 显示结果
     */
    private void showResult(){
        magicEngine.savePicture(getOutputMediaFile(), new SavePictureTask.OnPictureSaveListener() {
            @Override
            public void onSaved(String result) {
                //显示结果
                Bundle bundle = new Bundle();
                bundle.putString("path", result);
                startActivity(ShowResultActivity.class, bundle);
            }
        });
    }

    private void takeVideo() {
        if (isRecording) {
            animator.end();
            magicEngine.stopRecord();
        } else {
            animator.start();
            magicEngine.startRecord();
        }
        isRecording = !isRecording;
    }

    private void showFilters() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", mFilterLayout.getHeight(), 0);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                findViewById(R.id.btn_camera_shutter).setClickable(false);
                mFilterLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        animator.start();
    }

    private void hideFilters() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", 0, mFilterLayout.getHeight());
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                mFilterLayout.setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_camera_shutter).setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub
                mFilterLayout.setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_camera_shutter).setClickable(true);
            }
        });
        animator.start();
    }

    public File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Picture");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera_mode: {
                switchMode();
                break;
            }
            case R.id.btn_camera_shutter: {
                if (PermissionChecker.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            v.getId());
                } else {
                    if (mode == MODE_PIC)
                        takePhoto();
                    else
                        takeVideo();
                }
                break;
            }
            case R.id.btn_camera_filter: {
                showFilters();
                break;
            }
            case R.id.btn_camera_switch: {
                magicEngine.switchCamera();
                break;
            }
            case R.id.btn_camera_beauty:{
                new AlertDialog.Builder(CameraActivity.this)
                        .setSingleChoiceItems(new String[]{"关闭", "1", "2", "3", "4", "5"}, MagicParams.beautyLevel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        magicEngine.setBeautyLevel(which);
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            }
            case R.id.btn_camera_closefilter:{
                hideFilters();
                break;
            }
            case R.id.btn_delay: {
                delayTake();
                break;
            }
        }
    }

    /**
     * 延时拍摄
     */
    private void delayTake() {
        DELAY_NUMBER += 1;
        switch (DELAY_NUMBER % 4) {
            case 0: {
                mDelayTime = DELAY_ZERO;
                break;
            }

            case 1: {
                mDelayTime = DELAY_THREE;
                break;
            }

            case 2: {
                mDelayTime = DELAY_FIVE;
                break;
            }

            case 3: {
                mDelayTime = DELAY_TEN;
                break;
            }

        }
        btnDelay.setText(String.valueOf(mDelayTime)+"s");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        //手机移动一段时间后静止，然后静止一段时间后进行对焦
        // 读取加速度传感器数值，values数组0,1,2分别对应x,y,z轴的加速度
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];

        }

        mSensorRotation = calculateSensorRotation(event.values[0],event.values[1]);
        cameraView.setmSensorRotation(mSensorRotation);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public  int calculateSensorRotation(float x, float y) {
        //x是values[0]的值，X轴方向加速度，从左侧向右侧移动，values[0]为负值；从右向左移动，values[0]为正值
        //y是values[1]的值，Y轴方向加速度，从上到下移动，values[1]为负值；从下往上移动，values[1]为正值
        //不考虑Z轴上的数据，
        if (Math.abs(x) > 6 && Math.abs(y) < 4) {
            if (x > 6) {
                return 270;
            } else {
                return 90;
            }
        } else if (Math.abs(y) > 6 && Math.abs(x) < 4) {
            if (y > 6) {
                return 0;
            } else {
                return 180;
            }
        }

        return -1;
    }

}
