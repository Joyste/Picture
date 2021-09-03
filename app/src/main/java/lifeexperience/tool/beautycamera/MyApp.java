package lifeexperience.tool.beautycamera;

import android.app.Application;
import android.content.Context;
import android.util.Log;


import androidx.multidex.MultiDex;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lifeexperience.tool.beautycamera.http.MyCallBack;
import lifeexperience.tool.beautycamera.http.OkHttpUtils;
import lifeexperience.tool.beautycamera.model.AppInfo;
import lifeexperience.tool.beautycamera.utils.OperateJson;
import lifeexperience.tool.beautycamera.utils.ReflectUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class MyApp extends Application {



    public static MyApp instance;
    private static Context context;
    private static boolean isAppListNull = true;

    public static MyApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = getApplicationContext();
        MultiDex.install(this);
        appListIsNull();
    }


    public static boolean getIsAppListNull() {
        return isAppListNull;
    }

    public static void setIsAppListNull(boolean isAppListNull) {
        MyApp.isAppListNull = isAppListNull;
    }

    public static Context getContext() {
        return context;
    }


    public static void appListIsNull(){
        OkHttpUtils.getInstance().Get("http://staticoss.lifeexperiencelife.xyz/lfmcplayer/static/apps/produtlist.json", new MyCallBack<String>() {
            @Override
            public void onLoadingBefore(Request request) {

            }

            @Override
            public void onSuccess(Response response, String result) {
                try {
                    JSONObject jsonObj = OperateJson.ReadJsonString(result);
                    JSONArray product = jsonObj.getJSONArray("apps");
                    List<AppInfo> newList = new ArrayList<>();
                    newList = ReflectUtil.convertToList(product, AppInfo.class);
                    if(newList.size() == 0){
                        isAppListNull = true;
                    }else {
                        isAppListNull = false;
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
}
