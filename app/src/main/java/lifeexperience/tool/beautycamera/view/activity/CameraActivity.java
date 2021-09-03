package lifeexperience.tool.beautycamera.view.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.helper.SavePictureTask;
import com.seu.magicfilter.widget.MagicCameraView;
import lifeexperience.tool.beautycamera.R;
import lifeexperience.tool.beautycamera.adapter.FilterAdapter;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static lifeexperience.tool.beautycamera.utils.FileUtils.getBitmapDegree;

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
    private int mode = MODE_PIC;
    private boolean isOpenFilter = false;
    private boolean isOpenBeauty = false;

    private ImageView btn_shutter;
    private ImageView btn_mode;

    private ObjectAnimator animator;
    private int mSensorRotation;
    private OrientationEventListener mOrientationListener;
    private TextView line1;
    private TextView line2;
    private TextView line3;
    private TextView line4;
    private ImageView btnSubline;
    private boolean isOpenSubline = true;
    private RelativeLayout rlCameraView;
    private LinearLayout ll;

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
     * 延时7秒
     */
    private static final int DELAY_SEVEN = 7;

    private static final int[] DELAY_ICON={R.drawable.ico_delay_none,R.drawable.ico_delay_three,R.drawable.ico_delay_seven};
    private static final int[] SIZE_ICON={R.drawable.ico_3_4,R.drawable.ico_9_16,R.drawable.ico_1_1};
    private static final int[] FlashLight_ICON={R.drawable.flash_light_closed,R.drawable.ico_flash_light_auto,R.drawable.ico_flash_light_open};
    private int DELAY_NUMBER = 0;
    private int SIZE_NUMBER = 0;
    private int FlashLight_NUMBER = 0;

    private ImageView btnDelay;
    private TextView txtDelayTime;
    private ImageView backHome;
    private ImageView btnSwitchCameraSize;
    private ImageView btnCameraFilter;
    private ImageView btnCameraBeauty;

    private Point screenSize;
    private RelativeLayout.LayoutParams params;
    private LinearLayout llBeautyLevel;
    private SeekBar seekBar;
    private TextView beauty;
    private TextView value;
    private ImageView btnFlashlight;

    private int SEEKBAR_VALUE = 0;
    private int Level = 0;
    private float ratio = 4/3f;


    private final MagicFilterType[] types = new MagicFilterType[]{
            MagicFilterType.NONE,
            MagicFilterType.FAIRYTALE,
            MagicFilterType.SUNSET,
            MagicFilterType.WHITECAT,
            MagicFilterType.BLACKCAT,
            MagicFilterType.HEALTHY,
            MagicFilterType.ROMANCE,
            MagicFilterType.SAKURA,
            MagicFilterType.ANTIQUE,
            MagicFilterType.LATTE,
            MagicFilterType.TENDER,
            MagicFilterType.COOL,
            MagicFilterType.EMERALD,
            MagicFilterType.EVERGREEN,
            MagicFilterType.AMARO,
            MagicFilterType.BROOKLYN,
            MagicFilterType.FREUD,
            MagicFilterType.HEFE,
            MagicFilterType.HUDSON,
            MagicFilterType.INKWELL,
            MagicFilterType.KEVIN,
            MagicFilterType.N1977,
            MagicFilterType.NASHVILLE,
            MagicFilterType.PIXAR,
            MagicFilterType.RISE,
            MagicFilterType.SIERRA,
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
        btnDelay = findViewById(R.id.btn_delay);
        txtDelayTime = findViewById(R.id.txt_delay_time);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);
        line4 = findViewById(R.id.line4);
        btnSubline = findViewById(R.id.btn_subline);
        rlCameraView = findViewById(R.id.rl_camera_view);
        ll = findViewById(R.id.ll);
        backHome = findViewById(R.id.btn_home);
        btnSwitchCameraSize = findViewById(R.id.btn_switch_camera_size);
        btnCameraFilter = findViewById(R.id.btn_camera_filter);
        btnCameraBeauty = findViewById(R.id.btn_camera_beauty);
        llBeautyLevel = findViewById(R.id.ll_beauty_level);
        seekBar = findViewById(R.id.sb_set_beauty_level);
        beauty = findViewById(R.id.tv_beauty);
        value = findViewById(R.id.tv_beauty_seekbar_value);
        btnFlashlight = findViewById(R.id.btn_flashlight);

        //滤镜列表
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFilterListView.setLayoutManager(linearLayoutManager);

        mAdapter = new FilterAdapter(this, types);
        mFilterListView.setAdapter(mAdapter);
        mAdapter.setOnFilterChangeListener(onFilterChangeListener);

        animator = ObjectAnimator.ofFloat(btn_shutter, "rotation", 0, 360);
        animator.setDuration(500);
        animator.setRepeatCount(ValueAnimator.INFINITE);

        //设置预览尺寸
        screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        cameraView = (MagicCameraView) findViewById(R.id.glsurfaceview_camera);
        params = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
        params.width = screenSize.x;
        params.height = screenSize.x * 4 / 3 ;//设置长宽比4:3
        cameraView.setLayoutParams(params);
    }

    @Override
    protected void initListeners() {
        findViewById(R.id.btn_camera_closefilter).setOnClickListener(listener);
        findViewById(R.id.btn_camera_shutter).setOnClickListener(listener);
        findViewById(R.id.btn_camera_switch).setOnClickListener(listener);
        btnCameraFilter.setOnClickListener(listener);
        btnCameraBeauty.setOnClickListener(listener);
        btnDelay.setOnClickListener(listener);
        btnSubline.setOnClickListener(listener);
        backHome.setOnClickListener(listener);
        btnSwitchCameraSize.setOnClickListener(listener);
        btnFlashlight.setOnClickListener(listener);

        //设置美颜度
        seekBar.setProgress(SEEKBAR_VALUE);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value.setText(String.valueOf(progress));
                SEEKBAR_VALUE = progress;
                if(progress!=0 && progress!=100){
                    Level = progress / 20 + 1;
                }else if(progress == 0){
                    Level = progress;
                }else {
                    Level = progress / 20;
                }
                magicEngine.setBeautyLevel(Level);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //监听手机的旋转角度
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
            takePhoto();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
    }

    /**
     * 显示结果
     */
    private void showResult(){
        magicEngine.switchFlashLight(FlashLight_NUMBER % FlashLight_ICON.length);
        magicEngine.savePicture(getOutputMediaFile(), new SavePictureTask.OnPictureSaveListener() {
            @Override
            public void onSaved(String result) {
                Log.d("======3333333", "onSaved: "+getBitmapDegree(result));
                //显示结果
                Bundle bundle = new Bundle();
                bundle.putString("path", result);
                startActivity(ShowResultActivity.class, bundle);
                findViewById(R.id.btn_camera_shutter).setOnClickListener(listener);
            }
        });
    }


    private void showFilters() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "alpha", 0,1);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
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
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "alpha", 1,0);
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
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub
                mFilterLayout.setVisibility(View.INVISIBLE);
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
                case R.id.btn_camera_shutter: {
                    if (PermissionChecker.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                v.getId());
                    } else {
                        takePhoto();
                    }
                    break;
                }
                case R.id.btn_camera_filter: {
                    if(!isOpenFilter){
                        llBeautyLevel.setVisibility(View.INVISIBLE);
                        isOpenBeauty = false;
                        showFilters();
                        isOpenFilter = true;
                    }else {
                        hideFilters();
                        isOpenFilter = false;
                    }
                    break;
                }
                case R.id.btn_camera_switch: {
                    magicEngine.switchCamera();
                    int cameraId = magicEngine.getCameraId();
                    if(cameraId == 0){
                        btnFlashlight.setVisibility(View.VISIBLE);
                    }else {
                        btnFlashlight.setVisibility(View.GONE);
                    }
                    break;
                }
                case R.id.btn_camera_beauty: {
                    if(!isOpenBeauty){
                        hideFilters();
                        isOpenFilter = false;
                        llBeautyLevel.setVisibility(View.VISIBLE);
                        isOpenBeauty = true;
                    }else {
                        llBeautyLevel.setVisibility(View.INVISIBLE);
                        isOpenBeauty = false;
                    }
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
                case R.id.btn_home: {
                    finish();
                    break;
                }
                case R.id.btn_flashlight: {
                    flashlightSwitch();
                    break;
                }
                case R.id.btn_switch_camera_size: {
                    switchCameraSize();
                    break;
                }
            }
        }

    };

    /**
     * 切换闪光灯
     */
    private void flashlightSwitch() {
        FlashLight_NUMBER += 1;
        magicEngine.switchFlashLight(FlashLight_NUMBER % FlashLight_ICON.length);
        btnFlashlight.setImageResource(FlashLight_ICON[FlashLight_NUMBER % FlashLight_ICON.length]);
    }


    /**
     * 切换显示尺寸
     */
    private void switchCameraSize(){
        SIZE_NUMBER += 1;
        switch (SIZE_NUMBER % SIZE_ICON.length) {
            case 0: {
                switchUI(false);
                beauty.setTextColor(getResources().getColor(R.color.white));
                value.setTextColor(getResources().getColor(R.color.white));
                params.height = screenSize.x * 4 / 3 ;//设置长宽比4:3
                ratio = 4/3f;
                break;
            }

            case 1: {
                switchUI(true);
                beauty.setTextColor(getResources().getColor(R.color.white));
                value.setTextColor(getResources().getColor(R.color.white));
                RelativeLayout.LayoutParams rlCameraViewParams = (RelativeLayout.LayoutParams) rlCameraView.getLayoutParams();
                params.height = rlCameraViewParams.height ;//设置长宽比16:9
                ratio = 16/9f;
                break;
            }

            case 2: {
                switchUI(false);
                beauty.setTextColor(getResources().getColor(R.color.black));
                value.setTextColor(getResources().getColor(R.color.black));
                params.height = screenSize.x;//设置长宽比1:1
                ratio = 1f;
                break;
            }
        }
        cameraView.setLayoutParams(params);
        cameraView.setRatio(ratio);
        btnSwitchCameraSize.setImageResource(SIZE_ICON[SIZE_NUMBER % SIZE_ICON.length]);
    }


    private void switchUI(boolean is9_16){
        if(is9_16){
            ll.setVisibility(View.GONE);
            btnCameraBeauty.setImageResource(R.drawable.ico_beauty_white);
            btnCameraFilter.setImageResource(R.drawable.ico_fliter_white);
        }else {
            ll.setVisibility(View.VISIBLE);
            btnCameraBeauty.setImageResource(R.drawable.ico_beauty);
            btnCameraFilter.setImageResource(R.drawable.ico_filter);
        }
    }


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
            btnSubline.setImageResource(R.drawable.ico_line_open);
        }else {
            line1.setVisibility(View.VISIBLE);
            line2.setVisibility(View.VISIBLE);
            line3.setVisibility(View.VISIBLE);
            line4.setVisibility(View.VISIBLE);
            isOpenSubline = true;
            btnSubline.setImageResource(R.drawable.ico_line_closed);
        }
    }


    /**
     * 延时拍摄
     */
    private void delayTake() {
        DELAY_NUMBER += 1;
        switch (DELAY_NUMBER % DELAY_ICON.length) {
            case 0: {
                mDelayTime = DELAY_ZERO;
                break;
            }

            case 1: {
                mDelayTime = DELAY_THREE;
                break;
            }

            case 2: {
                mDelayTime = DELAY_SEVEN;
                break;
            }

        }
        btnDelay.setImageResource(DELAY_ICON[DELAY_NUMBER % DELAY_ICON.length]);
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
