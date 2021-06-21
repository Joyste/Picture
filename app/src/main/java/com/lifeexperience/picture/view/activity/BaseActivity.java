package com.lifeexperience.picture.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {


    private  boolean isSetFullScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen(isSetFullScreen);
        setContentView(getLayoutId());
        initView();
        initListeners();
        initData();
    }


    /**
     * 绑定界面
     */
    protected abstract int getLayoutId();

    /**
     * 控件初始化
     */
    protected abstract void initView();

    /**
     * 监听初始化
     */
    protected abstract void initListeners();

    /**
     * 数据初始化
     */
    protected void initData(){

    }

    /**
     * 请求数据
     */
    protected void getData(){

    };

    public void finishActivity() {
        finish();
    }

    /**
     * [页面跳转]
     *
     * @param clz
     */
    public void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }
    /**
     * [携带数据的页面跳转]
     *
     * @param clz
     * @param bundle
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
    /**
     * [含有Bundle通过Class打开编辑界面]
     *
     * @param cls
     * @param bundle
     * @param requestCode
     */
    public void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 设置全屏
     */
    protected void setFullScreen(Boolean isSetFullScreen) {
        if(isSetFullScreen){
            //设置全屏
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

    }


}
