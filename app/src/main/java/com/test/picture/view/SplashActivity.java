package com.test.picture.view;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.test.picture.R;
import com.test.picture.view.activity.BaseActivity;

public class SplashActivity extends BaseActivity {

    private ImageView mImageView;
    private static final int DURATION_MILLIS = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }


    protected void initView() {
        mImageView = findViewById(R.id.splash_logo);
    }

    @Override
    protected void initListeners() {
        setAnim();
    }


    /**
     * 设置过场动画
     */
    private void setAnim() {
        AlphaAnimation anim = new AlphaAnimation(1.0F, 1.0F);

        anim.setFillAfter(true);
        anim.setDuration(DURATION_MILLIS);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //在这里做一些初始化的操作
                //跳转到指定的Activity
                startActivity(MainActivity.class);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mImageView.startAnimation(anim);
    }


    @Override
    public void onClick(View v) {

    }
}