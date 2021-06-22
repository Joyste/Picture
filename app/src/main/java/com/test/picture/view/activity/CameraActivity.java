package com.test.picture.view.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.test.picture.R;
import com.test.picture.utils.BrightnessUtil;

public class CameraActivity extends BaseActivity {


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

    private CameraActivityHelper cameraActivityHelper;
    private TextureView textureView;
    private ImageButton btnTakePicture;
    private ImageView ivExchangeCamera;
    private Button btnDelay;
    private TextView txtDelayTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);//硬件加速
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持常亮
        setFullScreen(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initView() {

        textureView = findViewById(R.id.textureView);
        btnTakePicture = findViewById(R.id.btnTakePic);
        ivExchangeCamera = findViewById(R.id.ivExchange);
        btnDelay = findViewById(R.id.btn_delay);
        txtDelayTime = findViewById(R.id.txt_delay_time);
        cameraActivityHelper = new CameraActivityHelper(this, textureView);
        initBrightness();
    }

    @Override
    protected void initListeners() {
        btnTakePicture.setOnClickListener(this);
        ivExchangeCamera.setOnClickListener(this);
        btnDelay.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTakePic:{
                takePicture();
                break;
            }
            case R.id.ivExchange: {
                cameraActivityHelper.exchangeCamera();
                break;
            }
            case R.id.btn_delay:{
                delayTake();
                break;
            }
        }
    }

    /**
     * 初始化屏幕亮度，不到200自动调整到200
     */
    private void initBrightness() {
        int brightness = BrightnessUtil.getScreenBrightness(this);
        if (brightness < 200) {
            BrightnessUtil.setBrightness(this, 200);
        }
    }

    /**
     * 延时拍摄
     */
    private void delayTake() {
        DELAY_NUMBER += 1;
        switch (DELAY_NUMBER % 4) {
            case 0:{
                mDelayTime = DELAY_ZERO;
                break;
            }

            case 1:{
                mDelayTime = DELAY_THREE;
                break;
            }

            case 2:{
                mDelayTime = DELAY_FIVE;
                break;
            }

            case 3:{
                mDelayTime = DELAY_TEN;
                break;
            }

        }
        btnDelay.setText(mDelayTime + R.string.second);
    }

    /**
     * 拍照＋判定延时
     */
    private void takePicture() {
        //设置拍照按键不可点
        btnTakePicture.setOnClickListener(null);

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
                    cameraActivityHelper.takePic();
                }
            }.start();
        } else {
            cameraActivityHelper.takePic();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraActivityHelper.releaseThread();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //重新初始化
        cameraActivityHelper.reInitCameraInfo();
        //恢复监听
        initListeners();
    }


}
