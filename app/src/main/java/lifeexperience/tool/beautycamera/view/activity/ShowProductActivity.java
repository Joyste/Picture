package lifeexperience.tool.beautycamera.view.activity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lifeexperience.tool.beautycamera.R;
import lifeexperience.tool.beautycamera.adapter.AppListAdapter;
import lifeexperience.tool.beautycamera.http.MyCallBack;
import lifeexperience.tool.beautycamera.http.OkHttpUtils;
import lifeexperience.tool.beautycamera.model.AppInfo;
import lifeexperience.tool.beautycamera.utils.OperateJson;
import lifeexperience.tool.beautycamera.utils.ReflectUtil;
import lifeexperience.tool.beautycamera.utils.ToastUtil;
import lifeexperience.tool.beautycamera.view.MainActivity;


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
    }

    @Override
    protected void initListeners() {
        btnBack.setOnClickListener(this);
    }

    public void getAppData(){

          OkHttpUtils.getInstance().Get("http://staticoss.lifeexperiencelife.xyz/lfmcplayer/static/apps/produtlist.json", new MyCallBack<String>() {
              @Override
              public void onLoadingBefore(Request request) {

              }

              @Override
              public void onSuccess(Response response, String result) {
                  try {
                      Log.d("JSONObject-success",result);
                      JSONObject jsonObj = OperateJson.ReadJsonString(result);
                      JSONArray product = jsonObj.getJSONArray("apps");
                      List<AppInfo> newList = new ArrayList<>();
                      newList = ReflectUtil.convertToList(product, AppInfo.class);
                      appInfos.clear();
                      appInfos.addAll(newList);

                      if(appInfos.size()>0){
                          appListAdapter = new AppListAdapter(ShowProductActivity.this, appInfos);
                          appList.setAdapter(appListAdapter);
                      }else {
                          startActivity(MainActivity.class);
                          ToastUtil.showShortToast("No application");
                      }

                  } catch (Exception e) {
                      e.printStackTrace();
                  }
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