package com.test.picture.view.activity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.test.picture.R;
import com.test.picture.adapter.AppListAdapter;
import com.test.picture.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class ShowProductActivity extends BaseActivity {
    private Button btnBack;
    private RecyclerView appList;
    private AppListAdapter appListAdapter;
    private List<AppInfo> appInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_product;
    }

    @Override
    protected void initView() {
        btnBack = findViewById(R.id.btn_back);
        appList = findViewById(R.id.app_list);

        appList.setHasFixedSize(false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        appList.setLayoutManager(gridLayoutManager);

        getAppData();

        appListAdapter = new AppListAdapter(this, appInfos);
        appList.setAdapter(appListAdapter);
    }

    @Override
    protected void initListeners() {
        btnBack.setOnClickListener(this);
    }

    private void getAppData(){
        AppInfo appInfo1 = new AppInfo();
        AppInfo appInfo2 = new AppInfo();

        appInfo1.setApp_img("https://img1.baidu.com/it/u=2480604110,4008147240&fm=26&fmt=auto&gp=0.jpg");
        appInfo1.setApp_title("音乐");
        appInfo1.setApp_isNew(1);
        appInfo1.setApp_packet("media.mp3player.musicplayer");

        appInfo2.setApp_img("https://img1.baidu.com/it/u=2480604110,4008147240&fm=26&fmt=auto&gp=0.jpg");
        appInfo2.setApp_title("天气");
        appInfo2.setApp_isNew(1);
        appInfo2.setApp_packet("weather.forecast.alerts");

        appInfos.add(appInfo1);
        appInfos.add(appInfo2);
        appInfos.add(appInfo1);
        appInfos.add(appInfo2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back : {
                finish();
                break;
            }
        }
    }
}