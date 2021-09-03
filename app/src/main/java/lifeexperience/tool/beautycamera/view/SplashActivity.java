package lifeexperience.tool.beautycamera.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import lifeexperience.tool.beautycamera.R;
import lifeexperience.tool.beautycamera.utils.SharedPreferencesUtil;
import lifeexperience.tool.beautycamera.utils.ToastUtil;
import lifeexperience.tool.beautycamera.view.activity.BaseActivity;

public class SplashActivity extends BaseActivity {

    private ImageView ivStartBackground;
    private TextView tvStart;
    private FrameLayout flAccept;
    private ImageView btnAccept;
    private TextView tvPrivacyPolicy;
    private WebView wvShowPrivacyPolicy;
    private LinearLayout llSplashPrivacyPolicy;
    private boolean isAccept = false;
    private boolean isShowWebView = false;
    private FrameLayout btnStart;
    private int mDelayTime = 1500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }


    protected void initView() {
        ivStartBackground = findViewById(R.id.iv_start_background);
        tvStart = findViewById(R.id.tv_start);
        flAccept = findViewById(R.id.fl_accept);
        btnAccept = findViewById(R.id.btn_accept);
        tvPrivacyPolicy = findViewById(R.id.tv_privacy_policy);
        wvShowPrivacyPolicy = findViewById(R.id.wv_show_privacy_policy);
        btnStart = findViewById(R.id.btn_start);
        llSplashPrivacyPolicy = findViewById(R.id.ll_splash_privacy_policy);

        boolean isAccept = SharedPreferencesUtil.getBoolean(SplashActivity.this, SharedPreferencesUtil.IS_ACCEPT_PRIVACY_POLICY,false);
        if (isAccept){
            acceptEntry();
        }
    }

    @Override
    protected void initListeners() {
        ivStartBackground.setOnClickListener(this);
        flAccept.setOnClickListener(this);
        tvPrivacyPolicy.setOnClickListener(this);
    }


    private void acceptEntry(){
        btnStart.setVisibility(View.GONE);
        llSplashPrivacyPolicy.setVisibility(View.GONE);
        // 定时器
        new CountDownTimer(mDelayTime, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                entry();
            }
        }.start();
    }
    /**
     *进入主页面
     */
    private void entry() {
        startActivity(MainActivity.class);
        finish();
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_start_background:{
                if (isAccept){
                    entry();
                    SharedPreferencesUtil.putBoolean(SplashActivity.this, SharedPreferencesUtil.IS_ACCEPT_PRIVACY_POLICY,true);
                }else {
                    ToastUtil.showShortToast(getResources().getString(R.string.toast_check_btn));
                }
                break;
            }
            case R.id.fl_accept:{
                if (isAccept){
                    btnAccept.setBackground(getResources().getDrawable(R.drawable.pri_policy_normal));
                    ivStartBackground.setBackground(getResources().getDrawable(R.drawable.shape_rect_start_normal));
                    tvStart.setTextColor(getResources().getColor(R.color.white));
                    isAccept = false;
                }else {
                    btnAccept.setBackground(getResources().getDrawable(R.drawable.pri_policy_select));
                    ivStartBackground.setBackground(getResources().getDrawable(R.drawable.shape_rect_start_select));
                    tvStart.setTextColor(getResources().getColor(R.color.theme_color));
                    isAccept = true;
                }
                break;

            }
            case R.id.tv_privacy_policy:{
                if (!isShowWebView){
                    isShowWebView = true;
                    String url = "file:///android_asset/" + "privacypolicy.html";
                    wvShowPrivacyPolicy.setVisibility(View.VISIBLE);
                    wvShowPrivacyPolicy.loadUrl(url);
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isShowWebView){
            isShowWebView = false;
            wvShowPrivacyPolicy.setVisibility(View.GONE);
        }else {
            finish();
        }
    }
}