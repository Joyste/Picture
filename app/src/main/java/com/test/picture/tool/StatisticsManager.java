package com.test.picture.tool;

import android.content.Context;
import android.os.Bundle;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.math.BigDecimal;
import java.util.Currency;


/**
 * 统计管理器 1:第三方界面统计 2:网络流量统计
 */
public class StatisticsManager
{
    static FirebaseAnalytics mFirebaseAnalytics;


    /**
     * 测试
     * @return
     */
    //jty123123
    public static boolean isTest(){
        return false;
    }


    /**
     * 对特定业务统计
     */
    public static void uploadAnalyticsEvents(Context context, String eventsType)
    {
        if(isTest()){
            return;
        }
        //firebase统计
        try {
            if (mFirebaseAnalytics == null) {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            }
            if (mFirebaseAnalytics != null) {
       
                Bundle params = new Bundle();
                mFirebaseAnalytics.logEvent(eventsType, params);
            }
        }
        catch (Exception e) {
        }
    }

    /**
     * 上传视图位置
     *
     * @param context
     * @param viewName
     */
    public static void uploadAnalyticsView(Context context, String viewName)
    {
        if(isTest()){
            return;
        }
        //firebase统计
        try {
            if (mFirebaseAnalytics == null) {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            }
            if (mFirebaseAnalytics != null) {
                
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, viewName);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
            }
        }
        catch (Exception e) {
        }

    }

}
