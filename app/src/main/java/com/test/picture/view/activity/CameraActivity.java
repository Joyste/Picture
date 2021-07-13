package com.test.picture.view.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.OrientationEventListener;
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


import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.helper.SavePictureTask;
import com.seu.magicfilter.utils.MagicParams;
import com.seu.magicfilter.widget.MagicCameraView;
import com.test.picture.R;
import com.test.picture.adapter.FilterAdapter;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.test.picture.MyApp.getContext;

/**
 * Created by why8222 on 2016/3/17.
 */
public class CameraActivity extends BaseActivity {
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
    private OrientationEventListener mOrientationListener;
    private TextView line1;
    private TextView line2;
    private TextView line3;
    private TextView line4;
    private Button btnSubline;
    private boolean isOpenSubline = true;


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
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);
        line4 = findViewById(R.id.line4);
        btnSubline = findViewById(R.id.btn_subline);

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
        findViewById(R.id.btn_camera_filter).setOnClickListener(listener);
        findViewById(R.id.btn_camera_closefilter).setOnClickListener(listener);
        findViewById(R.id.btn_camera_shutter).setOnClickListener(listener);
        findViewById(R.id.btn_camera_switch).setOnClickListener(listener);
        findViewById(R.id.btn_camera_mode).setOnClickListener(listener);
        findViewById(R.id.btn_camera_beauty).setOnClickListener(listener);
        btnDelay.setOnClickListener(listener);
        btnSubline.setOnClickListener(listener);

        mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    return;  //手机平放时，检测不到有效的角度
                }
//只检测是否有四个角度的改变
                if (orientation > 350 || orientation < 10) { //0度
                    orientation = 0;
                } else if (orientation > 80 && orientation < 100) { //90度
                    orientation = 90;
                } else if (orientation > 170 && orientation < 190) { //180度
                    orientation = 180;
                } else if (orientation > 260 && orientation < 280) { //270度
                    orientation = 270;
                } else {
                    return;
                }
                Log.i("MyOrientationDetector ", "onOrientationChanged:" + orientation);

                cameraView.setmSensorRotation(orientation);
            }
        };

        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        } else {
            mOrientationListener.disable();
        }

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
            if (mode == MODE_PIC){
                takePhoto();
            }

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
        //提示音
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
        r.play();

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
                findViewById(R.id.btn_camera_shutter).setOnClickListener(listener);
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
    private View.OnClickListener listener = new View.OnClickListener() {
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
                case R.id.btn_camera_beauty: {
                    new AlertDialog.Builder(CameraActivity.this)
                            .setSingleChoiceItems(new String[]{"关闭", "1级", "2级", "3级", "4级", "5级"}, MagicParams.beautyLevel,
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
                case R.id.btn_camera_closefilter: {
                    hideFilters();
                    break;
                }
                case R.id.btn_delay: {
                    delayTake();
                    break;
                }
                case R.id.btn_subline: {
                    switchSubline();
                    break;
                }
            }
        }


    };

    /**
     * 开关辅助线
     */
    private void switchSubline() {
        if(isOpenSubline){
            line1.setVisibility(View.INVISIBLE);
            line2.setVisibility(View.INVISIBLE);
            line3.setVisibility(View.INVISIBLE);
            line4.setVisibility(View.INVISIBLE);
            isOpenSubline = false;
            btnSubline.setText("辅助线(开)");
        }else {
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
            line3.setVisibility(View.VISIBLE);
            line4.setVisibility(View.VISIBLE);
            isOpenSubline = true;
            btnSubline.setText("辅助线(关)");
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
        mOrientationListener.disable();
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

        }
    }

    @Override
    public void onClick(View v) {

    }
}
