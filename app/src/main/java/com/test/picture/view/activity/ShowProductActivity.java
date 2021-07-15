package com.test.picture.view.activity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.test.picture.R;
import com.test.picture.adapter.AppListAdapter;
import com.test.picture.http.MyCallBack;
import com.test.picture.http.OkHttpUtils;
import com.test.picture.model.AppInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class ShowProductActivity extends BaseActivity {
    private ImageView btnBack;
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

          OkHttpUtils.getInstance().Get("http://staticoss.lifeexperiencelife.xyz/lfmcplayer/static/apps/produtlist.json", new MyCallBack<String>() {
              @Override
              public void onLoadingBefore(Request request) {

              }

              @Override
              public void onSuccess(Response response, String result) {
                  Log.d("JSONObject",result);
              }

              @Override
              public void onFailure(Request request, Exception e) {
                  Log.d("JSONObject",e.toString());
              }

              @Override
              public void onError(Response response) {
                  Log.d("JSONObject",response.toString());
              }
          });


        AppInfo appInfo1 = new AppInfo();
        AppInfo appInfo2 = new AppInfo();

        appInfo1.setIcoUrl("https://img1.baidu.com/it/u=2480604110,4008147240&fm=26&fmt=auto&gp=0.jpg");
        appInfo1.setName("音乐");
        appInfo1.setIsNew(1);
        appInfo1.setLink("media.mp3player.musicplayer");

        appInfo2.setIcoUrl("https://img1.baidu.com/it/u=2480604110,4008147240&fm=26&fmt=auto&gp=0.jpg");
        appInfo2.setName("天气");
        appInfo2.setIsNew(1);
        appInfo2.setLink("weather.forecast.alerts");

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